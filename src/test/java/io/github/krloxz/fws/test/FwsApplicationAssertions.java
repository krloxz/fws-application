package io.github.krloxz.fws.test;

import org.springframework.context.ApplicationContext;
import org.springframework.hateoas.MediaTypes;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;

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
   * Asserts that all the recorded actions were successful and returns a {@link ResponseSpec} to
   * validate the freelancers registered in the system.
   *
   * @return a {@link ResponseSpec} to validate the freelancers registered in the system
   */
  public ResponseSpec freelancers() {
    this.recordedActions.succeed();

    return this.webClient.get()
        .uri("/freelancers")
        .accept(MediaTypes.HAL_JSON)
        .exchange();
  }

  /**
   * Asserts that all the recorded actions, but the last one, were successful and returns a
   * {@link ResponseSpec} to validate the response of the last action that was recorded.
   *
   * @return a {@link ResponseSpec} to validate the response of the last action that was recorded
   */
  public ResponseSpec response() {
    return this.recordedActions.lastResponse();
  }

}
