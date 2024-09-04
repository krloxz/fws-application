package io.github.krloxz.fws.test.gherkin;

import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import io.github.krloxz.fws.test.gherkin.actions.ActionResult;

/**
 * The "Then" step in a Gherkin scenario.
 * <p>
 * This step is used to validate the system state, facilitating the execution of a series of
 * assertions on the {@link ActionResult} produced by a preceding action, ensuring that the system
 * behaves as expected. Currently, only assertions defined using {@link ResultMatcher
 * ResultMatchers} from the Spring Test framework are supported.
 *
 * @author Carlos Gomez
 */
public class ThenStep {

  private final ActionResult<?> result;

  /**
   * Constructs a new {@link ThenStep} instance for validating the system state exposed by the
   * specified {@link ActionResult}.
   *
   * @param result
   *        the result of an action used to access the system state
   */
  ThenStep(final ActionResult<?> result) {
    this.result = result;
  }

  /**
   * Asserts that the system state matches the given {@link ResultMatcher}.
   * <p>
   * This method applies the provided {@link ResultMatcher} to the {@link MvcResult} contained within
   * the {@link ActionResult}. If the result does not match the expected condition defined by the
   * matcher, an {@link AssertionError} is thrown.
   *
   * @param matcher
   *        the {@link ResultMatcher} to be applied to the system state
   * @return this {@link ThenStep} instance, enabling the chaining of additional assertions
   * @throws AssertionError
   *         if the system state does not match the expected condition, or if the {@link ActionResult}
   *         does not contain a valid {@link MvcResult}
   * @throws IllegalStateException
   *         if an unexpected exception occurs while executing the matcher
   */
  public ThenStep contains(final ResultMatcher matcher) {
    if (this.result.getValue() instanceof final MvcResult mvcResult) {
      return match(matcher, mvcResult);
    }
    throw new AssertionError("The result is not an instance of MvcResult, cannot apply the matcher");
  }

  private ThenStep match(final ResultMatcher matcher, final MvcResult mvcResult) throws AssertionError {
    try {
      matcher.match(mvcResult);
      return this;
    } catch (final AssertionError e) {
      throw e;
    } catch (final Throwable e) {
      throw new IllegalStateException("Unexpected exception while executing a matcher ", e);
    }
  }

}
