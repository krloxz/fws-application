package io.github.krloxz.fws.freelancer.domain;

import java.math.BigDecimal;
import java.util.Currency;

import org.immutables.value.Value;

@Value.Immutable
public interface HourlyWage {

  BigDecimal value();

  Currency currency();

  static HourlyWage of(final BigDecimal value, final String currency) {
    return ImmutableHourlyWage.builder()
        .value(value)
        .currency(Currency.getInstance(currency))
        .build();
  }
}
