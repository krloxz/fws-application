package io.github.krloxz.fws.project.application;

import java.util.UUID;

import org.immutables.builder.Builder;

import io.github.krloxz.fws.project.domain.Freelancer;

/**
 * DTO to carry a {@link Freelancer}.
 *
 * @author Carlos Gomez
 */
public record FreelancerDto(UUID id, String firstName, String lastName, int allocatedHours) {

  @Builder.Constructor
  public FreelancerDto {}

}
