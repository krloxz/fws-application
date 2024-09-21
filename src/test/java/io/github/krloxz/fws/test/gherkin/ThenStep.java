package io.github.krloxz.fws.test.gherkin;

import org.springframework.context.ApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;

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
  private final ObjectMapper objectMapper;

  /**
   * Constructs a new {@link ThenStep} instance for validating the system state exposed by the
   * specified {@link ActionResult}.
   *
   * @param result
   *        the result of an action used to access the system state
   * @param context
   *        the Spring application context
   */
  ThenStep(final ActionResult<?> result, final ApplicationContext context) {
    this.result = result;
    this.objectMapper = context.getBean(ObjectMapper.class);
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
    try {
      matcher.match(mvcResult());
      return this;
    } catch (final AssertionError e) {
      throw e;
    } catch (final Throwable e) {
      throw new IllegalStateException("Unexpected exception while executing a matcher ", e);
    }
  }

  private MvcResult mvcResult() {
    if (this.result.getValue() instanceof final MvcResult mvcResult) {
      return mvcResult;
    }
    return mvcResultWithResponseOnly();
  }

  private MvcResult mvcResultWithResponseOnly() {
    try {
      final var resultJson = this.objectMapper.writeValueAsString(this.result.getValue());
      final var resultResponse = new MockHttpServletResponse();
      resultResponse.getWriter().write(resultJson);
      return new MvcResultWithResponseOnly(resultResponse);
    } catch (final Exception e) {
      throw new IllegalStateException("Unexpected exception while serializing the result", e);
    }
  }

  private static final class MvcResultWithResponseOnly implements MvcResult {

    private final MockHttpServletResponse response;

    MvcResultWithResponseOnly(final MockHttpServletResponse response) {
      this.response = response;
    }

    @Override
    public MockHttpServletResponse getResponse() {
      return this.response;
    }

    @Override
    public Exception getResolvedException() {
      throw new UnsupportedOperationException();
    }

    @Override
    public MockHttpServletRequest getRequest() {
      throw new UnsupportedOperationException();
    }

    @Override
    public ModelAndView getModelAndView() {
      throw new UnsupportedOperationException();
    }

    @Override
    public HandlerInterceptor[] getInterceptors() {
      throw new UnsupportedOperationException();
    }

    @Override
    public Object getHandler() {
      throw new UnsupportedOperationException();
    }

    @Override
    public FlashMap getFlashMap() {
      throw new UnsupportedOperationException();
    }

    @Override
    public Object getAsyncResult(final long timeToWait) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Object getAsyncResult() {
      throw new UnsupportedOperationException();
    }
  }

}
