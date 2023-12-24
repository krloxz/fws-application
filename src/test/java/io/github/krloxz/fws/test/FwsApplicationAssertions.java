package io.github.krloxz.fws.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.assertj.core.api.ListAssert;
import org.springframework.context.ApplicationContext;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.core.TypeReferences.CollectionModelType;
import org.springframework.test.web.reactive.server.WebTestClient;

import io.github.krloxz.fws.freelancer.application.FreelancerDto;

/**
 * Entry point to the assertions for the FwsApplication.
 * <p>
 * Assertions are designed to look at the state of the system from the end user perspective in order
 * to validate the side effects of functionalities (actions) that have been previously recorded.
 * <p>
 * Assertions implement the THEN clause of the Gherkin DSL.
 *
 * @author Carlos Gomez
 */
public class FwsApplicationAssertions {

  private final RecordedActions recordedActions;
  private final WebTestClient webClient;

  /**
   * Creates a new instance.
   *
   * @param recordedActions
   *        the system actions that have been recorded for the current test
   * @param context
   *        the current Spring application context, used to manually retrieve required dependencies
   */
  public FwsApplicationAssertions(final RecordedActions recordedActions, final ApplicationContext context) {
    this.recordedActions = recordedActions;
    this.webClient = context.getBean(WebTestClient.class);
  }

  /**
   * Asserts that all the recorded actions were successful and returns the assertions that can be used
   * to validate the freelancers registered in the system.
   *
   * @return the assertions that can be used to validate the freelancers registered in the system
   */
  public ListAssert<FreelancerDto> freelancers() {
    this.recordedActions.succeed();

    final var freelancers = this.webClient.get()
        .uri("/freelancers")
        .accept(MediaTypes.HAL_JSON)
        .exchange()
        .expectStatus().isOk()
        .returnResult(new CollectionModelType<FreelancerDto>() {})
        .getResponseBody()
        .blockLast()
        .getContent();

    return assertThat(List.copyOf(freelancers));
  }

  /**
   * Asserts that all the recorded actions, but the last one, were successful and returns the
   * assertions that can be used to validate the response of the last action that was recorded.
   *
   * @return the assertions that can be used to validate the response of the last action that was
   *         recorded
   */
  public ResponseAssertions response() {
    return new ResponseAssertions(this.recordedActions.lastResponse());
  }

}
