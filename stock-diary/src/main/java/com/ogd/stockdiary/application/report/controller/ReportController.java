package com.ogd.stockdiary.application.report.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ogd.stockdiary.application.config.security.AuthenticatedUser;
import com.ogd.stockdiary.application.report.dto.Mapper.ReportMapper;
import com.ogd.stockdiary.application.report.dto.Response.BadgeResponse;
import com.ogd.stockdiary.application.report.dto.Response.CreateFeedbackResponse;
import com.ogd.stockdiary.common.httpresponse.HttpApiResponse;
import com.ogd.stockdiary.domain.report.entity.Feedback;
import com.ogd.stockdiary.domain.report.port.in.CreateFeedbackCommand;
import com.ogd.stockdiary.domain.report.port.in.GetFeedbackCommand;
import com.ogd.stockdiary.domain.report.port.out.FeedbackRepository;
import com.ogd.stockdiary.domain.report.usecase.CreateFeedbackUseCase;
import com.ogd.stockdiary.domain.report.usecase.GetFeedbackUsecase;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reports")
public class ReportController {
    private final CreateFeedbackUseCase createFeedbackUseCase;
    private final FeedbackRepository feedbackRepository;
    private final GetFeedbackUsecase getFeedbackUsecase;

    @PostMapping("/{retrospectionId}/feedback")
    @Operation(summary = "피드백 생성", description = "회고에 대한 피드백을 생성합니다.")
    public ResponseEntity<HttpApiResponse<CreateFeedbackResponse>> CreateFeedback(
        @PathVariable Long retrospectionId)
        throws JsonProcessingException {

        // 요청을 command 객체로 변환
        CreateFeedbackCommand command = ReportMapper.toCommand(retrospectionId);
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

    @GetMapping()
    @Operation(summary = "홈화면 피드백 뱃지 조회", description = "홈화면에서 뱃지 개수를 조회합니다.")
    public ResponseEntity<HttpApiResponse<BadgeResponse>> GetAllFeedback(
        @AuthenticationPrincipal AuthenticatedUser user) {
        Long userId = user.getUserId();

        GetFeedbackCommand command = ReportMapper.toFeedbackCommand(userId);

        BadgeResponse badgeResponse = getFeedbackUsecase.getAllFeedbackUsecase(command);

        return ResponseEntity.ok().body(HttpApiResponse.of(badgeResponse));

    }
}
