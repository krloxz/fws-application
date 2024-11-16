package io.github.krloxz.fws.project.domain;

import java.util.List;
import java.util.Optional;

import io.github.krloxz.fws.core.PageSpec;

/**
 * Repository for projects.
 *
 * @author Carlos Gomez
 */
public interface ProjectRepository {

  /**
   * Returns a project by its ID.
   *
   * @param id
   *        the ID of the project to return
   * @return an {@link Optional} with the project, if found
   */
  Optional<Project> findById(ProjectId id);

  /**
   * Returns a page of projects meeting the criteria specified by the given {@link PageSpec}.
   *
   * @param pageSpec
   *        specification for the page to return
   * @return a {@link List} with the requested page of projects
   */
  List<Project> findAllBy(PageSpec pageSpec);

  /**
   * @return the total count of projects
   */
  int count();

  /**
   * Saves a project.
   *
   * @param project
   *        the project to save
   * @return the saved project
   */
  Project save(Project project);

  /**
   * Updates a project.
   *
   * @param project
   *        the project to update
   * @return the updated project
   */
  Project update(Project project);

}
