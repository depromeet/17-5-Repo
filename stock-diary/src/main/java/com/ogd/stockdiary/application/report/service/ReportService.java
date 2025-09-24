package com.ogd.stockdiary.application.report.service;

import java.util.List;
import java.util.Map;

import jakarta.transaction.Transactional;

import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Service;

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

    @Override
    @Transactional
    public Feedback createFeedbackUseCase(CreateFeedbackCommand command) {

        Retrospection retrospection = retrospectionRepository.getById(command.retrospectionId());

        RetrospectionForReport retrospectionForReport =
                retrospectionForReportRepository.getById(command.retrospectionId());

        String symbol = retrospectionForReport.getSymbol();
        String market = retrospectionForReport.getMarket();
        Order order = retrospectionForReport.getOrder();

        String systemText =
                """
                        Today symbol is {symbol} and market is {market}.Order is {order}.
                        """;

        Message systemMessage =
                new SystemPromptTemplate(systemText)
                        .createMessage(Map.of("symbol", symbol, "market", market, "order", order));

        Message userMessage = new UserMessage(reportPromptLoader.getPrompt());

        OpenAiChatOptions options =
                new OpenAiChatOptions.Builder().model(command.modelName()).maxTokens(200).build();

        Prompt prompt = new Prompt(List.of(systemMessage, userMessage), options);

        // 동기 처리
        ChatResponse response = chatModel.call(prompt);

        String text = response.getResult().getOutput().getText();

        // 피드백 객체 생성
        Feedback feedback = new Feedback(text, retrospection);

        // 저장
        feedbackRepository.save(feedback);

        // 반환
        return feedback;
    }
}
