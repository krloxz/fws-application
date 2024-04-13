package io.github.krloxz.fws.freelancer.domain;

import static java.util.stream.Collectors.toSet;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.immutables.value.Value;

/**
 * Represents a freelancer.
 *
 * @author Carlos Gomez
 */
@Value.Immutable
@Value.Style(depluralize = true)
public abstract class Freelancer {

  /**
   * @return the unique identifier of this freelancer
   */
  public abstract UUID id();

  /**
   * @return the name of this freelancer
   */
  public abstract PersonName name();

  /**
   * @return the email of this freelancer
   */
  public abstract Optional<Gender> gender();

  /**
   * @return the birth date of this freelancer
   */
  public abstract LocalDate birthDate();

  // TODO: add speciality and skills

  /**
   * @return the address of this freelancer
   */
  public abstract Address address();

  /**
   * @return the hourly wage of this freelancer
   */
  public abstract HourlyWage wage();

  /**
   * @return the nicknames of this freelancer
   */
  public abstract Set<String> nicknames();

  /**
   * @return the communication channels of this freelancer
   */
  public abstract Set<CommunicationChannel> communicationChannels();

  /**
   * Creates a new builder for the Freelancer class.
   *
   * @return a new instance of the builder
   */
  public static ImmutableFreelancer.Builder builder() {
    return ImmutableFreelancer.builder();
  }

  /**
   * Updates the address of this freelancer.
   *
   * @param newAddress
   *        the new address
   * @return a copy of this freelancer with the new address
   */
  public Freelancer movedTo(final Address newAddress) {
    return Freelancer.builder()
        .from(this)
        .address(newAddress)
        .build();
  }

  /**
   * Updates the nicknames of this freelancer.
   *
   * @param nicknames
   *        the new nicknames
   * @return a copy of this freelancer with the given nicknames
   */
  public Freelancer withNicknames(final String... nicknames) {
    return Freelancer.builder()
        .from(this)
        .nicknames(Set.of(nicknames))
        .build();
  }

  /**
   * Adds a new communication channel to this freelancer.
   *
   * @param channel
   *        the new communication channel
   * @return a copy of this freelancer with the new communication channel
   */
  public Freelancer addCommunicationChannel(final CommunicationChannel channel) {
    return Freelancer.builder()
        .from(this)
        .addCommunicationChannels(channel)
        .build();
  }

  /**
   * Removes a communication channel from this freelancer.
   *
   * @param id
   *        the identifier of the communication channel to be removed
   * @return an {@link Optional} with a copy of this freelancer with the communication channel removed
   *         when the channel was found, an empty {@link Optional} otherwise
   */
  public Optional<Freelancer> removeCommunicationChannel(final UUID id) {
    final var newChannels = communicationChannels().stream()
        .filter(channel -> !channel.id().equals(id))
        .collect(toSet());
    if (newChannels.size() == communicationChannels().size()) {
      return Optional.empty();
    }
    return Optional.of(
        Freelancer.builder()
            .from(this)
            .communicationChannels(newChannels)
            .build());
  }

  /**
   * Updates the hourly wage of this freelancer.
   *
   * @param wage
   *        the new hourly wage
   * @return a new instance of freelancer with the updated hourly wage
   */
  public Freelancer update(final HourlyWage wage) {
    return Freelancer.builder()
        .from(this)
        .wage(wage)
        .build();
  }

}
