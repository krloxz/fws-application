package io.github.krloxz.fws.test;

import java.util.List;
import java.util.function.Supplier;

import org.springframework.hateoas.MediaTypes;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;

import io.github.krloxz.fws.freelancer.application.FreelancerDto;

/**
 * Actions to invoke the Freelancers API.
 *
 * @author Carlos Gomez
 */
public class FreelancerActions {

  private final List<FreelancerDto> freelancers;
  private final FwsApplicationActions applicationActions;
  private final WebTestClient webClient;
  private final ActionsRecorder actionRecorder;

  FreelancerActions(
      final List<FreelancerDto> freelancers,
      final FwsApplicationActions applicationActions) {
    this.freelancers = freelancers;
    this.applicationActions = applicationActions;
    this.webClient = applicationActions.webClient();
    this.actionRecorder = applicationActions.actionsRecorder();
  }

  /**
   * Invokes the registration endpoint.
   *
   * @return the {@link FwsApplicationActions}
   */
  public FwsApplicationActions registered() {
    this.freelancers.stream()
        .map(this::register)
        .forEach(this.actionRecorder::add);
    return this.applicationActions;
  }

  private Supplier<ResponseSpec> register(final FreelancerDto freelancer) {
    return () -> this.webClient.post()
        .uri("/freelancers")
        .accept(MediaTypes.HAL_JSON)
        .bodyValue(freelancer)
        .exchange();
  }

}
