package io.github.krloxz.fws.freelancer.application.dtos;

import java.util.Optional;

import org.hibernate.validator.constraints.UUID;
import org.immutables.builder.Builder;

import io.github.krloxz.fws.freelancer.domain.CommunicationChannel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO to carry a {@link CommunicationChannel}.
 *
 * @author Carlos Gomez
 */
public record CommunicationChannelDto(
    Optional<@UUID String> id,
    @NotBlank String value,
    @NotNull CommunicationChannel.Type type) {

  @Builder.Constructor
  public CommunicationChannelDto {}

}
