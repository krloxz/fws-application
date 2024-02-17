package io.github.krloxz.fws.freelancer.application.dtos;

import io.github.krloxz.fws.freelancer.domain.CommunicationChannel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO to carry a {@link CommunicationChannel}.
 *
 * @author Carlos Gomez
 */
public record CommunicationChannelDto(
    @NotBlank String value,
    @NotNull CommunicationChannel.Type type) {

}
