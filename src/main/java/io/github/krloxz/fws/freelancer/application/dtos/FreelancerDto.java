package io.github.krloxz.fws.freelancer.application.dtos;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import org.hibernate.validator.constraints.UUID;
import org.immutables.builder.Builder;
import org.springframework.hateoas.server.core.Relation;

import io.github.krloxz.fws.freelancer.domain.Freelancer;
import io.github.krloxz.fws.freelancer.domain.Gender;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Positive;

/**
 * DTO to carry a {@link Freelancer}.
 *
 * @author Carlos Gomez
 */
@Relation(collectionRelation = "freelancers")
public record FreelancerDto(
    Optional<@UUID String> id,
    @NotBlank String firstName,
    @NotBlank String lastName,
    Optional<String> middleName,
    Optional<Gender> gender,
    @NotNull @Past LocalDate birthDate,
    @NotNull @Valid AddressDto address,
    @NotNull @Valid HourlyWageDto wage,
    @NotNull @Positive Integer weeklyAvailability,
    Set<@NotBlank String> nicknames,
    Set<CommunicationChannelDto> communicationChannels) {

  @Builder.Constructor
  public FreelancerDto {}

}
