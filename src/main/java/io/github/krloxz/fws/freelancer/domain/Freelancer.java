package io.github.krloxz.fws.freelancer.domain;

import java.util.Optional;
import java.util.Set;

import org.immutables.value.Value;

@Value.Immutable
@Value.Style(depluralize = true)
public abstract class Freelancer {

  public abstract FreelancerId id();

  public abstract PersonName name();

  public abstract Optional<Gender> gender();

  // TODO: add speciality and skills

  public abstract Address address();

  public abstract HourlyWage wage();

  public abstract Set<String> nicknames();

  public abstract Set<CommunicationChannel> communicationChannels();

  public static ImmutableFreelancer.Builder builder() {
    return ImmutableFreelancer.builder();
  }

  public Freelancer movedTo(final Address newAddress) {
    return null;
  }

  public Freelancer add(final String nickName) {
    return null;
  }

  public Freelancer add(final CommunicationChannel channel) {
    return null;
  }

  public Freelancer remove(final String nickName) {
    return null;
  }

  public Freelancer remove(final CommunicationChannel channel) {
    return null;
  }

  public Freelancer update(final HourlyWage wage) {
    return null;
  }

}
