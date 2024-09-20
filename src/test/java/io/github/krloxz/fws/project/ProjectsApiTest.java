package io.github.krloxz.fws.project;

import static io.github.krloxz.fws.freelancer.FreelancerMother.steveRogers;
import static io.github.krloxz.fws.freelancer.FreelancerMother.tonyStark;
import static io.github.krloxz.fws.freelancer.FreelancerMother.unregistered;
import static io.github.krloxz.fws.project.FreelancerProjectActions.freelancer;
import static io.github.krloxz.fws.project.ProjectActions.project;
import static io.github.krloxz.fws.project.ProjectActions.projects;
import static io.github.krloxz.fws.test.assertions.Assertions.embedded;
import static io.github.krloxz.fws.test.assertions.Assertions.link;
import static io.github.krloxz.fws.test.gherkin.TestScenario.given;
import static io.github.krloxz.fws.test.gherkin.actions.Actions.response;
import static io.github.krloxz.fws.test.gherkin.actions.Actions.systemReady;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.web.server.ResponseStatusException;

import io.github.krloxz.fws.freelancer.application.FreelancersApiController;
import io.github.krloxz.fws.project.application.ProjectDto;
import io.github.krloxz.fws.project.application.ProjectDtoBuilder;
import io.github.krloxz.fws.test.FwsApplicationTest;

/**
 * Tests for the Projects API.
 *
 * @author Carlos Gomez
 */
@FwsApplicationTest
@ApplicationModuleTest(verifyAutomatically = false, extraIncludes = "test")
class ProjectsApiTest {

  @MockBean
  private FreelancersApiController freelancersApiController;

  @BeforeEach
  void setup() {
    when(this.freelancersApiController.get(tonyStark().id().orElseThrow()))
        .thenReturn(EntityModel.of(tonyStark()));
    when(this.freelancersApiController.get(steveRogers().id().orElseThrow()))
        .thenReturn(EntityModel.of(steveRogers()));
    when(this.freelancersApiController.get(unregistered().id().orElseThrow()))
        .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));
  }

  @Test
  void createProjects() {
    given(systemReady())
        .when(project(avengers()).created())
        .then(response())
        .contains(status().isCreated())
        .contains(jsonPath("id").isNotEmpty())
        .contains(jsonPath("name").value(avengers().name()))
        .contains(jsonPath("description").value(avengers().description()))
        .contains(link("collection").withPath("/projects"))
        .contains(link("self").withPath("/projects/{id}", avengers().id()))
        .contains(link("join").withPath("/projects/{id}/join", avengers().id()));
  }

  @Test
  void projectsCanBeListedAfterCreation() {
    given(systemReady())
        .when(project(avengers()).created())
        .and(projects().listed())
        .then(response())
        .contains(jsonPath("_embedded.projects[*].name").value(hasItem(avengers().name())));
  }

  @Test
  void projectsCanBeRetrievedAfterCreation() {
    given(systemReady())
        .when(project(avengers()).created())
        .and(project(avengers()).retrieved())
        .then(response())
        .contains(jsonPath("name").value(avengers().name()));
  }

  @Test
  void dontCreateProjectsWithInvalidData() {
    given(systemReady())
        .when(project(new ProjectDtoBuilder().build()).created())
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

  @Test
  void assignFreelancers() {
    given(project(avengers()).created())
        .and(freelancer(tonyStark()).registered())
        .and(freelancer(steveRogers()).registered())
        .when(freelancer(tonyStark()).joins(avengers()).withAllocation(40))
        .and(freelancer(steveRogers()).joins(avengers()).withAllocation(50))
        .then(response())
        .contains(status().isOk())
        .contains(embedded("freelancers").withLength(2))
        .contains(embedded("freelancers[0].firstName").withValue("Tony"))
        .contains(embedded("freelancers[0].allocatedHours").withValue(40))
        .contains(embedded("freelancers[0]").withLink("self").withPath("/freelancers/{id}", tonyStark().id()))
        .contains(embedded("freelancers[1].firstName").withValue("Steve"))
        .contains(embedded("freelancers[1].allocatedHours").withValue(50))
        .contains(embedded("freelancers[1]").withLink("self").withPath("/freelancers/{id}", steveRogers().id()))
        .contains(link("join").withPath("/projects/{id}/join", avengers().id()));
  }

  @Test
  void dontAssignUnregisteredFreelancers() {
    given(project(avengers()).created())
        .when(freelancer(unregistered()).joins(avengers()))
        .then(response())
        .contains(status().isUnprocessableEntity())
        .contains(jsonPath("type").value("http://localhost/probs/error.html"))
        .contains(jsonPath("detail").isNotEmpty());
  }

  @Test
  void dontAssignFreelancersWithZeroAllocation() {
    given(project(avengers()).created())
        .and(freelancer(tonyStark()).registered())
        .when(freelancer(tonyStark()).joins(avengers()).withAllocation(0))
        .then(response())
        .contains(status().isBadRequest())
        .contains(jsonPath("type").value("http://localhost/probs/validation-error.html"))
        .contains(jsonPath("errors").isArray())
        .contains(jsonPath("errors").isNotEmpty());
  }

  @Test
  void dontOverallocateProject() {
    given(project(avengers()).created())
        .and(freelancer(tonyStark()).registered())
        .and(freelancer(steveRogers()).registered())
        .when(freelancer(tonyStark()).joins(avengers()).withAllocation(avengers().requiredHours()))
        .and(freelancer(steveRogers()).joins(avengers()).withAllocation(1))
        .then(response())
        .contains(status().isUnprocessableEntity())
        .contains(jsonPath("type").value("http://localhost/probs/error.html"))
        .contains(jsonPath("detail").isNotEmpty());
  }

  @Test
  void preventProjectOverallocation() {
    given(project(avengers()).created())
        .and(freelancer(tonyStark()).registered())
        .when(freelancer(tonyStark()).joins(avengers()).withAllocation(avengers().requiredHours()))
        .then(response())
        .contains(status().isOk())
        .contains(jsonPath("_links.join").doesNotExist());
  }

  ProjectDto avengers() {
    return new ProjectDtoBuilder()
        .id("eda7cd79-1976-46fc-81ef-fe5f6dba7aa5")
        .name("Avengers")
        .description("Earth's mightiest heroes")
        .requiredHours(200)
        .build();
  }

}
