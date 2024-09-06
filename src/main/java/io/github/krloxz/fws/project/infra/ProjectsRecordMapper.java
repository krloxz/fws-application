package io.github.krloxz.fws.project.infra;

import java.util.UUID;

import org.mapstruct.Mapper;

import io.github.krloxz.fws.infra.jooq.project.tables.records.ProjectsRecord;
import io.github.krloxz.fws.project.domain.Project;
import io.github.krloxz.fws.project.domain.ProjectId;

/**
 * Maps {@link Project} instances between the JOOQ record and domain models.
 * <p>
 * Use public methods only, protected methods are designed only to support MapStruct.
 *
 * @author Carlos Gomez
 */
@Mapper(componentModel = "spring")
abstract class ProjectsRecordMapper {

  public abstract ProjectsRecord toProjectsRecord(Project freelancer);

  UUID toFreelancersRecordId(final ProjectId id) {
    return id.value();
  }

  public abstract Project fromRecord(final ProjectsRecord records);

  ProjectId fromProjectsRecordId(final UUID id) {
    return new ProjectId(id);
  }

}
