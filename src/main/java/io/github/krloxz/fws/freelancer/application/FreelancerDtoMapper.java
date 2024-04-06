package io.github.krloxz.fws.freelancer.application;

import java.util.Optional;
import java.util.UUID;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import io.github.krloxz.fws.freelancer.application.dtos.AddressDto;
import io.github.krloxz.fws.freelancer.application.dtos.CommunicationChannelDto;
import io.github.krloxz.fws.freelancer.application.dtos.FreelancerDto;
import io.github.krloxz.fws.freelancer.domain.Address;
import io.github.krloxz.fws.freelancer.domain.CommunicationChannel;
import io.github.krloxz.fws.freelancer.domain.Freelancer;
import io.github.krloxz.fws.freelancer.domain.FreelancerId;

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

  @InheritInverseConfiguration
  public abstract Freelancer fromDto(FreelancerDto dto);

  public abstract Address fromAddressDto(AddressDto dto);

  public abstract CommunicationChannel fromDto(CommunicationChannelDto dto);

  FreelancerId fromDtoId(final Optional<String> id) {
    return id.map(UUID::fromString)
        .map(FreelancerId::of)
        .orElseGet(FreelancerId::create);
  }

  Optional<String> toDtoId(final FreelancerId id) {
    return Optional.of(id.value().toString());
  }

}
