package io.github.krloxz.fws.project.infra;

import static io.github.krloxz.fws.project.infra.jooq.Tables.FREELANCERS;
import static io.github.krloxz.fws.project.infra.jooq.Tables.PROJECTS;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record1;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import io.github.krloxz.fws.core.PageSpec;
import io.github.krloxz.fws.project.domain.Project;
import io.github.krloxz.fws.project.domain.ProjectId;
import io.github.krloxz.fws.project.domain.ProjectRepository;

/**
 * JOOQ implementation of the {@link ProjectRepository}.
 *
 * @author Carlos Gomez
 */
@Repository
@Transactional(propagation = Propagation.MANDATORY)
class JooqProjectRepository implements ProjectRepository {

  private final DSLContext create;
  private final ProjectsRecordMapper mapper;

  JooqProjectRepository(final DSLContext create, final ProjectsRecordMapper mapper) {
    this.create = create;
    this.mapper = mapper;
  }

  @Override
  public Optional<Project> findById(final ProjectId id) {
    return findRecordById(id.value()).map(this.mapper::fromRecords);
  }

  @Override
  public List<Project> findAllBy(final PageSpec pageSpec) {
    return this.create.select(PROJECTS.ID)
        .from(PROJECTS)
        .orderBy(PROJECTS.NAME)
        .offset(pageSpec.number() * pageSpec.size())
        .limit(pageSpec.size())
        .fetch()
        .map(Record1::value1)
        .stream()
        .map(this::findRecordById)
        .flatMap(Optional::stream)
        .map(this.mapper::fromRecords)
        .collect(Collectors.toList());
  }

  @Override
  public int count() {
    return this.create.fetchCount(PROJECTS);
  }

  @Override
  public Project save(final Project project) {
    this.create.insertInto(PROJECTS)
        .set(this.mapper.toProjectsRecord(project))
        .execute();
    this.mapper.toFreelancersRecords(project)
        .forEach(freelancer -> this.create.insertInto(FREELANCERS).set(freelancer).execute());
    return project;
  }

  @Override
  public Project update(final Project project) {
    this.create.update(PROJECTS)
        .set(this.mapper.toProjectsRecord(project))
        .where(PROJECTS.ID.eq(project.id().value()))
        .execute();
    this.create.delete(FREELANCERS)
        .where(FREELANCERS.PROJECT_ID.eq(project.id().value()))
        .execute();
    this.mapper.toFreelancersRecords(project)
        .forEach(freelancer -> this.create.insertInto(FREELANCERS).set(freelancer).execute());
    return project;
  }

  private Optional<List<Record>> findRecordById(final UUID id) {
    final var records = this.create.select()
        .from(PROJECTS)
        .leftJoin(FREELANCERS).onKey()
        .where(PROJECTS.ID.eq(id))
        .fetch();
    return records.isEmpty() ? Optional.empty() : Optional.of(records);
  }

}
