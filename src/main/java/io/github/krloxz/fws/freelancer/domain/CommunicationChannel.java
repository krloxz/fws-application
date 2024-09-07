package io.github.krloxz.fws.freelancer.domain;

import java.util.UUID;

import org.immutables.value.Value;
import org.jmolecules.ddd.annotation.Entity;
import org.jmolecules.ddd.annotation.Identity;

@Entity
@Value.Immutable
public interface CommunicationChannel {

  @Identity
  @Value.Default
  default UUID id() {
    return UUID.randomUUID();
  }

  String value();

  Type type();

  static ImmutableCommunicationChannel.Builder builder() {
    return ImmutableCommunicationChannel.builder();
  }

  enum Type {
    MOBILE, PHONE, EMAIL, URL;
  }
}
