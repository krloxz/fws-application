package io.github.krloxz.fws.test.gherkin;

import org.springframework.context.ApplicationContext;

import io.github.krloxz.fws.test.gherkin.actions.Action;

/**
 * Entry point to the Gherkin DSL that allows writing tests using a syntax that resembles the
 * Gherkin language:
 *
 * <pre>
 * {@code @Test}
 * void registeringFreelancers() {
 *   given(systemReady())
 *       .when(freelancer(tonyStark()).registered())
 *       .and(freelancer(steveRogers()).registered())
 *       .then(freelancers().collection())
 *       .contains(jsonPath("_embedded.freelancers[*].firstName").value(hasItems("Tony", "Steve")));
 * }
 * </pre>
 *
 * The previous test case is equivalent to the following Gherkin scenario:
 *
 * <pre>
 * Scenario: Registering freelancers
 *   Given the system is ready
 *   When the freelancer Tony Stark is registered
 *   And the freelancer Steve Rogers is registered
 *   Then the freelancers collection contains the first names Tony and Steve
 * </pre>
 *
 * The Gherkin DSL depends on the Spring Framework to access dependencies, import the
 * {@link GherkinConfig} class to enable the Gherkin DSL.
 *
 * @author Carlos Gomez
 * @see GherkinConfig
 * @see <a href="https://cucumber.io/docs/gherkin/reference/">Gherkin Reference</a>
 */
public class TestScenario {

  private static ApplicationContext context;

  TestScenario(final ApplicationContext context) {
    TestScenario.context = context;
  }

  /**
   * Starts a test scenario initializing the system with the given action.
   *
   * @param action
   *        the first {@link Action} that will be executed to initialize the system
   * @return a {@link GivenStep} that allows chaining other initialization actions or a
   *         {@link WhenStep}
   * @throws IllegalStateException
   *         if the Gherkin DSL is used without the {@link GherkinConfig}
   */
  public static GivenStep given(final Action<?, ?> action) {
    if (context == null) {
      throw new IllegalStateException(
          "TestScenario context not initialized, import the GherkinConfig class to enable the Gherkin DSL.");
    }
    return new GivenStep(context).and(action);
  }

}
