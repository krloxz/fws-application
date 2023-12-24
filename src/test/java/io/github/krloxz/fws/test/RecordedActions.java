package io.github.krloxz.fws.test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.assertj.core.api.AbstractAssert;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;

/**
 * {@link ActionsRecorder} that also provides access to the recorded actions.
 *
 * @author Carlos Gomez
 */
class RecordedActions extends AbstractAssert<RecordedActions, List<Supplier<ResponseSpec>>> implements ActionsRecorder {

  private final List<Supplier<ResponseSpec>> actions;

  RecordedActions() {
    this(new ArrayList<>());
  }

  private RecordedActions(final List<Supplier<ResponseSpec>> actions) {
    super(actions, RecordedActions.class);
    this.actions = actions;
  }

  @Override
  public void add(final Supplier<ResponseSpec> action) {
    this.actions.add(action);
  }

  /**
   * Plays the recorded actions in the order they were recorded and asserts that all they succeeded.
   */
  void succeed() {
    try {
      this.actions.stream()
          .map(Supplier::get)
          .forEach(response -> response.expectStatus().is2xxSuccessful().returnResult(Void.class));
    } catch (final AssertionError e) {
      throw failure("Not all the recorded actions succeded: %s", e);
    }
  }

  /**
   * Plays the recorded actions in the order they were recorded to get the response of the last
   * action. All the actions, but the last one, are asserted to be successful.
   *
   * @return the response of the last action
   */
  ResponseSpec lastResponse() {
    try {
      return this.actions.stream()
          .map(Supplier::get)
          .reduce((first, second) -> {
            first.expectStatus().is2xxSuccessful().returnResult(Void.class);
            return second;
          })
          .orElseThrow(() -> new IllegalStateException("No actions have been recorded"));
    } catch (final AssertionError e) {
      throw failure("Not all the recorded actions succeded: %s", e);
    }
  }

}
