package com.ogd.stockdiary.domain.report.usecase;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ogd.stockdiary.domain.report.entity.Feedback;
import com.ogd.stockdiary.domain.report.port.in.CreateFeedbackCommand;

public interface CreateFeedbackUseCase {

    Feedback createFeedbackUseCase(CreateFeedbackCommand command) throws JsonProcessingException;
}
