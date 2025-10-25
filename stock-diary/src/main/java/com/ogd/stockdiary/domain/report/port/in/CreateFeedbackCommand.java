package com.ogd.stockdiary.domain.report.port.in;

import org.springframework.web.multipart.MultipartFile;

public record CreateFeedbackCommand(Long retrospectionId, MultipartFile imageFile) {}
