package io.github.krloxz.fws.test;

import org.springframework.stereotype.Component;

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
@Component
public class TestFwsApplication {

  private final FwsApplicationInitializers initializers;

  public TestFwsApplication(final FwsApplicationInitializers initializers) {
    this.initializers = initializers;
  }

  /**
   * @return the {@link FwsApplicationInitializers}
   */
  public FwsApplicationInitializers running() {
    return this.initializers;
  }

  /**
   * Synonym for {@link #running()}. Just syntactic sugar to improve readability.
   */
  public FwsApplicationInitializers runningWith() {
    return this.initializers;
  }

}
