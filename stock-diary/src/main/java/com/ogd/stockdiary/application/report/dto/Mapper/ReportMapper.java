package com.ogd.stockdiary.application.report.dto.Mapper;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ogd.stockdiary.application.report.dto.Response.CreateFeedbackResponse;
import com.ogd.stockdiary.domain.report.entity.Feedback;
import com.ogd.stockdiary.domain.report.port.in.CreateFeedbackCommand;
import com.ogd.stockdiary.domain.report.port.in.GetFeedbackCommand;

public class ReportMapper {

    public static CreateFeedbackCommand toCommand(Long retrospectionId) {
        return new CreateFeedbackCommand(retrospectionId);
    }

    public static CreateFeedbackResponse toResponse(Feedback feedback)
        throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<String> keepList = null; // value만
        List<String> improveList = null;
        List<String> nextTiimeList = null;

        // 디비에 JSON 으로 저장되었었음.
        String keepJson = feedback.getKeep();
        String improveJson = feedback.getImprove();
        String nextTimeJson = feedback.getNextTime();

        // JSON 을 자바 객체로 파싱
        keepList = objectMapper.readValue(
            keepJson, new TypeReference<List<String>>() {
            });
        improveList = objectMapper.readValue(
            improveJson, new TypeReference<List<String>>() {
            });
        nextTiimeList = objectMapper.readValue(
            nextTimeJson, new TypeReference<List<String>>() {
            });

        return new CreateFeedbackResponse(
            // 나머지 필드는 디비에 String 으로 저장되었었음
            feedback.getSymbol(), feedback.getPrice(), feedback.getVolume(),
            feedback.getOrderType(), feedback.getCompanylogo(), feedback.getKeptCount(), feedback.getNeutralCount(),
            feedback.getNotKeptCount(),
            feedback.getTitle(), keepList, improveList, nextTiimeList);
    }

    public static GetFeedbackCommand toFeedbackCommand(Long userId) {
        return new GetFeedbackCommand(userId);
    }
}
