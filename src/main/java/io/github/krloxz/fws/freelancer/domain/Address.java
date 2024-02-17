package io.github.krloxz.fws.freelancer.domain;

import java.util.Optional;

import org.immutables.value.Value;

@Value.Immutable
public interface Address {

  String street();

  Optional<String> apartment();

  String city();

  String state();

  String zipCode();

  @Value.Default
  default String country() {
    return "USA";
  }

  static ImmutableAddress.Builder builder() {
    return ImmutableAddress.builder();
  }
}
