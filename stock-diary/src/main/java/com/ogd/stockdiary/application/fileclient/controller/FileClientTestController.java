package com.ogd.stockdiary.application.fileclient.controller;

import jakarta.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.ogd.stockdiary.application.fileclient.dto.CreateUploadUrlRequest;
import com.ogd.stockdiary.application.fileclient.dto.FileUploadResponse;
import com.ogd.stockdiary.application.fileclient.dto.PreSignedUrlResponse;
import com.ogd.stockdiary.common.httpresponse.HttpApiResponse;
import com.ogd.stockdiary.domain.fileclient.port.in.FileClientTestUseCase;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 파일 클라이언트 테스트 컨트롤러
 *
 * <p>
 * <b>⚠️ 테스트 전용:</b> 이 컨트롤러는 MinIO 파일 업로드/다운로드 기능을 테스트하기 위한 용도입니다.
 *
 * <p>
 * <b>🔴 추후 삭제 예정:</b> 프로덕션 배포 전 반드시 제거해야 합니다.
 *
 * <p>
 * <b>구현:</b>
 * {@link com.ogd.stockdiary.domain.fileclient.port.out.FileClientPort}를 사용합니다.
 */
@Tag(name = "[TEST] File Client", description = "⚠️ 테스트 전용 API - 추후 삭제 예정")
@RestController
@RequestMapping("/api/test/file-client")
public class FileClientTestController {

    private final FileClientTestUseCase fileClientTestUseCase;

    public FileClientTestController(FileClientTestUseCase fileClientTestUseCase) {
        this.fileClientTestUseCase = fileClientTestUseCase;
    }

    @Operation(summary = "[TEST] 업로드 URL 생성", description = "파일 업로드를 위한 Pre-Signed URL을 생성합니다. "
        + "⚠️ 테스트 전용 - 추후 삭제 예정")
    @PostMapping("/upload-url")
    public ResponseEntity<HttpApiResponse<PreSignedUrlResponse>> createUploadUrl(
        @Valid @RequestBody CreateUploadUrlRequest request) {

        String url = fileClientTestUseCase.createUploadUrl(request.getObjectKey(), request.getTtl());

        PreSignedUrlResponse response = new PreSignedUrlResponse(url, request.getObjectKey(), request.getTtl());

        return ResponseEntity.ok(HttpApiResponse.of(response));
    }

    @Operation(summary = "[TEST] 파일 직접 업로드", description = "파일을 MinIO에 직접 업로드합니다. " + "⚠️ 테스트 전용 - 추후 삭제 예정")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<HttpApiResponse<FileUploadResponse>> uploadFile(
        @RequestParam("file") MultipartFile file, @RequestParam("objectKey") String objectKey)
        throws Exception {

        fileClientTestUseCase.uploadFile(file.getInputStream(), objectKey, file.getSize());

        FileUploadResponse response = new FileUploadResponse(objectKey, file.getSize(), "파일 업로드 성공");

        return ResponseEntity.ok(HttpApiResponse.of(response));
    }

    @Operation(summary = "[TEST] 다운로드 URL 생성", description = "파일 다운로드를 위한 Pre-Signed URL을 생성합니다. "
        + "⚠️ 테스트 전용 - 추후 삭제 예정")
    @GetMapping("/download-url/{objectKey}")
    public ResponseEntity<HttpApiResponse<PreSignedUrlResponse>> createDownloadUrl(
        @PathVariable String objectKey, @RequestParam(defaultValue = "3600") int ttl) {

        String url = fileClientTestUseCase.createDownloadUrl(objectKey, ttl);

        PreSignedUrlResponse response = new PreSignedUrlResponse(url, objectKey, ttl);

        return ResponseEntity.ok(HttpApiResponse.of(response));
    }
}
