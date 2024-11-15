package io.github.krloxz.fws.project;

import java.util.UUID;

import org.springframework.context.ApplicationContext;
import org.springframework.test.web.servlet.MvcResult;

import io.github.krloxz.fws.core.FreelancerId;
import io.github.krloxz.fws.core.FreelancerProjectCommitmentFailed;
import io.github.krloxz.fws.core.ProjectId;
import io.github.krloxz.fws.freelancer.application.dtos.FreelancerDto;
import io.github.krloxz.fws.project.application.JoinRequest;
import io.github.krloxz.fws.project.application.ProjectDto;
import io.github.krloxz.fws.test.DomainEventAction;
import io.github.krloxz.fws.test.gherkin.actions.Action;
import io.github.krloxz.fws.test.gherkin.actions.VoidAction;
import io.github.krloxz.fws.test.gherkin.restapi.RestApi;
import io.github.krloxz.fws.test.gherkin.restapi.RestApiAction;

/**
 * Provides Freelancer API actions that are required to test the Projects API.
 *
 * @author Carlos Gomez
 */
final class FreelancerProjectActions {

  private ProjectDto project;
  private final FreelancerDto freelancer;

  private FreelancerProjectActions(final FreelancerDto freelancer) {
    this.freelancer = freelancer;
  }

  static FreelancerProjectActions freelancer(final FreelancerDto dto) {
    return new FreelancerProjectActions(dto);
  }

  VoidAction registered() {
    return new VoidAction();
  }

  WithAllocationBuilder joins(final ProjectDto project) {
    this.project = project;
    return new WithAllocationBuilder();
  }

  private String projectId() {
    return this.project.id().orElseThrow();
  }

  private String freelancerId() {
    return this.freelancer.id().orElseThrow();
  }

  class WithAllocationBuilder implements RestApiAction {

    @Override
    public MvcResult perform(final RestApi restApi) {
      return withAllocation(1).perform(restApi);
    }

    RestApiAction withAllocation(final int hours) {
      return restApi -> restApi.post("/projects/" + projectId() + "/join", new JoinRequest(freelancerId(), hours));
    }

    Action<ApplicationContext, Void> overCommitingAvailableTime() {
      return freelancerJoinsProject().andThen(freelancerProjectCommitmentFails());
    }

    private RestApiAction freelancerJoinsProject() {
      return withAllocation(FreelancerProjectActions.this.freelancer.weeklyAvailability() + 10);
    }

    private DomainEventAction freelancerProjectCommitmentFails() {
      return () -> new FreelancerProjectCommitmentFailed(
          new FreelancerId(freelancerId()),
          new ProjectId(UUID.fromString(projectId())),
          FreelancerProjectActions.this.freelancer.weeklyAvailability() + 10,
          FreelancerProjectActions.this.freelancer.weeklyAvailability());
    }

  }

}
