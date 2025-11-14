package com.ogd.stockdiary.application.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ogd.stockdiary.domain.user.entity.OAuthProvider;
import com.ogd.stockdiary.domain.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.oAuthProviderInfo.oauthProvider = :provider AND u.oAuthProviderInfo.subject = :subject AND u.isDeleted=false")
    Optional<User> findByOAuthProviderAndSubject(
        @Param("provider") OAuthProvider provider, @Param("subject") String subject);

    @Query("SELECT u FROM User u WHERE u.id = :id AND u.isDeleted = false")
    Optional<User> findById(@Param("id") Long id);

    @Query("SELECT u FROM User u WHERE u.email = :email AND u.isDeleted = false")
    Optional<User> findByEmail(@Param("email") String email);
}
