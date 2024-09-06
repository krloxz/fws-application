package io.github.krloxz.fws.project.application;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.mapstruct.Mapper;

import io.github.krloxz.fws.project.domain.Project;
import io.github.krloxz.fws.project.domain.ProjectId;

/**
 * Maps {@link Project} instances between the DTO and domain models.
 * <p>
 * Use public methods only, protected methods are designed only to support MapStruct.
 *
 * @author Carlos Gomez
 */
@Mapper(componentModel = "spring")
abstract class ProjectDtoMapper {

  public abstract ProjectDto toDto(Project project);

  public abstract List<ProjectDto> toDto(List<Project> projects);

  public abstract Project fromDto(ProjectDto dto);

  ProjectId toProjectId(final Optional<String> value) {
    return value.map(UUID::fromString)
        .map(ProjectId::new)
        .orElseGet(ProjectId::new);
  }

  Optional<String> toOptionalString(final ProjectId id) {
    return Optional.of(id.value().toString());
  }

}
