package io.github.krloxz.fws.freelancer;

import static io.github.krloxz.fws.freelancer.FreelancerActions.freelancer;
import static io.github.krloxz.fws.freelancer.FreelancerActions.freelancers;
import static io.github.krloxz.fws.freelancer.FreelancerMother.tonyStark;
import static io.github.krloxz.fws.test.PublishedEventsActionExtension.publishedEvents;
import static io.github.krloxz.fws.test.gherkin.TestScenario.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.jupiter.api.Test;

import io.github.krloxz.fws.project.application.ProjectDto;
import io.github.krloxz.fws.project.application.ProjectDtoBuilder;
import io.github.krloxz.fws.test.FwsApplicationTest;

/**
 * Tests processing of events related to freelancers.
 *
 * @author Carlos Gomez
 */
@FwsApplicationTest
class FreelancerEventsTest {

  @Test
  void freelancerJoinsProjectSuccessfully() {
    given(freelancer(tonyStark()).registered())
        .when(freelancer(tonyStark()).joins(avengers(), 30))
        .then(freelancers().collection())
        .contains(
            jsonPath("_embedded.freelancers[0].weeklyAvailability").value(tonyStark().weeklyAvailability() - 30));
  }

  @Test
  void publishEventWhenFreelancerFailsToCommitMoreHoursThanAvailable() {
    given(freelancer(tonyStark()).registered())
        .when(freelancer(tonyStark()).joins(avengers(), tonyStark().weeklyAvailability() + 10))
        .then(publishedEvents())
        .contains(jsonPath("$.[1].freelancerId.value").value(tonyStark().id().orElseThrow()))
        .contains(jsonPath("$.[1].projectId.value").value(avengers().id().orElseThrow()))
        .contains(jsonPath("$.[1].committedHours").value(tonyStark().weeklyAvailability() + 10))
        .contains(jsonPath("$.[1].availableHours").value(tonyStark().weeklyAvailability()));
  }

  @Test
  void availabilityDoesNotChangeWhenFreelancerFailsToCommitMoreHoursThanAvailable() {
    given(freelancer(tonyStark()).registered())
        .when(freelancer(tonyStark()).joins(avengers(), tonyStark().weeklyAvailability() + 10))
        .then(freelancers().collection())
        .contains(
            jsonPath("_embedded.freelancers[0].weeklyAvailability").value(tonyStark().weeklyAvailability()));
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
