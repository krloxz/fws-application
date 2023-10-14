package io.github.krloxz.fws.test;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import io.github.krloxz.fws.freelancer.application.FreelancerDto;

/**
 * Actions to invoke the Freelancers API.
 *
 * @author Carlos Gomez
 */
public class FreelancerActions {

  private final FwsApplicationActions applicationActions;
  private final WebTestClient webClient;
  private final List<FreelancerDto> freelancers;

  FreelancerActions(
      final List<FreelancerDto> freelancers,
      final WebTestClient webClient,
      final FwsApplicationActions applicationActions) {
    this.applicationActions = applicationActions;
    this.webClient = webClient;
    this.freelancers = freelancers;
  }

  /**
   * Invokes the registration endpoint.
   *
   * @return the {@link FwsApplicationActions}
   */
  public FwsApplicationActions registered() {
    this.freelancers.forEach(this::register);
    return this.applicationActions;
  }

  private void register(final FreelancerDto freelancer) {
    this.webClient.post()
        .uri("/freelancers")
        .accept(MediaType.APPLICATION_JSON)
        .bodyValue(freelancer)
        .exchange()
        .returnResult(Void.class);
  }

}
