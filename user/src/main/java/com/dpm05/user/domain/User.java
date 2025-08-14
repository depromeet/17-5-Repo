package com.dpm05.user.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

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
    
    @Embedded
    private OAuthProviderInfo oAuthProviderInfo;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Setter
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;
    
    public User(String nickname, String email, String profileImageUrl, OAuthProviderInfo oAuthProviderInfo) {
        this.nickname = nickname;
        this.email = email;
        this.profileImageUrl = profileImageUrl;
        this.oAuthProviderInfo = oAuthProviderInfo;
    }
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
