package com.ogd.stockdiary.application.report.dto.Mapper;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ogd.stockdiary.application.report.dto.Request.CreateFeedbackRequest;
import com.ogd.stockdiary.application.report.dto.Response.CreateFeedbackResponse;
import com.ogd.stockdiary.domain.report.entity.Feedback;
import com.ogd.stockdiary.domain.report.port.in.CreateFeedbackCommand;

public class ReportMapper {

    public static CreateFeedbackCommand toCommand(
        CreateFeedbackRequest request, Long retrospectionId) {
        return new CreateFeedbackCommand(retrospectionId);
    }

    public static CreateFeedbackResponse toResponse(Feedback feedback)
        throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<Map<String, String>> principlesList = null;

        // 디비에 JSON 으로 저장되었었음.
        String principleJson = feedback.getPrinciples();

        // JSON 을 자바 객체로 파싱
        principlesList = objectMapper.readValue(
            principleJson, new TypeReference<List<Map<String, String>>>() {
            });

        return new CreateFeedbackResponse(
            // 나머지 필드는 디비에 String 으로 저장되었었음
            feedback.getSummerizedFeedback(), feedback.getMarket(), principlesList);
    }
}
