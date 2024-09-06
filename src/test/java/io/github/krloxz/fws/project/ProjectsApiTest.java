package io.github.krloxz.fws.project;

import static io.github.krloxz.fws.project.ProjectActions.project;
import static io.github.krloxz.fws.project.ProjectActions.projects;
import static io.github.krloxz.fws.test.gherkin.TestScenario.given;
import static io.github.krloxz.fws.test.gherkin.actions.Actions.response;
import static io.github.krloxz.fws.test.gherkin.actions.Actions.systemReady;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;

import io.github.krloxz.fws.project.application.ProjectDto;
import io.github.krloxz.fws.project.application.ProjectDtoBuilder;
import io.github.krloxz.fws.test.FwsApplicationTest;

/**
 * Tests for the Projects API.
 *
 * @author Carlos Gomez
 */
@FwsApplicationTest
class ProjectsApiTest {

  @Test
  void createProjects() {
    given(systemReady())
        .when(project(avengers()).registered())
        .then(response())
        .contains(status().isCreated())
        .contains(jsonPath("id").isNotEmpty())
        .contains(jsonPath("name").value(avengers().name()))
        .contains(jsonPath("description").value(avengers().description()))
        .contains(jsonPath("_links.collection.href").isNotEmpty())
        .contains(jsonPath("_links.self.href").isNotEmpty());
  }

  @Test
  void projectCanBeListedAfterCreation() {
    given(systemReady())
        .when(project(avengers()).registered())
        .and(projects().listed())
        .then(response())
        .contains(jsonPath("_embedded.projects[*].name").value(hasItem(avengers().name())));
  }

  @Test
  void projectCanBeRetrievedAfterCreation() {
    given(systemReady())
        .when(project(avengers()).registered())
        .and(project(avengers()).retrieved())
        .then(response())
        .contains(jsonPath("name").value(avengers().name()));
  }

  @Test
  void doesNotCreateProjectsWithInvalidData() {
    given(systemReady())
        .when(project(new ProjectDtoBuilder().build()).registered())
        .then(response())
        .contains(status().isBadRequest())
        .contains(jsonPath("type").value(endsWith("/probs/validation-error.html")))
        .contains(jsonPath("errors").isArray())
        .contains(jsonPath("errors").isNotEmpty());
  }

  @Test
  void projectNotFound() {
    given(systemReady())
        .when(project(avengers()).retrieved())
        .then(response())
        .contains(status().isNotFound())
        .contains(jsonPath("type").value(endsWith("/probs/error.html")));
  }

  ProjectDto avengers() {
    return new ProjectDtoBuilder()
        .id("eda7cd79-1976-46fc-81ef-fe5f6dba7aa5")
        .name("Avengers")
        .description("Earth's mightiest heroes")
        .build();
  }

}
