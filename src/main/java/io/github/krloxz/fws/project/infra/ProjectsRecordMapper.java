package io.github.krloxz.fws.project.infra;

import static io.github.krloxz.fws.project.infra.jooq.Tables.FREELANCERS;
import static io.github.krloxz.fws.project.infra.jooq.Tables.PROJECTS;

import java.util.List;
import java.util.Objects;

import org.jooq.Record;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import io.github.krloxz.fws.project.domain.Freelancer;
import io.github.krloxz.fws.project.domain.Project;
import io.github.krloxz.fws.project.domain.ProjectId;
import io.github.krloxz.fws.project.infra.jooq.tables.records.FreelancersRecord;
import io.github.krloxz.fws.project.infra.jooq.tables.records.ProjectsRecord;
import io.github.krloxz.fws.support.OptionalMapper;

/**
 * Maps {@link Project} instances between the JOOQ record and domain models.
 * <p>
 * Use public methods only, protected methods are designed only to support MapStruct.
 *
 * @author Carlos Gomez
 */
@Mapper(componentModel = "spring", uses = OptionalMapper.class)
abstract class ProjectsRecordMapper {

  @Mapping(target = "id", source = "id.value")
  public abstract ProjectsRecord toProjectsRecord(Project project);

  public List<FreelancersRecord> toFreelancersRecords(final Project project) {
    return project.freelancers()
        .stream()
        .map(freelancer -> toFreelancersRecord(freelancer, project.id()))
        .toList();
  }

  @Mapping(target = "id", source = "freelancer.id.value")
  @Mapping(target = "firstName", source = "freelancer.name.first")
  @Mapping(target = "lastName", source = "freelancer.name.last")
  @Mapping(target = "middleName", source = "freelancer.name.middle")
  @Mapping(target = "projectId", source = "projectId.value")
  abstract FreelancersRecord toFreelancersRecord(final Freelancer freelancer, final ProjectId projectId);

  public Project fromRecords(final List<Record> records) {
    final var projectsRecord = records.get(0).into(PROJECTS);
    final var freelancersRecords = records.stream()
        .map(record -> record.into(FREELANCERS))
        .filter(record -> Objects.nonNull(record.getId()))
        .toList();
    return fromRecords(projectsRecord, freelancersRecords);
  }

  @Mapping(target = "id.value", source = "projectsRecord.id")
  @Mapping(target = "freelancers", source = "freelancersRecords")
  @Mapping(target = "domainEvents", ignore = true)
  abstract Project fromRecords(
      ProjectsRecord projectsRecord, List<FreelancersRecord> freelancersRecords);

  @Mapping(target = "id.value", source = "id")
  @Mapping(target = "name.first", source = "firstName")
  @Mapping(target = "name.last", source = "lastName")
  @Mapping(target = "name.middle", source = "middleName")
  abstract Freelancer fromRecord(FreelancersRecord record);

}
