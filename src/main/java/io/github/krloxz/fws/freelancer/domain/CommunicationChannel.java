package io.github.krloxz.fws.freelancer.domain;

import org.immutables.value.Value;

@Value.Immutable
public interface CommunicationChannel {

  String value();

  Type type();

  static CommunicationChannel of(final String value, final Type type) {
    return ImmutableCommunicationChannel.builder()
        .value(value)
        .type(type)
        .build();
  }

  enum Type {
    MOBILE, PHONE, EMAIL, URL;
  }
}
