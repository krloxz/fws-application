package io.github.krloxz.fws.project;

import io.github.krloxz.fws.project.application.ProjectDto;
import io.github.krloxz.fws.test.gherkin.restapi.RestApiAction;

/**
 * Provides actions to interact with the Projects API.
 *
 * @author Carlos Gomez
 */
class ProjectActions {

  private final ProjectDto project;

  private ProjectActions(final ProjectDto project) {
    this.project = project;
  }

  static ProjectActions project(final ProjectDto project) {
    return new ProjectActions(project);
  }

  static ProjectActions projects() {
    return new ProjectActions(null);
  }

  RestApiAction listed() {
    return restApi -> restApi.get("/projects");
  }

  RestApiAction retrieved() {
    return restApi -> restApi.get("/projects/" + this.project.id().orElseThrow());
  }

  RestApiAction registered() {
    return restApi -> restApi.post("/projects", this.project);
  }

}
