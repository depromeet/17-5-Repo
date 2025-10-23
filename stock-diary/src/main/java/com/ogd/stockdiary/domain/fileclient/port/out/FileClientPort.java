package com.ogd.stockdiary.domain.fileclient.port.out;

import java.io.InputStream;

/**
 * 파일 클라이언트 포트 인터페이스 (Hexagonal Architecture - Output Port)
 *
 * <p>이 인터페이스는 파일 스토리지 시스템(MinIO, AWS S3 등)과의 통신을 추상화합니다. 구현체를 교체하여 다양한 스토리지 시스템을 지원할 수 있습니다.
 */
public interface FileClientPort {

    /**
     * 파일 업로드를 위한 Pre-Signed URL을 생성합니다.
     *
     * @param objectKey 객체 키 (S3 파일 경로)
     * @param ttl URL 유효 시간 (초)
     * @return Pre-Signed URL
     */
    String createPreSignedUrl(String objectKey, int ttl);

    /**
     * 원격 스토리지에서 로컬로 파일을 다운로드합니다.
     *
     * @param objectKey 객체 키 (S3 파일 경로)
     * @param localFilePath 로컬 저장 경로
     */
    void downloadFile(String objectKey, String localFilePath);

    /**
     * InputStream을 통해 파일을 업로드합니다.
     *
     * @param inputStream 업로드할 파일의 InputStream
     * @param objectKey 객체 키 (S3 파일 경로)
     * @param contentLength 파일 크기 (바이트)
     */
    void uploadFile(InputStream inputStream, String objectKey, long contentLength);

    /**
     * 파일 다운로드를 위한 Pre-Signed URL을 생성합니다.
     *
     * @param objectKey 객체 키 (S3 파일 경로)
     * @param ttl URL 유효 시간 (초)
     * @return Pre-Signed URL
     * @throws com.ogd.stockdiary.common.exception.ApplicationException 파일이 존재하지 않을 경우
     */
    String getDownloadPreSignedUrl(String objectKey, int ttl);
}
