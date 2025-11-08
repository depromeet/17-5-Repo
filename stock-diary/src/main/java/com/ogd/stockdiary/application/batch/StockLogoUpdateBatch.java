package com.ogd.stockdiary.application.batch;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
import com.ogd.stockdiary.domain.fileclient.port.out.FileClientPort.ObjectInfo;
import com.ogd.stockdiary.domain.stock.entity.Stock;
import com.ogd.stockdiary.domain.stock.repository.StockRepository;

/**
 * Stock logo ObjectKey 업데이트 배치
 *
 * <p>
 * storage의 stock/logo/{code}/{filename} 구조에서 파일을 읽어서
 * Stock 엔티티의 logo 필드에 ObjectKey를 업데이트합니다.
 *
 * <p>
 * 실행 방법: application.yml에서 batch.stock-logo-update.enabled=true 설정
 */
@Component
@ConditionalOnProperty(name = "batch.stock-logo-update.enabled", havingValue = "true")
public class StockLogoUpdateBatch implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(StockLogoUpdateBatch.class);
    private static final String LOGO_PREFIX = "stock/logo/";

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

    public StockLogoUpdateBatch(FileClientPort fileClientPort, StockRepository stockRepository,
        ApplicationContext applicationContext, TransactionTemplate transactionTemplate) {
        this.fileClientPort = fileClientPort;
        this.stockRepository = stockRepository;
        this.applicationContext = applicationContext;
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    public void run(String... args) {
        logger.info("========================================");
        logger.info("Starting Stock Logo Update Batch...");
        logger.info("========================================");
        logger.info("Database URL: {}", dbUrl);
        logger.info("Database Username: {}", dbUsername);
        logger.info("Storage Endpoint: {}", storageEndpoint);
        logger.info("Storage Bucket: {}", storageBucket);
        logger.info("========================================");

        try {
            // 1. stock/logo/ 하위의 모든 파일 조회
            List<ObjectInfo> allFiles = fileClientPort.listObjects(LOGO_PREFIX);
            logger.info("Found {} files in storage with prefix: {}", allFiles.size(), LOGO_PREFIX);

            // 2. stock code별로 그룹핑 (stock/logo/{code}/{filename} 형태에서 code 추출)
            logger.info("Sample file keys (first 5):");
            allFiles.stream().limit(5).forEach(file -> logger.info("  - {}", file.key()));

            Map<String, List<ObjectInfo>> filesByCode = allFiles.stream()
                .filter(file -> {
                    String code = extractStockCode(file.key());
                    if (code == null) {
                        logger.debug("Skipping file with invalid format: {}", file.key());
                    }
                    return code != null;
                })
                .collect(Collectors.groupingBy(file -> extractStockCode(file.key())));

            logger.info("Found {} unique stock codes", filesByCode.size());
            logger.info("Sample stock codes (first 5): {}",
                filesByCode.keySet().stream().limit(5).collect(Collectors.toList()));

            // 3. 각 stock code별로 최신 파일 선택 및 DB 업데이트
            int updatedCount = 0;
            int notFoundCount = 0;

            for (Map.Entry<String, List<ObjectInfo>> entry : filesByCode.entrySet()) {
                String code = entry.getKey();
                List<ObjectInfo> files = entry.getValue();

                logger.debug("Processing stock code: {} with {} file(s)", code, files.size());

                // 최신 파일 선택 (lastModified 기준 내림차순)
                ObjectInfo latestFile = files.stream()
                    .max(Comparator.comparing(ObjectInfo::lastModified))
                    .orElse(null);

                if (latestFile == null) {
                    logger.warn("No files found for code: {}", code);
                    continue;
                }

                logger.debug("Latest file for {}: {} (modified: {})", code, latestFile.key(),
                    latestFile.lastModified());

                // DB에서 Stock 조회
                Stock stock = stockRepository.findByCode(code);

                if (stock == null) {
                    logger.warn("Stock not found in DB for code: {}", code);
                    notFoundCount++;
                    continue;
                }

                logger.debug("Found stock in DB: id={}, code={}, currentLogo={}",
                    stock.getId(), stock.getCode(), stock.getLogo());

                // logo 업데이트 (트랜잭션 내에서 실행)
                String oldLogo = stock.getLogo();
                transactionTemplate.execute(status -> {
                    stock.updateLogo(latestFile.key());
                    stockRepository.save(stock);
                    return null;
                });

                logger.info("Updated logo for stock code: {} | old: {} -> new: {}",
                    code, oldLogo, latestFile.key());
                updatedCount++;
            }

            logger.info("Stock Logo Update Batch completed. Updated: {}, Not Found: {}",
                updatedCount, notFoundCount);

        } catch (Exception e) {
            logger.error("Error occurred during Stock Logo Update Batch", e);
            System.exit(SpringApplication.exit(applicationContext, () -> 1));
        }

        // 배치 성공 후 애플리케이션 종료
        logger.info("Shutting down application after batch completion...");
        System.exit(SpringApplication.exit(applicationContext, () -> 0));
    }

    /**
     * ObjectKey에서 stock code 추출
     *
     * @param objectKey
     *            예: "stock/logo/AAPL/logo.png"
     * @return stock code (예: "AAPL") 또는 null
     */
    private String extractStockCode(String objectKey) {
        if (objectKey == null || !objectKey.startsWith(LOGO_PREFIX)) {
            return null;
        }

        String remaining = objectKey.substring(LOGO_PREFIX.length());
        String[] parts = remaining.split("/");

        if (parts.length < 2) {
            return null;
        }

        return parts[0]; // stock code
    }
}
