package io.github.krloxz.fws.freelancer.domain;

import java.math.BigDecimal;
import java.util.Currency;

import org.immutables.value.Value;

@Value.Immutable
public interface HourlyWage {

  BigDecimal value();

  Currency currency();

  static HourlyWage of(final String value, final String currency) {
    return ImmutableHourlyWage.builder()
        .value(new BigDecimal(value))
        .currency(Currency.getInstance(currency))
        .build();
  }
}
