package io.github.krloxz.fws.freelancer.application;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import io.github.krloxz.fws.freelancer.application.dtos.AddressDto;
import io.github.krloxz.fws.freelancer.application.dtos.CommunicationChannelDto;
import io.github.krloxz.fws.freelancer.application.dtos.FreelancerDto;
import io.github.krloxz.fws.freelancer.application.dtos.HourlyWageDto;
import io.github.krloxz.fws.freelancer.domain.Address;
import io.github.krloxz.fws.freelancer.domain.CommunicationChannel;
import io.github.krloxz.fws.freelancer.domain.Freelancer;
import io.github.krloxz.fws.freelancer.domain.HourlyWage;

/**
 * Maps {@link Freelancer}'s between the DTO and domain models.
 * <p>
 * Use public methods only, protected methods are designed only to support MapStruct.
 *
 * @author Carlos Gomez
 */
@Mapper(componentModel = "spring")
abstract class FreelancerDtoMapper {

  @Mapping(target = "firstName", source = "name.first")
  @Mapping(target = "lastName", source = "name.last")
  @Mapping(target = "middleName", source = "name.middle")
  public abstract FreelancerDto toDto(Freelancer freelancer);

  public abstract List<FreelancerDto> toDto(List<Freelancer> freelancers);

  @InheritInverseConfiguration
  public abstract Freelancer fromDto(FreelancerDto dto);

  public abstract Address fromDto(AddressDto dto);

  public abstract CommunicationChannel fromDto(CommunicationChannelDto dto);

  public abstract HourlyWage fromDto(HourlyWageDto dto);

  UUID toUUID(final Optional<String> value) {
    return value.map(UUID::fromString)
        .orElse(UUID.randomUUID());
  }

  Optional<String> toOptionalString(final UUID value) {
    return Optional.of(value.toString());
  }

}
