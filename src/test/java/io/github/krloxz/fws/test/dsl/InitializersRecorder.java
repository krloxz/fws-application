package io.github.krloxz.fws.test.dsl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationContext;

/**
 * @author Carlos Gomez
 */
public class InitializersRecorder {

  private final ApplicationContext context;
  private final List<Action> initActions;

  InitializersRecorder(final ApplicationContext context) {
    this.context = context;
    this.initActions = new ArrayList<>();
  }

  public InitializersRecorder with(final Action action) {
    this.initActions.add(action);
    return this;
  }

  public InitializersRecorder with(final Action... actions) {
    this.initActions.addAll(List.of(actions));
    return this;
  }

  public ActionsRecorder when(final Action initialAction) {
    final var restApi = this.context.getBean(RestApi.class);
    this.initActions.forEach(action -> action.execute(restApi));
    return new ActionsRecorder(this.context).and(initialAction);
  }

}
