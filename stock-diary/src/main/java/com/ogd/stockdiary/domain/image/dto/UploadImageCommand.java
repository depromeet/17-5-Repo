package com.ogd.stockdiary.domain.image.dto;

import java.io.InputStream;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UploadImageCommand {

    private final Long userId;
    private final String domain;
    private final String fileName;
    private final Long fileSize;
    private final InputStream inputStream;
}
