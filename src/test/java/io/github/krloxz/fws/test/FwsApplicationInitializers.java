package io.github.krloxz.fws.test;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Entry point to the test initializers: objects that provide methods to set up the state of the
 * FwsApplication before a test case is executed.
 * <p>
 * Initializers implement the GIVEN clause of the Gherkin DSL.
 *
 * @author Carlos Gomez
 */
@Component
public class FwsApplicationInitializers implements ApplicationContextAware {

  private ApplicationContext context;

  @Override
  public void setApplicationContext(final ApplicationContext context) {
    this.context = context;
  }

  /**
   * @return the {@link FwsApplicationActions}
   */
  public FwsApplicationActions when() {
    return new FwsApplicationActions(this.context);
  }

}
