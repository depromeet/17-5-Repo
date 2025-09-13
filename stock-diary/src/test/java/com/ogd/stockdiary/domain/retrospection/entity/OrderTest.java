package com.ogd.stockdiary.domain.retrospection.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ogd.stockdiary.domain.user.entity.OAuthProvider;
import com.ogd.stockdiary.domain.user.entity.OAuthProviderInfo;
import com.ogd.stockdiary.domain.user.entity.User;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OrderTest {

  private User createTestUser() {
    OAuthProviderInfo oAuthProviderInfo =
        new OAuthProviderInfo(OAuthProvider.APPLE, "test-provider-id");
    return new User("testUser", "test@example.com", "profile.jpg", oAuthProviderInfo);
  }

  @Test
  @DisplayName("올바른 주문 정보로 Order를 생성할 수 있다")
  void createValidOrder() {
    // given
    User user = createTestUser();
    Retrospection retrospection = new Retrospection(user, "005930", "KRX");
    OrderType orderType = OrderType.BUY;
    BigDecimal price = new BigDecimal("10000");
    Currency currency = Currency.KRW;
    Integer volume = 10;
    LocalDate orderDate = LocalDate.of(2025, 9, 13);

    // when
    Order order = new Order(retrospection, orderType, price, currency, volume, orderDate);

    // then
    assertThat(order.getRetrospection()).isEqualTo(retrospection);
    assertThat(order.getOrderType()).isEqualTo(orderType);
    assertThat(order.getPrice()).isEqualTo(price);
    assertThat(order.getCurrency()).isEqualTo(currency);
    assertThat(order.getVolume()).isEqualTo(volume);
    assertThat(order.getOrderDate()).isEqualTo(orderDate);
  }

  @Test
  @DisplayName("가격이 0 이하일 때 예외가 발생한다")
  void invalidPrice() {
    // given
    User user = createTestUser();
    Retrospection retrospection = new Retrospection(user, "005930", "KRX");
    OrderType orderType = OrderType.BUY;
    BigDecimal invalidPrice = BigDecimal.ZERO;
    Currency currency = Currency.KRW;
    Integer volume = 10;
    LocalDate orderDate = LocalDate.of(2025, 9, 13);

    // when & then
    assertThatThrownBy(
            () -> new Order(retrospection, orderType, invalidPrice, currency, volume, orderDate))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("가격은 0보다 큰 값이어야 합니다.");
  }

  @Test
  @DisplayName("거래량이 0 이하일 때 예외가 발생한다")
  void invalidVolume() {
    // given
    User user = createTestUser();
    Retrospection retrospection = new Retrospection(user, "005930", "KRX");
    OrderType orderType = OrderType.BUY;
    BigDecimal price = new BigDecimal("10000");
    Currency currency = Currency.KRW;
    Integer invalidVolume = 0;
    LocalDate orderDate = LocalDate.of(2025, 9, 13);

    // when & then
    assertThatThrownBy(
            () -> new Order(retrospection, orderType, price, currency, invalidVolume, orderDate))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("거래량은 0보다 큰 값이어야 합니다.");
  }

  @Test
  @DisplayName("가격이 null일 때 예외가 발생한다")
  void nullPrice() {
    // given
    User user = createTestUser();
    Retrospection retrospection = new Retrospection(user, "005930", "KRX");
    OrderType orderType = OrderType.BUY;
    BigDecimal nullPrice = null;
    Currency currency = Currency.KRW;
    Integer volume = 10;
    LocalDate orderDate = LocalDate.of(2025, 9, 13);

    // when & then
    assertThatThrownBy(
            () -> new Order(retrospection, orderType, nullPrice, currency, volume, orderDate))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("가격은 0보다 큰 값이어야 합니다.");
  }

  @Test
  @DisplayName("거래량이 null일 때 예외가 발생한다")
  void nullVolume() {
    // given
    User user = createTestUser();
    Retrospection retrospection = new Retrospection(user, "005930", "KRX");
    OrderType orderType = OrderType.BUY;
    BigDecimal price = new BigDecimal("10000");
    Currency currency = Currency.KRW;
    Integer nullVolume = null;
    LocalDate orderDate = LocalDate.of(2025, 9, 13);

    // when & then
    assertThatThrownBy(
            () -> new Order(retrospection, orderType, price, currency, nullVolume, orderDate))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("거래량은 0보다 큰 값이어야 합니다.");
  }
}
