package io.github.krloxz.fws.project.infra;

import static io.github.krloxz.fws.project.infra.jooq.Tables.PROJECTS;

import java.util.List;
import java.util.Optional;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import io.github.krloxz.fws.common.PageSpec;
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
    return this.create.selectFrom(PROJECTS)
        .where(PROJECTS.ID.eq(id.value()))
        .fetchOptional(this.mapper::fromRecord);
  }

  @Override
  public List<Project> findAllBy(final PageSpec pageSpec) {
    return this.create.selectFrom(PROJECTS)
        .orderBy(PROJECTS.NAME)
        .offset(pageSpec.number() * pageSpec.size())
        .limit(pageSpec.size())
        .fetch(this.mapper::fromRecord);
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
    return project;
  }

}
