package com.ogd.stockdiary.application.image.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ImageUploadResponse {

    private final Long imageId;
    private final String objectKey;
    private final String fileName;
    private final Long fileSize;
}
