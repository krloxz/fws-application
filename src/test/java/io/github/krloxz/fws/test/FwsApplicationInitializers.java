package io.github.krloxz.fws.test;

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
public class FwsApplicationInitializers {

  private final FwsApplicationActions actions;

  public FwsApplicationInitializers(final FwsApplicationActions actions) {
    this.actions = actions;
  }

  /**
   * @return the {@link FwsApplicationActions}
   */
  public FwsApplicationActions when() {
    return this.actions;
  }

}
