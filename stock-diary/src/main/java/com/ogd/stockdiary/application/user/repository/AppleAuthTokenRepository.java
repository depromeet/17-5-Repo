package com.ogd.stockdiary.application.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ogd.stockdiary.domain.user.entity.AppleAuthToken;

public interface AppleAuthTokenRepository extends JpaRepository<AppleAuthToken, Long> {
}
