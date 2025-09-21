package com.ogd.stockdiary.domain.investmentprinciple.entity;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;

import com.ogd.stockdiary.domain.user.entity.User;

@Entity
@Table(name = "investment_principles")
@Getter
@NoArgsConstructor
public class InvestmentPrinciple {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  private String principle;

  @Column(updatable = false)
  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;

  @PrePersist
  protected void onCreate() {
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
  }

  @PreUpdate
  protected void onUpdate() {
    this.updatedAt = LocalDateTime.now();
  }

  public static InvestmentPrinciple create(User user, String principle) {
    InvestmentPrinciple investmentPrinciple = new InvestmentPrinciple();
    investmentPrinciple.user = user;
    investmentPrinciple.principle = principle;
    return investmentPrinciple;
  }

  public void updatePrinciple(String principle) {
    this.principle = principle;
  }
}
