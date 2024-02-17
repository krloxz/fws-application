package io.github.krloxz.fws.freelancer.domain;

import java.util.Optional;

import org.immutables.value.Value;

@Value.Immutable
public interface PersonName {

  String first();

  String last();

  Optional<String> middle();

  static ImmutablePersonName.Builder builder() {
    return ImmutablePersonName.builder();
  }

}
