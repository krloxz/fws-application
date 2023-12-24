package io.github.krloxz.fws;

import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.lang.Nullable;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.server.ServerErrorException;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

/**
 * Global exception handler that takes advantage of {@link ResponseEntityExceptionHandler} to handle
 * all application raised exceptions by returning errors formatted as per the Internet standard
 * <a href="https://datatracker.ietf.org/doc/html/rfc7807">Problem Details for HTTP APIs (RFC
 * 7807)</a>.
 *
 * @author Carlos Gomez
 */
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  private static final Log LOGGER = LogFactory.getLog(GlobalExceptionHandler.class);

  /**
   * Handles unexpected exceptions.
   *
   * @param exception
   *        the exception
   * @param exchange
   *        the current request-response exchange
   * @return a {@link Mono} wrapping an error response when the exchange response is not yet
   *         committed, a failing {@link Mono} otherwise
   */
  @ExceptionHandler
  public Mono<ResponseEntity<Object>> handleUnexpectedException(
      final Throwable exception, final ServerWebExchange exchange) {
    final var serverException = new ServerErrorException("The server is not able to handle the request", exception);
    return handleException(serverException, exchange);
  }

  /**
   * Customizes handling of {@link ServerErrorException} to add logging.
   */
  @Override
  protected Mono<ResponseEntity<Object>> handleServerErrorException(
      final ServerErrorException exception,
      final HttpHeaders headers,
      final HttpStatusCode status,
      final ServerWebExchange exchange) {
    LOGGER.error("The request %s produced a server error: ".formatted(format(exchange.getRequest())), exception);
    return super.handleServerErrorException(exception, headers, status, exchange);
  }

  private String format(final ServerHttpRequest request) {
    return request.getMethod() + " " + request.getURI();
  }

  /**
   * Customizes handling of {@link WebExchangeBindException} to add the binding errors to the response
   * and set a custom problem type.
   */
  @Override
  protected Mono<ResponseEntity<Object>> handleWebExchangeBindException(
      final WebExchangeBindException exception,
      final HttpHeaders headers,
      final HttpStatusCode status,
      final ServerWebExchange exchange) {

    final var problem = exception.updateAndGetBody(getMessageSource(), getLocale(exchange));
    problem.setType(exchange.getRequest().getURI().resolve("/probs/validation-error.html"));
    final var errors = exception.getAllErrors()
        .stream()
        .map(error -> {
          if (error instanceof final FieldError fieldError) {
            return new Error(fieldError.getField(), fieldError.getDefaultMessage());
          }
          return new Error(null, error.getDefaultMessage());
        }).toList();
    problem.setProperty("errors", errors);

    return handleExceptionInternal(exception, problem, headers, status, exchange);
  }

  /**
   * Customizes internal handling to set a custom problem type when body parameter is null.
   */
  @Override
  protected Mono<ResponseEntity<Object>> handleExceptionInternal(
      final Exception exception,
      @Nullable final Object body,
      @Nullable final HttpHeaders headers,
      final HttpStatusCode status,
      final ServerWebExchange exchange) {

    if (exchange.getResponse().isCommitted()) {
      return Mono.error(exception);
    }
    if (body == null && exception instanceof final ErrorResponse errorResponse) {
      final var problem = errorResponse.updateAndGetBody(getMessageSource(), getLocale(exchange));
      problem.setType(exchange.getRequest().getURI().resolve("probs/error.html"));
      return createResponseEntity(problem, headers, status, exchange);
    }
    return createResponseEntity(body, headers, status, exchange);
  }

  private static Locale getLocale(final ServerWebExchange exchange) {
    final var locale = exchange.getLocaleContext().getLocale();
    return locale != null ? locale : Locale.getDefault();
  }

  private static record Error(String attribute, String detail) {
  }

}
