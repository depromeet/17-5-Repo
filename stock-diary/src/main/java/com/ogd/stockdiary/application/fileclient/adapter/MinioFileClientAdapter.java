package com.ogd.stockdiary.application.fileclient.adapter;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.time.Duration;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ogd.stockdiary.common.httpresponse.CodeEnum;
import com.ogd.stockdiary.domain.fileclient.port.out.FileClientPort;
import com.ogd.stockdiary.exception.ApplicationException;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

/**
 * MinIO 파일 클라이언트 어댑터
 *
 * <p>AWS SDK S3를 사용하여 MinIO와 통신합니다. MinIO는 S3 호환 API를 제공하므로 AWS SDK를 그대로 사용할 수 있습니다.
 *
 * <p><b>AWS S3로 전환시:</b> 구현체를 S3FileClientAdapter로 교체하고, application.yml에서 cloud.storage.endpoint
 * 설정을 제거하면 됩니다. endpointOverride 없이 S3Client를 생성하면 AWS S3에 연결됩니다.
 */
@Component
public class MinioFileClientAdapter implements FileClientPort {

    private static final Logger logger = LoggerFactory.getLogger(MinioFileClientAdapter.class);
    private final String bucketName;
    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    public MinioFileClientAdapter(
            @Value("${cloud.storage.endpoint}") String endpoint,
            @Value("${cloud.storage.bucket}") String bucketName,
            @Value("${cloud.storage.access-key}") String accessKey,
            @Value("${cloud.storage.secret-key}") String secretKey,
            @Value("${cloud.storage.region:us-east-1}") String region) {
        this.bucketName = bucketName;

        logger.info(
                "Initializing MinIO client with endpoint: {} and bucket: {}", endpoint, bucketName);

        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
        StaticCredentialsProvider credentialsProvider =
                StaticCredentialsProvider.create(credentials);

        // MinIO용 S3 Client 생성
        this.s3Client =
                S3Client.builder()
                        .endpointOverride(URI.create(endpoint)) // MinIO 엔드포인트 설정
                        .region(Region.of(region)) // MinIO는 region을 무시하지만 필수 설정
                        .credentialsProvider(credentialsProvider)
                        .forcePathStyle(true) // MinIO는 path-style 필수
                        .build();

        // MinIO용 S3 Presigner 생성
        this.s3Presigner =
                S3Presigner.builder()
                        .endpointOverride(URI.create(endpoint))
                        .region(Region.of(region))
                        .credentialsProvider(credentialsProvider)
                        .build();

        // AWS S3로 전환시 주석 해제:
        // this.s3Client = S3Client.builder()
        //     .region(Region.of(region))  // 실제 AWS region 사용 (예: ap-northeast-2)
        //     .credentialsProvider(credentialsProvider)
        //     .forcePathStyle(false)  // AWS S3는 virtual-hosted-style 권장
        //     .build();
        //
        // this.s3Presigner = S3Presigner.builder()
        //     .region(Region.of(region))
        //     .credentialsProvider(credentialsProvider)
        //     .build();
    }

    @Override
    public String createPreSignedUrl(String objectKey, int ttl) {
        logger.info(
                "Generating presigned upload URL for object: {} in bucket: {}",
                objectKey,
                bucketName);

        PutObjectRequest putObjectRequest =
                PutObjectRequest.builder().bucket(bucketName).key(objectKey).build();

        PutObjectPresignRequest presignRequest =
                PutObjectPresignRequest.builder()
                        .signatureDuration(Duration.ofSeconds(ttl))
                        .putObjectRequest(putObjectRequest)
                        .build();

        return s3Presigner.presignPutObject(presignRequest).url().toString();
    }

    @Override
    public void downloadFile(String objectKey, String localFilePath) {
        logger.info(
                "Downloading object {} from bucket {} to {}", objectKey, bucketName, localFilePath);

        GetObjectRequest getObjectRequest =
                GetObjectRequest.builder().bucket(bucketName).key(objectKey).build();

        s3Client.getObject(getObjectRequest, new File(localFilePath).toPath());
    }

    @Override
    public void uploadFile(InputStream inputStream, String objectKey, long contentLength) {
        logger.info("Uploading object {} to bucket {} with stream", objectKey, bucketName);

        PutObjectRequest putObjectRequest =
                PutObjectRequest.builder().bucket(bucketName).key(objectKey).build();

        RequestBody requestBody = RequestBody.fromInputStream(inputStream, contentLength);

        s3Client.putObject(putObjectRequest, requestBody);
    }

    @Override
    public String getDownloadPreSignedUrl(String objectKey, int ttl) {
        // 파일 존재 여부 확인
        try {
            s3Client.headObject(
                    HeadObjectRequest.builder().bucket(bucketName).key(objectKey).build());
        } catch (NoSuchKeyException e) {
            logger.error("Object not found in bucket {}: {}", bucketName, objectKey);
            throw new ApplicationException(
                    CodeEnum.FRS_003, "파일을 찾을 수 없습니다", Map.of("objectKey", objectKey));
        }

        logger.info(
                "Generating presigned download URL for object: {} in bucket: {}",
                objectKey,
                bucketName);

        GetObjectRequest getObjectRequest =
                GetObjectRequest.builder().bucket(bucketName).key(objectKey).build();

        GetObjectPresignRequest presignRequest =
                GetObjectPresignRequest.builder()
                        .signatureDuration(Duration.ofSeconds(ttl))
                        .getObjectRequest(getObjectRequest)
                        .build();

        return s3Presigner.presignGetObject(presignRequest).url().toString();
    }
}
