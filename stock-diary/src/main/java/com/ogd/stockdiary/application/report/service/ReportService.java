package com.ogd.stockdiary.application.report.service;

import java.util.List;
import java.util.Map;

import jakarta.transaction.Transactional;

import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ogd.stockdiary.application.report.dto.Response.CreateFeedbackResponse;
import com.ogd.stockdiary.domain.report.entity.Feedback;
import com.ogd.stockdiary.domain.report.entity.RetrospectionForReport;
import com.ogd.stockdiary.domain.report.port.in.CreateFeedbackCommand;
import com.ogd.stockdiary.domain.report.port.in.CreateFeedbackUseCase;
import com.ogd.stockdiary.domain.report.port.out.FeedbackRepository;
import com.ogd.stockdiary.domain.report.port.out.ReportPromptLoader;
import com.ogd.stockdiary.domain.report.port.out.RetrospectionForReportRepository;
import com.ogd.stockdiary.domain.retrospection.entity.Order;
import com.ogd.stockdiary.domain.retrospection.entity.Retrospection;
import com.ogd.stockdiary.domain.retrospection.port.out.RetrospectionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportService implements CreateFeedbackUseCase {

    private final RetrospectionForReportRepository retrospectionForReportRepository;
    private final RetrospectionRepository retrospectionRepository;
    private final FeedbackRepository feedbackRepository;
    private final ReportPromptLoader reportPromptLoader;
    private final ChatModel chatModel;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public Feedback createFeedbackUseCase(CreateFeedbackCommand command)
            throws JsonProcessingException {

        Retrospection retrospection = retrospectionRepository.getById(command.retrospectionId());

        RetrospectionForReport retrospectionForReport =
                retrospectionForReportRepository.getById(command.retrospectionId());

        String symbol = retrospectionForReport.getSymbol();
        String market = retrospectionForReport.getMarket();
        Order order = retrospectionForReport.getOrder();
        String content = retrospectionForReport.getContent();
                
        String userText =
                """
                Please analyze the symbol {symbol} in the {market} market based on the order: {order}.
                This is user message : {content}.
                """;

        // 시스템 메시지를 로더에서 불러오기
        Message systemMessage = new SystemMessage(reportPromptLoader.getPrompt());

        PromptTemplate promptTemplate = new PromptTemplate(userText);

        Map<String, Object> variables = Map.of("symbol", symbol, "market", market, "order", order, "content", content);

        // 플레이스 홀더 넣은 유저 메시지 구성
        Message userMessage = promptTemplate.createMessage(variables);

        String modelName = "gpt-4.1-nano";

        OpenAiChatOptions options =
                new OpenAiChatOptions.Builder().model(modelName).maxTokens(500).build();

        Prompt prompt = new Prompt(List.of(systemMessage, userMessage), options);

        // 동기 처리
        ChatResponse response = chatModel.call(prompt);

        String text = response.getResult().getOutput().getText();

        // JSON 문자열 text(LLM 응답)을 자바 객체로
        CreateFeedbackResponse dto = objectMapper.readValue(text, CreateFeedbackResponse.class);

        // principle 은 디비에서 JSON으로 저장되기에 다시 JSON 문자열로 변환
        String principlesJson = objectMapper.writeValueAsString(dto.principles());

        // 피드백 객체 생성
        Feedback feedback =
                Feedback.builder()
                        .feedback(text)
                        .summerizedFeedback(dto.summerizedFeedback())
                        .market(dto.market())
                        .principles(principlesJson)
                        .retrospection(retrospection)
                        .build();

        // 저장
        feedbackRepository.save(feedback);

        // 반환
        return feedback;
    }
}
