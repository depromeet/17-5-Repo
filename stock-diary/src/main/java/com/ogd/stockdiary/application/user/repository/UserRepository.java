package com.ogd.stockdiary.application.user.repository;

import com.ogd.stockdiary.domain.user.entity.OAuthProvider;
import com.ogd.stockdiary.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    
    @Query("SELECT u FROM User u WHERE u.oAuthProviderInfo.oauthProvider = :provider AND u.oAuthProviderInfo.subject = :subject")
    Optional<User> findByOAuthProviderAndSubject(@Param("provider") OAuthProvider provider, @Param("subject") String subject);
    
    Optional<User> findByEmail(String email);
}