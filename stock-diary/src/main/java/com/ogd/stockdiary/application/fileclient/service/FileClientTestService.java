package com.ogd.stockdiary.application.fileclient.service;

import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ogd.stockdiary.domain.fileclient.port.in.FileClientTestUseCase;
import com.ogd.stockdiary.domain.fileclient.port.out.FileClientPort;

/**
 * 파일 클라이언트 테스트 서비스
 *
 * <p>
 * <b>⚠️ 테스트 전용:</b> 이 서비스는 MinIO 파일 업로드/다운로드 기능을 테스트하기 위한 용도입니다.
 *
 * <p>
 * <b>🔴 추후 삭제 예정:</b> 프로덕션 배포 전 반드시 제거해야 합니다.
 *
 * <p>
 * <b>구현:</b> {@link FileClientPort}를 사용하여 파일 스토리지 작업을 수행합니다.
 */
@Service
public class FileClientTestService implements FileClientTestUseCase {

    private static final Logger logger = LoggerFactory.getLogger(FileClientTestService.class);
    private final FileClientPort fileClientPort;

    public FileClientTestService(FileClientPort fileClientPort) {
        this.fileClientPort = fileClientPort;
    }

    @Override
    public String createUploadUrl(String objectKey, int ttl) {
        logger.info("[TEST] Creating upload URL for object: {}", objectKey);
        return fileClientPort.createPreSignedUrl(objectKey, ttl);
    }

    @Override
    public void uploadFile(InputStream inputStream, String objectKey, long contentLength) {
        logger.info(
            "[TEST] Uploading file to object: {}, size: {} bytes", objectKey, contentLength);
        fileClientPort.uploadFile(inputStream, objectKey, contentLength);
    }

    @Override
    public String createDownloadUrl(String objectKey, int ttl) {
        logger.info("[TEST] Creating download URL for object: {}", objectKey);
        return fileClientPort.getDownloadPreSignedUrl(objectKey, ttl);
    }
}
