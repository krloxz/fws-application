package io.github.krloxz.fws.test.dsl;

import org.springframework.context.ApplicationContext;

/**
 * The FwsApplication encapsulated to be tested using a DSL that attempts to resemble the Gherkin
 * language:
 *
 * <pre>
 * {@code @Test}
 * void registersFreelancers() {
 *   this.fwsApplication.running()
 *       .when()
 *       .freelancer(tonyStark()).registered()
 *       .freelancer(steveRogers()).registered()
 *       .then()
 *       .freelancers()
 *       .contains(jsonPath("_embedded.freelancers[*].firstName").value(hasItems("Tony", "Steve")));
 * }
 * </pre>
 *
 * The previous test case is equivalent to the following Gherkin scenario:
 *
 * <pre>
 * Scenario: Registering freelancers
 *   Given that the FwsApplication is running
 *   When the freelancers Tony Stark and Steve Rogers are registered
 *   Then the names of the freelancers registered in the system should contain Tony and Steve
 * </pre>
 *
 * @author Carlos Gomez
 */
public class TestComponent {

  private final ApplicationContext context;

  public TestComponent(final ApplicationContext context) {
    this.context = context;
  }

  /**
   * @return the {@link InitializersRecorder}
   */
  public InitializersRecorder running() {
    return new InitializersRecorder(this.context);
  }

  /**
   * Synonym for {@link #running()}.
   */
  public InitializersRecorder runningWith() {
    return running();
  }

}
