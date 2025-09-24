package com.ogd.stockdiary.domain.analysis.application;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Service;

import com.ogd.stockdiary.domain.analysis.port.PromptLoader;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;

@Service
@AllArgsConstructor
public class AnalysisService {
    private final PromptLoader promptLoader;
    private final ChatModel chatModel;

    public ChatResponse analyze(
            String modelName, String market, String symbol, LocalDateTime time) {
        String systemText =
                """
            Today market is {market} and symbol is {symbol}.
            Time is {time}.
            You should reply to the user's request.
            """;
        Message systemMessage =
                new SystemPromptTemplate(systemText)
                        .createMessage(
                                Map.of(
                                        "market", market,
                                        "symbol", symbol,
                                        "time", time.toString()));
        Message userMessage = new UserMessage(promptLoader.getPrompt());

        OpenAiChatOptions options =
                OpenAiChatOptions.builder().model(modelName).maxTokens(250).build();

        Prompt prompt = new Prompt(List.of(systemMessage, userMessage), options);

        ChatResponse chatResponse = chatModel.call(prompt);

        return chatResponse;
    }
    public Flux<ChatResponse> analyzeStreamData(
            String modelName, String market, String symbol, LocalDateTime time) {
        String systemText =
                """
            Today market is {market} and symbol is {symbol}.
            Time is {time}.
            You should reply to the user's request.
            """;
        Message systemMessage =
                new SystemPromptTemplate(systemText)
                        .createMessage(
                                Map.of(
                                        "market", market,
                                        "symbol", symbol,
                                        "time", time.toString()));
        Message userMessage = new UserMessage(promptLoader.getPrompt());

        OpenAiChatOptions options =
                OpenAiChatOptions.builder().model(modelName).maxTokens(250).build();

        Prompt prompt = new Prompt(List.of(systemMessage, userMessage), options);

        Flux<ChatResponse> chatResponse = chatModel.stream(prompt);

        return chatResponse;
    }
}
