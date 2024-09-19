package io.github.krloxz.fws.project.application;

import java.util.List;
import java.util.Optional;

import org.hibernate.validator.constraints.UUID;
import org.immutables.builder.Builder;
import org.springframework.hateoas.server.core.Relation;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.github.krloxz.fws.project.domain.Project;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * DTO to carry a {@link Project}.
 *
 * @author Carlos Gomez
 */
@Relation(collectionRelation = "projects")
public record ProjectDto(
    Optional<@UUID String> id,
    @NotBlank String name,
    @NotBlank String description,
    @NotNull @Positive Integer requiredHours,
    @JsonIgnore List<FreelancerDto> freelancers) {

  @Builder.Constructor
  public ProjectDto {}

  ProjectDto withId(final String id) {
    return new ProjectDto(Optional.of(id), this.name, this.description, this.requiredHours, this.freelancers);
  }

  ProjectDto withoutFreelancers() {
    return new ProjectDto(this.id, this.name, this.description, this.requiredHours, null);
  }

}
