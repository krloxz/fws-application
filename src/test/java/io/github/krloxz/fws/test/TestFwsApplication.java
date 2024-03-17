package io.github.krloxz.fws.test;

import org.springframework.stereotype.Component;

/**
 * The FwsApplication encapsulated to test its functionalities using a DSL that attempts to resemble
 * the Gherkin language:
 *
 * <pre>
 * this.fwsApplication.running()
 *     .when()
 *     .freelancers(tonyStark(), steveRogers()).registered()
 *     .then()
 *     .freelancers()
 *     .extracting(FreelancerDto::firstName)
 *     .contains("Tony", "Steve");
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
