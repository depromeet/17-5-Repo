package com.ogd.stockdiary.domain.report.usecase;

import com.ogd.stockdiary.application.report.dto.Response.BadgeResponse;
import com.ogd.stockdiary.domain.report.port.in.GetFeedbackCommand;

public interface GetFeedbackUsecase {
    BadgeResponse getAllFeedbackUsecase(GetFeedbackCommand command);
}
