package com.ogd.stockdiary.domain.report.usecase;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ogd.stockdiary.application.report.dto.Response.BadgeResponse;
import com.ogd.stockdiary.application.report.dto.Response.CreateFeedbackResponse;

public interface GetFeedbackUsecase {
    CreateFeedbackResponse getFeedbackByRetrospectionId(Long retrospectionId) throws JsonProcessingException;

    BadgeResponse getAllFeedbackUsecase(Long userId);
}
