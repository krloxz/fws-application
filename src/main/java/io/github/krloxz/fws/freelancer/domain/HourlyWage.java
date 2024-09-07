package io.github.krloxz.fws.freelancer.domain;

import java.math.BigDecimal;
import java.util.Currency;

import org.immutables.value.Value;
import org.jmolecules.ddd.annotation.ValueObject;

@ValueObject
@Value.Immutable
public interface HourlyWage {

  BigDecimal amount();

  Currency currency();

  static HourlyWage of(final BigDecimal value, final String currency) {
    return ImmutableHourlyWage.builder()
        .amount(value)
        .currency(Currency.getInstance(currency))
        .build();
  }
}
