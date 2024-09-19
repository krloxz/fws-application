package io.github.krloxz.fws.test.assertions;

/**
 * A utility class offering convenient static methods to instantiate common assertion objects.
 *
 * @author Carlos Gomez
 */
public abstract class Assertions {

  private Assertions() {
    throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
  }

  /**
   * Starts the creation of an assertion that will verify the existence of an affordance link using
   * the JSON path {@code _links.<relation>}
   *
   * @param relation
   *        the link relation
   * @return a new instance of {@link LinkAssertionsBuilder}
   */
  public static LinkAssertionsBuilder link(final String relation) {
    return new LinkAssertionsBuilder(relation);
  }

  /**
   * Starts the creation of an assertion that will verify the existence of an embedded resource using
   * the JSON path {@code _embedded.<jsonPath>}.
   *
   * @param jsonPath
   *        a JSON path expression
   * @return a new instance of {@link EmbeddedAssertionsBuilder}
   */
  public static EmbeddedAssertionsBuilder embedded(final String jsonPath) {
    return new EmbeddedAssertionsBuilder(jsonPath);
  }

}
