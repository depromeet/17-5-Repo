package com.ogd.stockdiary.application.fileclient.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.ogd.stockdiary.common.httpresponse.CodeEnum;
import com.ogd.stockdiary.domain.fileclient.port.out.FileClientPort;
import com.ogd.stockdiary.exception.ApplicationException;

/**
 * MinioFileClientAdapter 통합 테스트
 *
 * <p>
 * 실제 MinIO 서버와 통신하여 파일 업로드/다운로드를 테스트합니다.
 *
 * <p>
 * <b>주의:</b> 이 테스트를 실행하려면:
 *
 * <ul>
 * <li>MinIO 서버가 실행 중이어야 합니다.
 * <li>src/test/resources/application.yml 파일이 있어야 합니다. (application.yml.example
 * 참고)
 * </ul>
 */
class MinioFileClientAdapterTest {

    private FileClientPort fileClient;
    private String testBucket;

    // 테스트용 파일 정보
    private static final String TEST_OBJECT_KEY = "test-file.txt";
    private static final String TEST_FILE_CONTENT = "Hello MinIO!";

    @BeforeEach
    void setUp() {
        // given - 환경변수 또는 시스템 프로퍼티에서 설정 읽기
        String endpoint = System.getenv("STORAGE_ENDPOINT");
        String accessKey = System.getenv("STORAGE_ACCESS_KEY");
        String secretKey = System.getenv("STORAGE_SECRET_KEY");
        String region = System.getenv("STORAGE_REGION") != null
            ? System.getenv("STORAGE_REGION")
            : "us-east-1";
        this.testBucket = System.getenv("STORAGE_BUCKET") != null
            ? System.getenv("STORAGE_BUCKET")
            : "depromeet";

        // MinioFileClientAdapter 직접 생성
        fileClient = new MinioFileClientAdapter(endpoint, testBucket, accessKey, secretKey, region);
    }

    @Test
    @DisplayName("Pre-Signed 업로드 URL을 생성할 수 있다")
    void createPreSignedUrl() {
        // given
        String objectKey = TEST_OBJECT_KEY;
        int ttl = 3600; // 1시간

        // when
        String preSignedUrl = fileClient.createPreSignedUrl(objectKey, ttl);

        // then
        assertThat(preSignedUrl).isNotNull();
        assertThat(preSignedUrl).startsWith("http");
        assertThat(preSignedUrl).contains(testBucket);
        assertThat(preSignedUrl).contains(objectKey);
        assertThat(preSignedUrl).contains("X-Amz-Signature"); // AWS Signature 포함
    }

    @Test
    @DisplayName("InputStream으로 파일을 업로드할 수 있다")
    void uploadFile() {
        // given
        String objectKey = "upload-test/" + TEST_OBJECT_KEY;
        byte[] content = TEST_FILE_CONTENT.getBytes(StandardCharsets.UTF_8);
        InputStream inputStream = new ByteArrayInputStream(content);
        long contentLength = content.length;

        // when
        fileClient.uploadFile(inputStream, objectKey, contentLength);

        // then
        // 업로드가 성공하면 예외가 발생하지 않음
        // 실제 파일이 업로드되었는지는 다운로드 테스트에서 검증
        assertThat(true).isTrue(); // 예외 없이 완료됨을 확인
    }

    @Test
    @DisplayName("파일을 다운로드할 수 있다")
    void downloadFile() throws Exception {
        // given
        String objectKey = "download-test/" + TEST_OBJECT_KEY;
        String localFilePath = "build/test-downloads/" + TEST_OBJECT_KEY;

        // 먼저 파일을 업로드
        byte[] content = TEST_FILE_CONTENT.getBytes(StandardCharsets.UTF_8);
        InputStream uploadStream = new ByteArrayInputStream(content);
        fileClient.uploadFile(uploadStream, objectKey, content.length);

        // 다운로드 디렉토리 생성
        new File("build/test-downloads").mkdirs();

        // when
        fileClient.downloadFile(objectKey, localFilePath);

        // then
        File downloadedFile = new File(localFilePath);
        assertThat(downloadedFile).exists();

        String downloadedContent = new String(Files.readAllBytes(downloadedFile.toPath()), StandardCharsets.UTF_8);
        assertThat(downloadedContent).isEqualTo(TEST_FILE_CONTENT);

        // 테스트 파일 삭제
        downloadedFile.delete();
    }

    @Test
    @DisplayName("존재하는 파일의 다운로드 Pre-Signed URL을 생성할 수 있다")
    void getDownloadPreSignedUrl() {
        // given
        String objectKey = "presigned-download-test/" + TEST_OBJECT_KEY;
        int ttl = 3600; // 1시간

        // 먼저 파일을 업로드
        byte[] content = TEST_FILE_CONTENT.getBytes(StandardCharsets.UTF_8);
        InputStream uploadStream = new ByteArrayInputStream(content);
        fileClient.uploadFile(uploadStream, objectKey, content.length);

        // when
        String downloadUrl = fileClient.getDownloadPreSignedUrl(objectKey, ttl);

        // then
        assertThat(downloadUrl).isNotNull();
        assertThat(downloadUrl).startsWith("http");
        assertThat(downloadUrl).contains(testBucket);
        assertThat(downloadUrl).contains("presigned-download-test");
        assertThat(downloadUrl).contains("X-Amz-Signature");
    }

    @Test
    @DisplayName("존재하지 않는 파일의 다운로드 URL 생성시 예외가 발생한다")
    void getDownloadPreSignedUrlForNonExistentFile() {
        // given
        String nonExistentObjectKey = "non-existent-file-" + System.currentTimeMillis() + ".txt";
        int ttl = 3600;

        // when & then
        assertThatThrownBy(() -> fileClient.getDownloadPreSignedUrl(nonExistentObjectKey, ttl))
            .isInstanceOf(ApplicationException.class)
            .hasFieldOrPropertyWithValue("code", CodeEnum.FRS_003)
            .hasMessageContaining("파일을 찾을 수 없습니다");
    }

    @Test
    @DisplayName("짧은 TTL로 Pre-Signed URL을 생성할 수 있다")
    void createPreSignedUrlWithShortTtl() {
        // given
        String objectKey = "short-ttl-test.txt";
        int shortTtl = 60; // 1분

        // when
        String preSignedUrl = fileClient.createPreSignedUrl(objectKey, shortTtl);

        // then
        assertThat(preSignedUrl).isNotNull();
        assertThat(preSignedUrl).contains("X-Amz-Expires=60"); // TTL 확인
    }

    @Test
    @DisplayName("긴 TTL로 Pre-Signed URL을 생성할 수 있다")
    void createPreSignedUrlWithLongTtl() {
        // given
        String objectKey = "long-ttl-test.txt";
        int longTtl = 7200; // 2시간

        // when
        String preSignedUrl = fileClient.createPreSignedUrl(objectKey, longTtl);

        // then
        assertThat(preSignedUrl).isNotNull();
        assertThat(preSignedUrl).contains("X-Amz-Expires=7200"); // TTL 확인
    }

    @Test
    @DisplayName("다양한 경로의 파일을 업로드할 수 있다")
    void uploadFileWithNestedPath() {
        // given
        String objectKey = "folder1/folder2/folder3/" + TEST_OBJECT_KEY;
        byte[] content = "Nested path test".getBytes(StandardCharsets.UTF_8);
        InputStream inputStream = new ByteArrayInputStream(content);
        long contentLength = content.length;

        // when
        fileClient.uploadFile(inputStream, objectKey, contentLength);

        // then
        // 다운로드 URL 생성으로 파일 존재 확인
        String downloadUrl = fileClient.getDownloadPreSignedUrl(objectKey, 60);
        assertThat(downloadUrl).isNotNull();
        assertThat(downloadUrl).contains("folder1/folder2/folder3");
    }
}
