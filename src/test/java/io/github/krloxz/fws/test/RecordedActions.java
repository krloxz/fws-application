package io.github.krloxz.fws.test;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Collection of system actions that have been recorded for the execution of a test.
 *
 * @author Carlos Gomez
 */
class RecordedActions {

  private final List<Supplier<ResultAssertions>> actions;

  RecordedActions() {
    this.actions = new ArrayList<>();
  }

  void add(final Supplier<ResultAssertions> result) {
    this.actions.add(result);
  }

  /**
   * Plays the recorded actions in the order they were recorded and asserts that all they succeed.
   */
  void succeed() {
    try {
      this.actions.stream()
          .map(Supplier::get)
          .forEach(result -> result.andExpect(status().is2xxSuccessful()));
    } catch (final Throwable e) {
      throw new AssertionError("Not all recorded actions succeeded", e);
    }
  }

  /**
   * Plays the recorded actions in the order they were recorded to get the result of the last action.
   * All the actions, but the last one, are asserted to be successful.
   *
   * @return the result of the last action
   */
  ResultAssertions lastResult() {
    return playActionsAndReturnLast()
        .orElseThrow(() -> new IllegalStateException("No actions have been recorded"));
  }

  private Optional<ResultAssertions> playActionsAndReturnLast() {
    try {
      return this.actions.stream()
          .map(Supplier::get)
          .reduce(this::executeFirstThenReturnSecond);
    } catch (final Throwable e) {
      throw new AssertionError("Not all previous actions succeeded", e);
    }
  }

  private ResultAssertions executeFirstThenReturnSecond(final ResultAssertions first, final ResultAssertions second) {
    first.andExpect(status().is2xxSuccessful());
    return second;
  }

}
