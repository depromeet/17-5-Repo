package com.ogd.stockdiary.application.report.dto.Mapper;

import com.ogd.stockdiary.application.report.dto.Request.CreateFeedbackRequest;
import com.ogd.stockdiary.application.report.dto.Response.CreateFeedbackResponse;
import com.ogd.stockdiary.domain.report.entity.Feedback;
import com.ogd.stockdiary.domain.report.port.in.CreateFeedbackCommand;

public class ReportMapper {

    public static CreateFeedbackCommand toCommand(
            CreateFeedbackRequest request, Long retrospectionId) {
        return new CreateFeedbackCommand(retrospectionId, request.modelName());
    }

    public static CreateFeedbackResponse toResponse(Feedback feedback) {
        return new CreateFeedbackResponse(feedback.getFeedback());
    }
}
