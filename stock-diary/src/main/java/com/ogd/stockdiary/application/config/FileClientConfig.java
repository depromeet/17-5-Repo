package com.ogd.stockdiary.application.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ogd.stockdiary.application.fileclient.adapter.MinioFileClientAdapter;
import com.ogd.stockdiary.domain.fileclient.port.out.FileClientPort;

/**
 * 파일 클라이언트 설정
 *
 * <p>FileClientPort 구현체를 Bean으로 등록합니다.
 *
 * <p><b>AWS S3로 전환시:</b> MinioFileClientAdapter 대신 S3FileClientAdapter를 주입하도록 변경합니다.
 */
@Configuration
public class FileClientConfig {

    /**
     * FileClientPort 구현체를 Bean으로 등록
     *
     * <p>현재는 MinioFileClientAdapter를 사용하지만, AWS S3로 전환시 구현체만 교체하면 됩니다.
     *
     * @param minioAdapter MinIO 어댑터 (Spring이 자동으로 주입)
     * @return FileClientPort 인터페이스
     */
    @Bean
    public FileClientPort fileClientPort(MinioFileClientAdapter minioAdapter) {
        return minioAdapter;
    }

    // AWS S3로 전환시 주석 해제:
    // @Bean
    // public FileClientPort fileClientPort(S3FileClientAdapter s3Adapter) {
    //     return s3Adapter;
    // }
}
