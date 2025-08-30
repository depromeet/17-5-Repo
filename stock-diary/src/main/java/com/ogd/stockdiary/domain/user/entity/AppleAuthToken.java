package com.ogd.stockdiary.domain.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "apple_auth_tokens")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AppleAuthToken {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Setter
    @Column(name = "refresh_token", nullable = false, length = 1000)
    private String refreshToken;
}