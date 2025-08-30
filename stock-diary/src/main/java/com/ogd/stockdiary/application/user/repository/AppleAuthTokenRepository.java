package com.ogd.stockdiary.application.user.repository;

import com.ogd.stockdiary.domain.user.entity.AppleAuthToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppleAuthTokenRepository extends JpaRepository<AppleAuthToken, Long> {
}