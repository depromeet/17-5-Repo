package com.ogd.stockdiary.domain.report.port.in;

import com.ogd.stockdiary.domain.report.entity.Feedback;

public interface CreateFeedbackUseCase {

    Feedback createFeedbackUseCase(CreateFeedbackCommand command);
}
