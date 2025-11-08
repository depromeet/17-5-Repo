package com.ogd.stockdiary.application.batch;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import com.ogd.stockdiary.domain.fileclient.port.out.FileClientPort;
import com.ogd.stockdiary.domain.stock.entity.Stock;
import com.ogd.stockdiary.domain.stock.repository.StockRepository;

/**
 * Stock logo 파일 업로드 배치
 *
 * <p>
 * 로컬 폴더의 로고 파일들을 스토리지에 업로드하고 DB에 ObjectKey를 저장합니다.
 * 파일명이 주식 코드와 매칭되면 업로드합니다.
 *
 * <p>
 * 실행 방법: application.yml에서 batch.stock-logo-upload.enabled=true 설정
 */
@Component
@ConditionalOnProperty(name = "batch.stock-logo-upload.enabled", havingValue = "true")
public class StockLogoUploadBatch implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(StockLogoUploadBatch.class);
    private static final String LOGO_PREFIX = "stock/logo/";
    private static final String LOCAL_LOGO_PATH = "/Users/joonheelee/Downloads/로고";

    private final FileClientPort fileClientPort;
    private final StockRepository stockRepository;
    private final ApplicationContext applicationContext;
    private final TransactionTemplate transactionTemplate;

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${cloud.storage.endpoint}")
    private String storageEndpoint;

    @Value("${cloud.storage.bucket}")
    private String storageBucket;

    public StockLogoUploadBatch(FileClientPort fileClientPort, StockRepository stockRepository,
        ApplicationContext applicationContext, TransactionTemplate transactionTemplate) {
        this.fileClientPort = fileClientPort;
        this.stockRepository = stockRepository;
        this.applicationContext = applicationContext;
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    public void run(String... args) {
        logger.info("========================================");
        logger.info("Starting Stock Logo Upload Batch...");
        logger.info("========================================");
        logger.info("Database URL: {}", dbUrl);
        logger.info("Database Username: {}", dbUsername);
        logger.info("Storage Endpoint: {}", storageEndpoint);
        logger.info("Storage Bucket: {}", storageBucket);
        logger.info("Local Logo Path: {}", LOCAL_LOGO_PATH);
        logger.info("========================================");

        try {
            File logoDir = new File(LOCAL_LOGO_PATH);

            if (!logoDir.exists() || !logoDir.isDirectory()) {
                logger.error("Logo directory does not exist: {}", LOCAL_LOGO_PATH);
                System.exit(SpringApplication.exit(applicationContext, () -> 1));
                return;
            }

            File[] files = logoDir.listFiles();
            if (files == null || files.length == 0) {
                logger.warn("No files found in directory: {}", LOCAL_LOGO_PATH);
                System.exit(SpringApplication.exit(applicationContext, () -> 0));
                return;
            }

            logger.info("Found {} files in local directory", files.length);

            int uploadedCount = 0;
            int notFoundCount = 0;
            int skippedCount = 0;

            for (File file : files) {
                if (file.isDirectory()) {
                    logger.debug("Skipping directory: {}", file.getName());
                    skippedCount++;
                    continue;
                }

                String fileName = file.getName();
                String stockCode = extractStockCode(fileName);

                if (stockCode == null) {
                    logger.debug("Cannot extract stock code from file: {}", fileName);
                    skippedCount++;
                    continue;
                }

                logger.debug("Processing file: {} -> stock code: {}", fileName, stockCode);

                // DB에서 Stock 조회
                Stock stock = stockRepository.findByCode(stockCode);

                if (stock == null) {
                    logger.warn("Stock not found in DB for code: {} (file: {})", stockCode, fileName);
                    notFoundCount++;
                    continue;
                }

                // ObjectKey 생성: stock/logo/{code}/{filename}
                String objectKey = LOGO_PREFIX + stockCode + "/" + fileName;

                try {
                    // 스토리지에 파일 업로드
                    uploadFileToStorage(file, objectKey);
                    logger.info("Uploaded file to storage: {}", objectKey);

                    // DB 업데이트 (트랜잭션)
                    String oldLogo = stock.getLogo();
                    transactionTemplate.execute(status -> {
                        stock.updateLogo(objectKey);
                        stockRepository.save(stock);
                        return null;
                    });

                    logger.info("Updated stock logo in DB: code={}, old={}, new={}",
                        stockCode, oldLogo, objectKey);
                    uploadedCount++;

                } catch (Exception e) {
                    logger.error("Failed to upload file: {} for stock code: {}", fileName, stockCode, e);
                    skippedCount++;
                }
            }

            logger.info("Stock Logo Upload Batch completed. " +
                "Uploaded: {}, Not Found: {}, Skipped: {}",
                uploadedCount, notFoundCount, skippedCount);

        } catch (Exception e) {
            logger.error("Error occurred during Stock Logo Upload Batch", e);
            System.exit(SpringApplication.exit(applicationContext, () -> 1));
        }

        // 배치 성공 후 애플리케이션 종료
        logger.info("Shutting down application after batch completion...");
        System.exit(SpringApplication.exit(applicationContext, () -> 0));
    }

    /**
     * 파일을 스토리지에 업로드
     *
     * @param file
     *            업로드할 파일
     * @param objectKey
     *            저장할 ObjectKey
     * @throws IOException
     *             파일 읽기 실패 시
     */
    private void uploadFileToStorage(File file, String objectKey) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            long fileSize = file.length();
            fileClientPort.uploadFile(fis, objectKey, fileSize);
        }
    }

    /**
     * 파일명에서 stock code 추출
     *
     * <p>
     * 파일명이 숫자로 시작하면 숫자 부분을 stock code로 간주
     * 예: "005930.png" -> "005930"
     * 예: "AAPL.png" -> "AAPL"
     * 예: "TSLA@2x.png" -> "TSLA"
     *
     * @param fileName
     *            파일명
     * @return stock code 또는 null
     */
    private String extractStockCode(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return null;
        }

        // 확장자 제거
        int dotIndex = fileName.lastIndexOf('.');
        String nameWithoutExt = dotIndex > 0 ? fileName.substring(0, dotIndex) : fileName;

        // @2x 같은 suffix 제거
        int atIndex = nameWithoutExt.indexOf('@');
        if (atIndex > 0) {
            nameWithoutExt = nameWithoutExt.substring(0, atIndex);
        }

        // 숫자 또는 영문자로만 구성되어 있는지 확인
        if (nameWithoutExt.matches("^[A-Za-z0-9]+$")) {
            return nameWithoutExt;
        }

        return null;
    }
}
