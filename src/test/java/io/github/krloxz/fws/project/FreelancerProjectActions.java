package io.github.krloxz.fws.project;

import java.util.Optional;

import org.springframework.test.web.servlet.MvcResult;

import io.github.krloxz.fws.freelancer.application.dtos.FreelancerDto;
import io.github.krloxz.fws.project.application.JoinRequest;
import io.github.krloxz.fws.project.application.ProjectDto;
import io.github.krloxz.fws.test.gherkin.restapi.RestApi;
import io.github.krloxz.fws.test.gherkin.restapi.RestApiAction;

/**
 * Provides Freelancer API actions that are required to test the Projects API.
 *
 * @author Carlos Gomez
 */
final class FreelancerProjectActions {

  private final Optional<FreelancerDto> freelancer;

  private FreelancerProjectActions(final FreelancerDto freelancer) {
    this.freelancer = Optional.ofNullable(freelancer);
  }

  static FreelancerProjectActions freelancer(final FreelancerDto dto) {
    return new FreelancerProjectActions(dto);
  }

  RestApiAction registered() {
    return restApi -> restApi.post("/freelancers", freelancer());
  }

  WithAllocationBuilder joins(final ProjectDto project) {
    return new WithAllocationBuilder(project.id().orElseThrow());
  }

  class WithAllocationBuilder implements RestApiAction {

    private final String projectId;

    WithAllocationBuilder(final String projectId) {
      this.projectId = projectId;
    }

    @Override
    public MvcResult perform(final RestApi restApi) {
      return withAllocation(1).perform(restApi);
    }

    RestApiAction withAllocation(final int hours) {
      return restApi -> restApi.post(
          "/projects/" + this.projectId + "/join", new JoinRequest(freelancerId(), hours));
    }

  }

  private FreelancerDto freelancer() {
    return this.freelancer.orElseThrow(() -> new IllegalStateException("No freelancer provided"));
  }

  private String freelancerId() {
    return freelancer().id().orElseThrow();
  }

}
