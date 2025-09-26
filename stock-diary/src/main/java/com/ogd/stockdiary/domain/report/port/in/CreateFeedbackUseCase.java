package com.ogd.stockdiary.domain.report.port.in;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ogd.stockdiary.domain.report.entity.Feedback;

public interface CreateFeedbackUseCase {

    Feedback createFeedbackUseCase(CreateFeedbackCommand command) throws JsonProcessingException;
}
