package io.github.krloxz.fws.test.gherkin;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.lang3.function.Consumers;
import org.springframework.context.ApplicationContext;

import io.github.krloxz.fws.test.gherkin.actions.Action;
import io.github.krloxz.fws.test.gherkin.actions.ActionResult;
import io.github.krloxz.fws.test.gherkin.actions.Actions;
import io.github.krloxz.fws.test.gherkin.actions.CapturingLastResultAction;

/**
 * The "When" step in a Gherkin scenario.
 * <p>
 * This step is used to interact with the system executing a series of {@link Action actions} and,
 * eventually, start the {@link ThenStep}.
 *
 * @author Carlos Gomez
 */
public class WhenStep {

  private final ApplicationContext context;
  private final List<Action<?, ?>> actions;

  WhenStep(final ApplicationContext context) {
    this.context = context;
    this.actions = new ArrayList<>();
  }

  /**
   * Records the specified action(s) to interact with the system. The actions will be accumulated to
   * be executed in the order they were added when the {@link ThenStep} is started.
   *
   * @param actions
   *        the interaction actions to be recorded
   * @return this {@link WhenStep}
   */
  public WhenStep and(final Action<?, ?>... actions) {
    this.actions.addAll(List.of(actions));
    return this;
  }

  /**
   * Executes the actions recorded so far along with the specified action, and starts the "Then" step
   * of the Gherkin scenario.
   *
   * @param action
   *        an {@link Action} that will be executed to access the system state to be validated in the
   *        {@link ThenStep}. Use the special {@link Actions#result() result} or
   *        {@link Actions#response() response} actions to validate the last action result.
   * @return a {@link ThenStep} to validate the system state
   */
  public ThenStep then(final Action<?, ?> action) {
    return new ThenStep(lastResult(action));
  }

  private ActionResult<?> lastResult(final Action<?, ?> action) {
    if (action instanceof CapturingLastResultAction) {
      return captureLastActionResult();
    }
    return getResultFrom(action);
  }

  private ActionResult<?> captureLastActionResult() {
    final var lastAction = this.actions.removeLast();
    this.actions.forEach(a -> execute(a, ActionResult::validate));
    return execute(lastAction, Consumers.nop());
  }

  private ActionResult<?> getResultFrom(final Action<?, ?> action) {
    this.actions.forEach(a -> execute(a, ActionResult::validate));
    return execute(action, ActionResult::validate);
  }

  private ActionResult<?> execute(final Action<?, ?> action, final Consumer<ActionResult<?>> validator) {
    try {
      final var result = action.execute(this.context);
      validator.accept(result);
      return result;
    } catch (final Throwable e) {
      throw new AssertionError("A recorded action has failed, look at the stack trace for more information", e);
    }
  }

}
