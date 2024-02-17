package io.github.krloxz.fws.freelancer.application.dtos;

import java.util.Optional;

import org.immutables.builder.Builder;

import io.github.krloxz.fws.freelancer.domain.Address;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO to carry an {@link Address}.
 *
 * @author Carlos Gomez
 */
public record AddressDto(
    @NotBlank String street,
    Optional<String> apartment,
    @NotBlank String city,
    @NotBlank String state,
    @NotBlank String zipCode,
    @NotBlank String country) {

  @Builder.Constructor
  public AddressDto {}

}
