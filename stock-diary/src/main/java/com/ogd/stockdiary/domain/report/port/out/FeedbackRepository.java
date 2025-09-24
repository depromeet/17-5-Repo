package com.ogd.stockdiary.domain.report.port.out;

import com.ogd.stockdiary.domain.report.entity.Feedback;

public interface FeedbackRepository {

    Feedback save(Feedback feedback);
}
