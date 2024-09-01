package io.github.krloxz.fws.test.dsl;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.context.ApplicationContext;

/**
 * @author Carlos Gomez
 */
public class ActionsRecorder {

  private final List<Action> actions;
  private final RestApi restApi;

  ActionsRecorder(final ApplicationContext context) {
    this.actions = new ArrayList<>();
    this.restApi = context.getBean(RestApi.class);
  }

  public ActionsRecorder and(final Action action) {
    this.actions.add(action);
    return this;
  }

  public ResultAssertions then(final Action finalAction) {
    if (!(finalAction instanceof Action.NoOpAction)) {
      this.actions.add(finalAction);
    }
    return executeActionsAndReturnLastResult()
        .orElseThrow(() -> new IllegalStateException("No actions have been recorded"));
  }

  private Optional<ResultAssertions> executeActionsAndReturnLastResult() {
    try {
      return this.actions.stream()
          .map(action -> action.execute(this.restApi))
          .reduce(this::validateFirstThenReturnSecond);
    } catch (final Throwable e) {
      throw new AssertionError("Not all recorded actions succeeded", e);
    }
  }

  private ResultAssertions validateFirstThenReturnSecond(final ResultAssertions first, final ResultAssertions second) {
    first.andExpect(status().is2xxSuccessful());
    return second;
  }

}
