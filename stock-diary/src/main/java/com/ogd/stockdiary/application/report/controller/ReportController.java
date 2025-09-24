package com.ogd.stockdiary.application.report.controller;

import com.ogd.stockdiary.application.report.dto.Mapper.ReportMapper;
import com.ogd.stockdiary.application.report.dto.Request.CreateFeedbackRequest;
import com.ogd.stockdiary.application.report.dto.Response.CreateFeedbackResponse;
import com.ogd.stockdiary.common.httpresponse.HttpApiResponse;
import com.ogd.stockdiary.domain.report.entity.Feedback;
import com.ogd.stockdiary.domain.report.port.in.CreateFeedbackCommand;
import com.ogd.stockdiary.domain.report.port.in.CreateFeedbackUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reports")
public class ReportController {
    private final CreateFeedbackUseCase createFeedbackUseCase;

    @PostMapping("/{retrospectionId}/feedback")
    public ResponseEntity<HttpApiResponse<CreateFeedbackResponse>> CreateFeedback(
            @RequestBody CreateFeedbackRequest request,
            @PathVariable Long retrospectionId){

        // 요청을 command 객체로 변환
        CreateFeedbackCommand command = ReportMapper.toCommand(request, retrospectionId);
        // 서비스에 전달 후 피드백 엔티티 반환
        Feedback feedback = createFeedbackUseCase.createFeedbackUseCase(command);
        // 응답용 DTO
        CreateFeedbackResponse text = ReportMapper.toResponse(feedback);

        return ResponseEntity.status(HttpStatus.CREATED).body(HttpApiResponse.of(text));
    }
}
