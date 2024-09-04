package io.github.krloxz.fws.test.gherkin.restapi;

import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MvcResult;

import io.github.krloxz.fws.test.gherkin.actions.Action;
import io.github.krloxz.fws.test.gherkin.actions.InvalidActionResultException;

/**
 * A generic {@link Action} interface designed for declaring actions that interact with the
 * {@link RestApi} of the system.
 * <p>
 * {@code RestApiAction} interfaces leverage Spring's {@link MvcResult} to represent the outcome of
 * REST API interactions. When these interactions result in an HTTP error response (4xx or 5xx
 * status codes), the {@link #validateResult(MvcResult) validation method} triggers an
 * {@link InvalidActionResultException}, providing detailed information about the failed request.
 * <p>
 * This interface is intended to be implemented as a lambda expression or method reference,
 * providing a simple and flexible way to define REST-API interactions.
 *
 * @author Carlos Gomez
 */
@FunctionalInterface
public interface RestApiAction extends Action<RestApi, MvcResult> {

  @Override
  default Class<RestApi> inputType() {
    return RestApi.class;
  }

  @Override
  default void validateResult(final MvcResult result) {
    final var responseStatus = result.getResponse().getStatus();
    if (HttpStatus.valueOf(responseStatus).isError()) {
      final var requestMethod = result.getRequest().getMethod();
      final var requestURI = result.getRequest().getRequestURI();
      throw new InvalidActionResultException(
          "RestApiAction '%s %s' has failed with HTTP status %d".formatted(requestMethod, requestURI, responseStatus));
    }
  }

}
