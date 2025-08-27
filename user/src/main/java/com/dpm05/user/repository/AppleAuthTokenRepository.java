package com.dpm05.user.repository;

import com.dpm05.user.domain.AppleAuthToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppleAuthTokenRepository extends JpaRepository<AppleAuthToken, Long> {
}