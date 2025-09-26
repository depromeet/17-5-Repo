package com.ogd.stockdiary.application.report.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ogd.stockdiary.application.report.dto.Mapper.ReportMapper;
import com.ogd.stockdiary.application.report.dto.Request.CreateFeedbackRequest;
import com.ogd.stockdiary.application.report.dto.Response.CreateFeedbackResponse;
import com.ogd.stockdiary.common.httpresponse.HttpApiResponse;
import com.ogd.stockdiary.domain.report.entity.Feedback;
import com.ogd.stockdiary.domain.report.port.in.CreateFeedbackCommand;
import com.ogd.stockdiary.domain.report.port.in.CreateFeedbackUseCase;
import com.ogd.stockdiary.domain.report.port.out.FeedbackRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reports")
public class ReportController {
    private final CreateFeedbackUseCase createFeedbackUseCase;
    private final FeedbackRepository feedbackRepository;

    @PostMapping("/{retrospectionId}/feedback")
    public ResponseEntity<HttpApiResponse<CreateFeedbackResponse>> CreateFeedback(
            @RequestBody(required = false) CreateFeedbackRequest request,
            @PathVariable Long retrospectionId)
            throws JsonProcessingException {

        // 요청을 command 객체로 변환
        CreateFeedbackCommand command = ReportMapper.toCommand(request, retrospectionId);
        // 서비스에 전달 후 피드백 엔티티 반환
        Feedback feedback = createFeedbackUseCase.createFeedbackUseCase(command);
        // 응답용 DTO
        CreateFeedbackResponse text = ReportMapper.toResponse(feedback);

        return ResponseEntity.status(HttpStatus.CREATED).body(HttpApiResponse.of(text));
    }

    @DeleteMapping("/{feedbackId}")
    public ResponseEntity<HttpApiResponse<Void>> DeleteFeedback(@PathVariable Long feedbackId) {
        feedbackRepository.deleteById(feedbackId);

        return ResponseEntity.ok().build();
    }
}
