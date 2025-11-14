package com.ogd.stockdiary.application.report.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;

import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ogd.stockdiary.application.report.dto.Response.*;
import com.ogd.stockdiary.domain.analysis.port.PromptLoader;
import com.ogd.stockdiary.domain.principlecheck.entity.PrincipleCheckStatus;
import com.ogd.stockdiary.domain.report.entity.Feedback;
import com.ogd.stockdiary.domain.report.entity.RetrospectionForReport;
import com.ogd.stockdiary.domain.report.port.in.CreateFeedbackCommand;
import com.ogd.stockdiary.domain.report.port.in.GetFeedbackCommand;
import com.ogd.stockdiary.domain.report.port.out.FeedbackRepository;
import com.ogd.stockdiary.domain.report.port.out.ReportDataPort;
import com.ogd.stockdiary.domain.report.port.out.ReportPromptLoader;
import com.ogd.stockdiary.domain.report.port.out.RetrospectionForReportRepository;
import com.ogd.stockdiary.domain.report.usecase.CreateFeedbackUseCase;
import com.ogd.stockdiary.domain.report.usecase.GetFeedbackUsecase;
import com.ogd.stockdiary.domain.report.vo.ReportSourceData;
import com.ogd.stockdiary.domain.retrospection.entity.OrderType;
import com.ogd.stockdiary.domain.retrospection.entity.Retrospection;
import com.ogd.stockdiary.domain.retrospection.port.out.RetrospectionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportService implements CreateFeedbackUseCase, GetFeedbackUsecase {

    private final RetrospectionForReportRepository retrospectionForReportRepository;
    private final RetrospectionRepository retrospectionRepository;
    private final FeedbackRepository feedbackRepository;
    private final ReportPromptLoader reportPromptLoader;
    private final ChatModel chatModel;
    private final ObjectMapper objectMapper;
    private final ReportDataPort reportDataPort;
    private final PromptLoader promptLoader;

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
        LocalDate date = retrospectionForReport.getCreatedAt().toLocalDate();

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

        // 프롬프트에 JSON 포맷으로 전달
        String principleCheckAndImage = objectMapper.writeValueAsString(checks);

        // 원칙 상태 카운트 집계
        long keptCount = reportSourceData.stream()
            .filter(d -> d.status() == PrincipleCheckStatus.KEPT).count();
        long neutralCount = reportSourceData.stream()
            .filter(d -> d.status() == PrincipleCheckStatus.NEUTRAL).count();

        long notKeptCount = reportSourceData.stream()
            .filter(d -> d.status() == PrincipleCheckStatus.NOT_KEPT).count();

        LlmResponse llmResponse = analyzeWithMultiAgent(symbol, price, volume, orderType, date, principleCheckAndImage);

        // 디비에서 JSON으로 저장되는 건 다시 JSON 문자열로 변환
        String keepJson = objectMapper.writeValueAsString(llmResponse.keep());
        String improveJson = objectMapper.writeValueAsString(llmResponse.fix());
        String nextTimeJson = objectMapper.writeValueAsString(llmResponse.next());

        // 피드백 객체 생성
        Feedback feedback = Feedback.builder()
            .title(llmResponse.badge())
            .keep(keepJson)
            .user(retrospection.getUser())
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

    public LlmResponse analyzeWithMultiAgent(String symbol,
        BigDecimal price,
        Integer volume,
        OrderType orderType,
        LocalDate date,
        String principleCheckAndImage) throws JsonProcessingException {

        // 병렬로 리서치 진행
        CompletableFuture<MarketData> marketDataFuture = collectMarketDataAsync(symbol, date);
        CompletableFuture<TechnicalAnalysis> technicalAnalysisFuture = analyzeTechnicalAsync(symbol);
        CompletableFuture<FundamentalAnalysis> fundamentalAnalysisFuture = analyzeFundamentalAsync(symbol);

        // 모든 분석 완료 대기(비동기 메서드 여러 개이므로)
        CompletableFuture.allOf(marketDataFuture, technicalAnalysisFuture, fundamentalAnalysisFuture).join();

        // 리서치 객체 생성
        Research research = synthesizeResearch(
            marketDataFuture.join(),
            technicalAnalysisFuture.join(),
            fundamentalAnalysisFuture.join());

        // 최종 판단
        LlmResponse llmResponse = generateExpertJudgement(research, symbol, price, volume, orderType, date,
            principleCheckAndImage);

        return llmResponse;

    }

    @Async("threadPoolTaskExecutor")
    public CompletableFuture<MarketData> collectMarketDataAsync(String symbol, LocalDate date) {
        return CompletableFuture.supplyAsync(() -> getAndCacheMarketData(symbol, date));
    }

    @Cacheable(value = "marketData", key = "{#symbol,#date}")
    public MarketData getAndCacheMarketData(String symbol, LocalDate date) {

        try {
            // instruction
            String systemPrompt = """

                Return structured market data by adhering strictly to the JSON format provided below.
                Provide the analysis concisely and briefly.

                Example:
                {
                    "symbol": "005930",
                    "trend1w": "+2.3%",
                    "trend1m": "-1.6%",
                    "trend6m": "+15.4%"
                }



                """;

            String userPrompt = """
                Collect market data for {symbol} on {date}:
                - Current price and volume
                - 1 week, 1 month, 6 month price history
                - Trading volume patterns

                """;

            PromptTemplate promptTemplate = new PromptTemplate(userPrompt);
            Map<String, Object> variables = new HashMap<>();
            variables.put("symbol", symbol);
            variables.put("date", date);
            Message userMessage = promptTemplate.createMessage(variables);

            String modelName = "gpt-3.5-turbo-0125";
            OpenAiChatOptions options = new OpenAiChatOptions.Builder().model(modelName).maxTokens(700).build();

            SystemMessage systemMessage = new SystemMessage(systemPrompt);
            Prompt prompt = new Prompt(List.of(systemMessage, userMessage), options);

            // .call()은 동기 호출
            ChatResponse response = chatModel.call(prompt);

            String text = response.getResult().getOutput().getText();

            // JSON 문자열 text(LLM 응답)을 자바 객체로 LLM 응답용 dto 에
            MarketData marketDataLlmResponse = objectMapper.readValue(text, MarketData.class);

            // 정적 팩토리 메서드로 인스턴스 생성
            MarketData marketData = MarketData.onCreate(marketDataLlmResponse.symbol(), marketDataLlmResponse.trend1w(),
                marketDataLlmResponse.trend1m(), marketDataLlmResponse.trend6m());

            return marketData;

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Async("threadPoolTaskExecutor")
    public CompletableFuture<TechnicalAnalysis> analyzeTechnicalAsync(String symbol) {
        return CompletableFuture.supplyAsync(() -> getAndCacheTechnicalAnalysis(symbol));
    }

    @Cacheable(value = "technicalAnalysis", key = "{#symbol}")
    public TechnicalAnalysis getAndCacheTechnicalAnalysis(String symbol) {

        try {
            String systemPrompt = """

                Return data by adhering strictly to the JSON format provided below.
                Provide the analysis concisely and briefly.

                Example:
                {
                    "rsi": "65.2 (중립)",
                    "macd": "골든크로스 형성",
                    "bollingerBands": "상단 밴드 근접",
                    "movingAverage": "20선 상회, 60선 돌파 대기",
                    "support":260,
                    "resistance":275,
                    "signal": "매수신호"

                }


                """;

            String userPrompt = """
                Perform technical analysis for {symbol}:
                - RSI, MACD, Bollinger Bands
                - 20/60 day moving averages
                - Support and resistance levels
                - Volume analysis

                Provide clear technical signals.


                """;
            PromptTemplate promptTemplate = new PromptTemplate(userPrompt);
            Map<String, Object> variables = new HashMap<>();
            variables.put("symbol", symbol);
            Message userMessage = promptTemplate.createMessage(variables);

            String modelName = "gpt-3.5-turbo-0125";
            OpenAiChatOptions options = new OpenAiChatOptions.Builder().model(modelName).maxTokens(500).build();

            Message systemMessage = new SystemMessage(systemPrompt);
            Prompt prompt = new Prompt(List.of(systemMessage, userMessage), options);

            // .call()은 동기 호출
            ChatResponse response = chatModel.call(prompt);

            String text = response.getResult().getOutput().getText();

            TechnicalAnalysis technicalAnalysisLlmResponse = objectMapper.readValue(text, TechnicalAnalysis.class);
            TechnicalAnalysis technicalAnalysis = TechnicalAnalysis.onCreate(
                technicalAnalysisLlmResponse.rsi(),
                technicalAnalysisLlmResponse.macd(),
                technicalAnalysisLlmResponse.bollingerBands(),
                technicalAnalysisLlmResponse.movingAverage(),
                technicalAnalysisLlmResponse.support(),
                technicalAnalysisLlmResponse.resistance(),
                technicalAnalysisLlmResponse.signal());

            return technicalAnalysis;

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    @Async("threadPoolTaskExecutor")
    public CompletableFuture<FundamentalAnalysis> analyzeFundamentalAsync(String symbol) {
        return CompletableFuture.supplyAsync(() -> getAndCacheFundamentalAnalysis(symbol));
    }

    @Cacheable(value = "fundamentalAnalysis", key = "{#symbol}")
    public FundamentalAnalysis getAndCacheFundamentalAnalysis(String symbol) {

        try {
            String systemPrompt = """

                Return data by adhering strictly to the JSON format provided below.
                Provide the analysis concisely and briefly.

                Example:
                {
                    "earnings": "Q3 실적 예상치 상회",
                    "peRatio": "28.5 (적정 수준)",
                    "sectorTrend": "기술주 강세 지속",
                    "macroFactors": "Fed 금리 동결 긍정적",
                    "recommendation": "매수 유지"

                }

                """;

            String userPrompt = """
                Analyze fundamental factors for {symbol}:
                - Recent earnings and financial health
                - Industry trends and competitive position
                - Macro economic factors (Fed policy, inflation)
                - Sector performance


                """;
            PromptTemplate promptTemplate = new PromptTemplate(userPrompt);
            Map<String, Object> variables = new HashMap<>();
            variables.put("symbol", symbol);
            Message userMessage = promptTemplate.createMessage(variables);

            String modelName = "gpt-3.5-turbo-0125";
            OpenAiChatOptions options = new OpenAiChatOptions.Builder().model(modelName).maxTokens(500).build();
            SystemMessage systemMessage = new SystemMessage(systemPrompt);

            Prompt prompt = new Prompt(List.of(systemMessage, userMessage), options);

            // .call()은 동기 호출
            ChatResponse response = chatModel.call(prompt);

            String text = response.getResult().getOutput().getText();

            FundamentalAnalysis fundamentalAnalysisLlmResponse = objectMapper.readValue(text,
                FundamentalAnalysis.class);

            FundamentalAnalysis analysis = FundamentalAnalysis.onCreate(fundamentalAnalysisLlmResponse.earnings(),
                fundamentalAnalysisLlmResponse.peRatio(), fundamentalAnalysisLlmResponse.sectorTrend(),
                fundamentalAnalysisLlmResponse.macroFactors(), fundamentalAnalysisLlmResponse.recommendation());

            return analysis;

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public Research synthesizeResearch(MarketData marketData, TechnicalAnalysis technicalAnalysis,
        FundamentalAnalysis fundamentalAnalysis) throws JsonProcessingException {

        String systemPrompt = """

            Return data by adhering strictly to the JSON format provided below.
            Provide the analysis concisely and briefly.

            Example:
            {
                "marketContext": "AAPL은 현재 기술적 돌파 구간에서 거래 중. 20일선 상회 후 60일선 돌파 대기 상태.",
                "investmentThesis": "Q3 실적 호조와 AI 관련 성장 모멘텀으로 상승 여력 존재",
                "riskFactors": "Fed 정책 변화, 중국 시장 불확실성, 고평가 우려",
                "priceTarget": "목표가 280, 손절가 255",
                "technicalSummary": "매수신호",
                "fundamentalSummary": "매수 유지"


            }


            """;

        String userPrompt = """
            Synthesize comprehensive research report:

            Market Data: {marketData}
            Technical Analysis: {techAnalysis}
            Fundamental Analysis: {fundAnalysis}

            Create unified research summary with:
            - Overall market context
            - Key investment thesis
            - Risk factors
            - Price targets
            """;

        PromptTemplate promptTemplate = new PromptTemplate(userPrompt);

        Map<String, Object> variables = new HashMap<>();
        // JSON 포맷으로 바꾸어서 프롬프트에 넣기
        variables.put("marketData", objectMapper.writeValueAsString(marketData));
        variables.put("techAnalysis", objectMapper.writeValueAsString(technicalAnalysis));
        variables.put("fundAnalysis", objectMapper.writeValueAsString(fundamentalAnalysis));
        Message userMessage = promptTemplate.createMessage(variables);

        String modelName = "gpt-3.5-turbo-0125";
        OpenAiChatOptions options = new OpenAiChatOptions.Builder().model(modelName).maxTokens(500).build();

        SystemMessage systemMessage = new SystemMessage(systemPrompt);
        Prompt prompt = new Prompt(List.of(systemMessage, userMessage), options);

        // .call()은 동기 호출
        ChatResponse response = chatModel.call(prompt);

        String text = response.getResult().getOutput().getText();
        Research researchLlmResponse = objectMapper.readValue(text, Research.class);

        Research research = Research.onCreate(
            researchLlmResponse.marketContext(),
            researchLlmResponse.investmentThesis(),
            researchLlmResponse.riskFactors(),
            researchLlmResponse.priceTarget(),
            technicalAnalysis.signal(),
            fundamentalAnalysis.recommendation());

        return research;

    }

    public LlmResponse generateExpertJudgement(Research research, String symbol, BigDecimal price, Integer volume,
        OrderType orderType, LocalDate date, String principleCheckAndImage) throws JsonProcessingException {

        String userPrompt = """
            Based on {research}:

            --- Trade Details ---
                Symbol: {symbol}
                Order Price: {price}
                Order Volume: {volume}
                Order Type: {orderType}
                date: {date}

            --- User Reflection ---
               Principle Check and Image Data: {principleCheckAndImage}


            """;

        // 시스템 메시지를 로더에서 불러오기
        Message systemMessage = new SystemMessage(reportPromptLoader.getPrompt());

        PromptTemplate promptTemplate = new PromptTemplate(userPrompt);

        // 플레이스 홀더, null 허용하기 위해 Map.of 대신 HashMap 사용
        Map<String, Object> variables = new HashMap<>();

        variables.put("research", research);
        variables.put("principleCheckAndImage", principleCheckAndImage);
        variables.put("symbol", symbol);
        variables.put("price", price);
        variables.put("volume", volume);
        variables.put("orderType", orderType);
        variables.put("date", date);

        // 플레이스 홀더 넣은 유저 메시지 구성
        Message userMessage = promptTemplate.createMessage(variables);

        // String modelName = "gpt-4.1-nano";
        String modelName = "gpt-3.5-turbo-0125";

        // 옵션: 최대 토큰 수 지정 등
        OpenAiChatOptions options = new OpenAiChatOptions.Builder().model(modelName).maxTokens(700).build();

        Prompt prompt = new Prompt(List.of(systemMessage, userMessage), options);

        ChatResponse response = chatModel.call(prompt);
        String text = response.getResult().getOutput().getText();

        LlmResponse llmResponse = objectMapper.readValue(text, LlmResponse.class);

        return llmResponse;

    }

    @Override
    public BadgeResponse getAllFeedbackUsecase(GetFeedbackCommand command) {
        List<Feedback> allFeedback = feedbackRepository.findAllByUserId(command.userId());

        Map<String, Long> badgeCounts = allFeedback.stream()
            .collect(Collectors.groupingBy(Feedback::getTitle, Collectors.counting()));

        int hedge = badgeCounts.getOrDefault("hedge", 0L).intValue();
        int bronze = badgeCounts.getOrDefault("bronze", 0L).intValue();
        int silver = badgeCounts.getOrDefault("silver", 0L).intValue();
        int gold = badgeCounts.getOrDefault("gold", 0L).intValue();

        int all = hedge + bronze + silver + gold;
        int percentage = 0;

        if (all != 0) {
            // double로 계산
            double tempPercentage = (hedge + gold) * 100.0 / all;

            // 반올림하여 int으로 변환
            percentage = (int) Math.round(tempPercentage);
        }

        BadgeResponse badgeResponse = new BadgeResponse(hedge, bronze, silver, gold, percentage);

        return badgeResponse;

    }
}
