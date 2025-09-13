package com.ogd.stockdiary.domain.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Setter
  @Column(nullable = false, length = 100)
  private String nickname;

  @Column(nullable = false, unique = true, length = 255)
  private String email;

  @Column(name = "profile_image_url", length = 500)
  private String profileImageUrl;

  @Embedded private OAuthProviderInfo oAuthProviderInfo;

  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @Setter
  @Column(name = "is_deleted", nullable = false)
  private Boolean isDeleted = false;

  @PrePersist
  protected void onCreate() {
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
  }

  @PreUpdate
  protected void onUpdate() {
    this.updatedAt = LocalDateTime.now();
  }

  public User(
      String nickname, String email, String profileImageUrl, OAuthProviderInfo oAuthProviderInfo) {
    this.nickname = nickname;
    this.email = email;
    this.profileImageUrl = profileImageUrl;
    this.oAuthProviderInfo = oAuthProviderInfo;
  }
}
