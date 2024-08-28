package io.github.krloxz.fws.test;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.context.ApplicationContext;

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
  private final FwsApplicationRestApi restApi;

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
    this.restApi = context.getBean(FwsApplicationRestApi.class);
  }

  /**
   * Asserts that all the recorded actions were successful and returns a {@link ResultAssertions}
   * instance to validate the freelancers registered in the system.
   *
   * @return a {@link ResultAssertions} instance to validate the freelancers registered in the system
   */
  public ResultAssertions freelancers() {
    this.recordedActions.succeed();
    return this.restApi.get("/freelancers").andExpect(status().isOk());
  }

  /**
   * Asserts that all the recorded actions, but the last one, were successful and returns a
   * {@link ResultAssertions} instance to validate the result of the last action that was recorded.
   *
   * @return a {@link ResultAssertions} instance to validate the result of the last action that was
   *         recorded
   */
  public ResultAssertions result() {
    return this.recordedActions.lastResult();
  }

}
