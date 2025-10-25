package com.ogd.stockdiary.domain.fileclient.port.in;

import java.io.InputStream;

/**
 * 파일 클라이언트 테스트 유즈케이스 (Hexagonal Architecture - Input Port)
 *
 * <p>
 * <b>⚠️ 테스트 전용:</b> 이 인터페이스는 MinIO 파일 업로드/다운로드 기능을 테스트하기 위한 용도입니다.
 *
 * <p>
 * <b>🔴 추후 삭제 예정:</b> 프로덕션 배포 전 반드시 제거해야 합니다.
 *
 * <p>
 * <b>구현 시 주의사항:</b> 반드시
 * {@link com.ogd.stockdiary.domain.fileclient.port.out.FileClientPort}를
 * 사용하여 구현해야 합니다.
 */
public interface FileClientTestUseCase {

    /**
     * 파일 업로드를 위한 Pre-Signed URL을 생성합니다.
     *
     * @param objectKey
     *            객체 키 (S3 파일 경로)
     * @param ttl
     *            URL 유효 시간 (초)
     * @return Pre-Signed 업로드 URL
     */
    String createUploadUrl(String objectKey, int ttl);

    /**
     * 파일을 직접 업로드합니다.
     *
     * @param inputStream
     *            업로드할 파일의 InputStream
     * @param objectKey
     *            객체 키 (S3 파일 경로)
     * @param contentLength
     *            파일 크기 (바이트)
     */
    void uploadFile(InputStream inputStream, String objectKey, long contentLength);

    /**
     * 파일 다운로드를 위한 Pre-Signed URL을 생성합니다.
     *
     * @param objectKey
     *            객체 키 (S3 파일 경로)
     * @param ttl
     *            URL 유효 시간 (초)
     * @return Pre-Signed 다운로드 URL
     */
    String createDownloadUrl(String objectKey, int ttl);
}
