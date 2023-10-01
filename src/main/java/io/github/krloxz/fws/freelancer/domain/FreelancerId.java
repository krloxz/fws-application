package io.github.krloxz.fws.freelancer.domain;

import java.util.UUID;

import org.immutables.value.Value;

@Value.Immutable
public interface FreelancerId {

  UUID value();

  static FreelancerId create() {
    return of(UUID.randomUUID());
  }

  static FreelancerId of(final UUID id) {
    return ImmutableFreelancerId.builder().value(id).build();
  }

}
