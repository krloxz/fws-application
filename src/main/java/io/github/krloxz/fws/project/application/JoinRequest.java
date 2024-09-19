package io.github.krloxz.fws.project.application;

import org.hibernate.validator.constraints.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * DTO carrying a request for a freelancer to join a project.
 *
 * @author Carlos Gomez
 */
public record JoinRequest(
    @NotNull @UUID String freelancerId,
    @NotNull @Positive Integer committedHours) {

}
