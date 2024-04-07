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
   * Creates a new instance of freelancer class with the given address.
   *
   * @param newAddress
   *        the new address
   * @return a new instance of freelancer class with the given address
   */
  public Freelancer movedTo(final Address newAddress) {
    return Freelancer.builder()
        .from(this)
        .address(newAddress)
        .build();
  }

  /**
   * Adds a new nickname to this freelancer.
   *
   * @param nickName
   *        the new nickname
   * @return a new instance of freelancer with the added nickname
   */
  public Freelancer add(final String nickName) {
    return Freelancer.builder()
        .from(this)
        .addNicknames(nickName)
        .build();
  }

  /**
   * Adds a new communication channel to this freelancer.
   *
   * @param channel
   *        the new communication channel
   * @return a new instance of freelancer with the added communication channel
   */
  public Freelancer add(final CommunicationChannel channel) {
    return Freelancer.builder()
        .from(this)
        .addCommunicationChannels(channel)
        .build();
  }

  /**
   * Removes a nickname from this freelancer.
   *
   * @param nickName
   *        the nickname to be removed
   * @return a new instance of freelancer with the nickname removed
   */
  public Freelancer remove(final String nickName) {
    final var newNicknames = nicknames().stream().filter(nick -> !nick.equals(nickName)).toList();
    return Freelancer.builder()
        .from(this)
        .nicknames(newNicknames)
        .build();
  }

  /**
   * Removes a communication channel from this freelancer.
   *
   * @param id
   *        the identifier of the communication channel to be removed
   * @return an {@link Optional} with a new instance of freelancer with the communication channel
   *         removed when the channel was found, an empty {@link Optional} otherwise
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
