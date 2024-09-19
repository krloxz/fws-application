package io.github.krloxz.fws.test.assertions;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.springframework.test.web.servlet.ResultMatcher;

/**
 * Builder of assertions for embedded resources contained in the response of an executed request.
 * <p>
 * Use {@link Assertions} to create instances of this class.
 *
 * @author Carlos Gomez
 */
public class EmbeddedAssertionsBuilder {

  private final String jsonPath;

  /**
   * @see Assertions#embedded(String)
   */
  EmbeddedAssertionsBuilder(final String jsonPath) {
    this.jsonPath = jsonPath;
  }

  /**
   * Asserts that the response contains an embedded resource with the given length.
   *
   * @param length
   *        the expected length of the embedded resource
   * @return a {@link ResultMatcher} that performs the assertion
   */
  public ResultMatcher withLength(final int length) {
    return jsonPath("_embedded.%s.length()", this.jsonPath).value(length);
  }

  /**
   * Asserts that the response contains an embedded resource with the given value.
   *
   * @param value
   *        the expected value of the embedded resource
   * @return a {@link ResultMatcher} that performs the assertion
   */
  public ResultMatcher withValue(final Object value) {
    return jsonPath("_embedded." + this.jsonPath).value(value);
  }

  /**
   * Starts the creation of an assertion that will verify the existence of a link within an embedded
   * resource.
   *
   * @param relation
   *        the relation of the link
   * @return a {@link ResultMatcher} that performs the assertion
   * @see LinkAssertionsBuilder
   */
  public LinkAssertionsBuilder withLink(final String relation) {
    return new LinkAssertionsBuilder(relation, "_embedded." + this.jsonPath);
  }

}
