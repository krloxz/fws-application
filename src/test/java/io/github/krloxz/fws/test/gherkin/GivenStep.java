package io.github.krloxz.fws.test.gherkin;

import java.util.stream.Stream;

import org.springframework.context.ApplicationContext;

import io.github.krloxz.fws.test.gherkin.actions.Action;
import io.github.krloxz.fws.test.gherkin.actions.ActionResult;

/**
 * The "Given" step in a Gherkin scenario.
 * <p>
 * This step is used to initialize the system state executing a series of {@link Action actions}
 * and, eventually, start the {@link WhenStep}.
 *
 * @author Carlos Gomez
 */
public class GivenStep {

  private final ApplicationContext context;

  GivenStep(final ApplicationContext context) {
    this.context = context;
  }

  /**
   * Executes the given actions to initialize the system.
   *
   * @param actions
   *        the initialization actions
   * @return this {@link GivenStep}
   */
  public GivenStep and(final Action<?, ?>... actions) {
    execute(actions);
    return this;
  }

  /**
   * Starts the "When" step of the Gherkin scenario.
   *
   * @param action
   *        the first {@link Action} that will be executed to interact with the system
   * @return a {@link WhenStep} that allows chaining other interaction actions or a {@link ThenStep}
   */
  public WhenStep when(final Action<?, ?> action) {
    return new WhenStep(this.context).and(action);
  }

  private void execute(final Action<?, ?>... actions) {
    try {
      Stream.of(actions)
          .map(action -> action.execute(this.context))
          .forEach(ActionResult::validate);
    } catch (final Throwable e) {
      throw new AssertionError("An initialization action has failed, look at the stack trace for more information", e);
    }
  }

}
