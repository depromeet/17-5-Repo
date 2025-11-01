package com.ogd.stockdiary.application.report.service;

import java.math.BigDecimal;
import java.util.HashMap;
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
import com.ogd.stockdiary.application.report.dto.Response.LlmResponse;
import com.ogd.stockdiary.domain.principlecheck.entity.PrincipleCheckStatus;
import com.ogd.stockdiary.domain.report.entity.Feedback;
import com.ogd.stockdiary.domain.report.entity.RetrospectionForReport;
import com.ogd.stockdiary.domain.report.port.in.CreateFeedbackCommand;
import com.ogd.stockdiary.domain.report.port.in.CreateFeedbackUseCase;
import com.ogd.stockdiary.domain.report.port.out.FeedbackRepository;
import com.ogd.stockdiary.domain.report.port.out.ReportDataPort;
import com.ogd.stockdiary.domain.report.port.out.ReportPromptLoader;
import com.ogd.stockdiary.domain.report.port.out.RetrospectionForReportRepository;
import com.ogd.stockdiary.domain.report.vo.ReportSourceData;
import com.ogd.stockdiary.domain.retrospection.entity.OrderType;
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
    private final ReportDataPort reportDataPort;

    @Override
    @Transactional
    public Feedback createFeedbackUseCase(CreateFeedbackCommand command)
        throws JsonProcessingException {

        Retrospection retrospection = retrospectionRepository.getById(command.retrospectionId());

        RetrospectionForReport retrospectionForReport = retrospectionForReportRepository
            .getById(command.retrospectionId());

        String symbol = retrospectionForReport.getSymbol();
        BigDecimal price = retrospectionForReport.getOrder().getPrice();
        Integer volume = retrospectionForReport.getOrder().getVolume();
        OrderType orderType = retrospectionForReport.getOrder().getOrderType();

        List<ReportSourceData> reportSourceData = reportDataPort.findByRetrospectionId(command.retrospectionId());

        // null 허용 위해서 해시맵 사용
        List<Map<String, Object>> checks = reportSourceData.stream()
            .map(source -> {
                Map<String, Object> map = new HashMap<>();
                map.put("principle", source.principle());
                map.put("status", source.status());
                map.put("reason", source.reason());
                map.put("imageUrls", source.imageUrls());

                return map;
            })
            .toList();

        String principleCheckAndImage = objectMapper.writeValueAsString(checks);

        // 원칙 상태 카운트 집계
        long keptCount = reportSourceData.stream()
            .filter(d -> d.status() == PrincipleCheckStatus.KEPT).count();
        long neutralCount = reportSourceData.stream()
            .filter(d -> d.status() == PrincipleCheckStatus.NEUTRAL).count();

        long notKeptCount = reportSourceData.stream()
            .filter(d -> d.status() == PrincipleCheckStatus.NOT_KEPT).count();

        String userText = """
                Please analyze the following trade data and the attached image, and provide comprehensive investment feedback based on the prompt: 'Invest'.

                --- Trade Details ---
                Symbol: {symbol}
                Order Price: {price}
                Order Volume: {volume}
                Order Type: {orderType}

                --- User Reflection ---
                Principle Check and Image Data: {principleCheckAndImage}
            """;

        // 시스템 메시지를 로더에서 불러오기
        Message systemMessage = new SystemMessage(reportPromptLoader.getPrompt());

        PromptTemplate promptTemplate = new PromptTemplate(userText);

        // 플레이스 홀더, null 허용하기 위해 Map.of 대신 HashMap 사용
        Map<String, Object> variables = new HashMap<>();

        variables.put("principleCheckAndImage", principleCheckAndImage);
        variables.put("symbol", symbol);
        variables.put("price", price);
        variables.put("volume", volume);
        variables.put("orderType", orderType);

        // 플레이스 홀더 넣은 유저 메시지 구성
        Message userMessage = promptTemplate.createMessage(variables);

        String modelName = "gpt-4.1-nano";

        // 옵션: 최대 토큰 수 지정 등
        OpenAiChatOptions options = new OpenAiChatOptions.Builder().model(modelName).maxTokens(500).build();

        Prompt prompt = new Prompt(List.of(systemMessage, userMessage), options);

        // 동기 처리
        ChatResponse response = chatModel.call(prompt);

        String text = response.getResult().getOutput().getText();

        // JSON 문자열 text(LLM 응답)을 자바 객체로 LLM 응답용 dto 에
        LlmResponse dto = objectMapper.readValue(text, LlmResponse.class);

        // 디비에서 JSON으로 저장되는 건 다시 JSON 문자열로 변환
        String keepJson = objectMapper.writeValueAsString(dto.keep());
        String improveJson = objectMapper.writeValueAsString(dto.fix());
        String nextTimeJson = objectMapper.writeValueAsString(dto.next());

        // 피드백 객체 생성
        Feedback feedback = Feedback.builder()
            .title(dto.badge())
            .keep(keepJson)
            .improve(improveJson)
            .nextTime(nextTimeJson)
            .retrospection(retrospection)
            .keptCount(keptCount)
            .neutralCount(neutralCount)
            .notKeptCount(notKeptCount)
            .symbol(symbol)
            .price(price)
            .orderType(orderType)
            .volume(volume)
            .build();

        // 저장
        feedbackRepository.save(feedback);

        // 반환
        return feedback;
    }
}
