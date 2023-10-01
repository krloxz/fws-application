package io.github.krloxz.fws.freelancer.domain;

import java.util.Optional;

import org.immutables.value.Value;

@Value.Immutable
public interface PersonName {

  String first();

  String last();

  Optional<String> middle();

  static PersonName of(final String first, final String last) {
    return ImmutablePersonName.builder()
        .first(first)
        .last(last)
        .build();
  }

  static PersonName of(final String first, final String last, final String middle) {
    return ImmutablePersonName.builder()
        .first(first)
        .last(last)
        .middle(middle)
        .build();
  }
}
