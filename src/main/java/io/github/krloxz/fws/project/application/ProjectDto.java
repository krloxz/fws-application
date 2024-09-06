package io.github.krloxz.fws.project.application;

import java.util.Optional;

import org.hibernate.validator.constraints.UUID;
import org.immutables.builder.Builder;
import org.springframework.hateoas.server.core.Relation;

import io.github.krloxz.fws.project.domain.Project;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO to carry a {@link Project}.
 *
 * @author Carlos Gomez
 */
@Relation(collectionRelation = "projects")
public record ProjectDto(
    Optional<@UUID String> id,
    @NotBlank String name,
    @NotBlank String description) {

  @Builder.Constructor
  public ProjectDto {}

  ProjectDto withId(final String id) {
    return new ProjectDto(Optional.of(id), this.name, this.description);
  }

}
