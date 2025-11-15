package com.ogd.stockdiary.domain.report.usecase;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ogd.stockdiary.application.report.dto.Response.CreateFeedbackResponse;

public interface CreateFeedbackUseCase {

    CreateFeedbackResponse createFeedback(Long retrospectionId) throws JsonProcessingException;
}
