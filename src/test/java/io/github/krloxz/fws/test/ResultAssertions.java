package io.github.krloxz.fws.test;

import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;

/**
 * Assertions for the result of a test action executed on the {@link FwsApplicationRestApi REST API
 * of the FwsApplication}.
 *
 * @author Carlos Gomez
 */
public class ResultAssertions {

  private final ResultActions result;

  ResultAssertions(final ResultActions result) {
    this.result = result;
  }

  /**
   * Asserts that the result of a test action matches the given {@link ResultMatcher}.
   *
   * @param matcher
   *        the {@link ResultMatcher}
   * @return this instance for chaining additional assertions
   */
  public ResultAssertions contains(final ResultMatcher matcher) {
    try {
      this.result.andExpect(matcher);
    } catch (final AssertionError e) {
      throw e;
    } catch (final Throwable e) {
      throw new IllegalStateException("Unexpected exception while matching the result: " + this.result, e);
    }
    return this;
  }

  /**
   * Synonym for {@link #contains(ResultMatcher)}.
   */
  public ResultAssertions andExpect(final ResultMatcher matcher) {
    try {
      this.result.andExpect(matcher);
    } catch (final AssertionError e) {
      throw e;
    } catch (final Throwable e) {
      throw new IllegalStateException("Unexpected exception while matching the result: " + this.result, e);
    }
    return this;
  }
}
