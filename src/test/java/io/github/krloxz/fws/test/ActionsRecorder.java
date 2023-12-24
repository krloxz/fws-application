package io.github.krloxz.fws.test;

import java.util.function.Supplier;

import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;

/**
 * Contract to record system actions during the execution of a test method.
 *
 * @author Carlos Gomez
 */
interface ActionsRecorder {

  /**
   * Records an action.
   *
   * @param action
   *        a supplier of {@link ResponseSpec} that encapsulates the action to be recorded
   */
  void add(final Supplier<ResponseSpec> action);

}
