package io.github.krloxz.fws.freelancer.domain;

import java.util.Optional;

import org.immutables.value.Value;

@Value.Immutable
public interface Address {

  String street();

  Optional<String> apartment();

  String city();

  String state();

  @Value.Default
  default String country() {
    return "USA";
  }

  String zipCode();

  static ImmutableAddress.Builder builder() {
    return ImmutableAddress.builder();
  }
}
