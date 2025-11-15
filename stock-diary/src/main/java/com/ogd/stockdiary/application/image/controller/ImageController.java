package com.ogd.stockdiary.application.image.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ogd.stockdiary.application.config.security.AuthenticatedUser;
import com.ogd.stockdiary.application.image.dto.ImageUploadResponse;
import com.ogd.stockdiary.common.httpresponse.HttpApiResponse;
import com.ogd.stockdiary.domain.image.dto.UploadImageCommand;
import com.ogd.stockdiary.domain.image.entity.ImageMetadata;
import com.ogd.stockdiary.domain.image.port.in.ImageUseCase;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Image", description = "이미지 업로드 API")
@RestController
@RequestMapping("/api/v1/{domain}/images")
@RequiredArgsConstructor
public class ImageController {

    private final ImageUseCase imageUseCase;

    @Operation(summary = "이미지 업로드", description = "이미지를 업로드하고 메타데이터를 저장합니다. 업로드된 이미지는 임시 상태(T)로 저장됩니다.")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<HttpApiResponse<ImageUploadResponse>> uploadImage(
        @AuthenticationPrincipal AuthenticatedUser user,
        @PathVariable String domain,
        @RequestParam("file") MultipartFile file) throws Exception {

        Long userId = user.getUserId();

        // UploadImageCommand 생성
        UploadImageCommand command = new UploadImageCommand(
            userId, domain, file.getOriginalFilename(), file.getSize(), file.getInputStream());

        // 이미지 업로드
        ImageMetadata imageMetadata = imageUseCase.uploadImage(command);

        // 응답 생성
        ImageUploadResponse response = new ImageUploadResponse(
            imageMetadata.getId(),
            imageMetadata.getObjectKey(),
            imageMetadata.getFileName(),
            imageMetadata.getFileSize());

        return ResponseEntity.ok(HttpApiResponse.of(response));
    }
}
