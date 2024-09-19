package io.github.krloxz.fws.project.application;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;

import io.github.krloxz.fws.project.domain.Freelancer;
import io.github.krloxz.fws.project.domain.Project;
import io.github.krloxz.fws.support.OptionalMapper;

/**
 * Maps {@link Project} instances between the DTO and domain models.
 * <p>
 * Use public methods only, protected methods are designed only to support MapStruct.
 *
 * @author Carlos Gomez
 */
@Mapper(componentModel = "spring", uses = OptionalMapper.class,
    nullValueIterableMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
abstract class ProjectDtoMapper {

  public abstract List<ProjectDto> toDto(List<Project> projects);

  @Mapping(target = "id", source = "id.value")
  public abstract ProjectDto toDto(Project project);

  @Mapping(target = "id", source = "freelancer.id.value")
  @Mapping(target = "firstName", source = "freelancer.name.first")
  @Mapping(target = "lastName", source = "freelancer.name.last")
  abstract FreelancerDto assigneeToAssigneeDto(Freelancer freelancer);

  @Mapping(target = "id.value", source = "id")
  @Mapping(target = "freelancers", source = "freelancers")
  public abstract Project fromDto(ProjectDto dto);

  @Mapping(target = "id.value", source = "id")
  @Mapping(target = "name.first", source = "firstName")
  @Mapping(target = "name.last", source = "lastName")
  abstract Freelancer map(FreelancerDto dto);

}
