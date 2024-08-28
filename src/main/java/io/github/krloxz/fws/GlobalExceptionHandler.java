package io.github.krloxz.fws;

import java.net.URI;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ServerErrorException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * Global exception handler that takes advantage of {@link ResponseEntityExceptionHandler} to handle
 * all application raised exceptions by returning errors formatted as per the Internet standard
 * <a href="https://datatracker.ietf.org/doc/html/rfc9457">Problem Details for HTTP APIs (RFC
 * 9457)</a>.
 *
 * @author Carlos Gomez
 */
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  private static final Log LOGGER = LogFactory.getLog(GlobalExceptionHandler.class);

  /**
   * Wraps unexpected exceptions into a {@link ServerErrorException} and delegates to
   * {@link #handleErrorResponseException(ErrorResponseException, HttpHeaders, HttpStatusCode, WebRequest)}.
   *
   * @throws Exception
   */
  @ExceptionHandler
  public ResponseEntity<Object> handleUnexpectedException(final Throwable exception, final WebRequest request)
      throws Exception {
    return handleException(
        new ServerErrorException("The server is not able to handle the request", exception), request);
  }

  @Override
  protected ResponseEntity<Object> handleErrorResponseException(
      final ErrorResponseException exception,
      final HttpHeaders headers,
      final HttpStatusCode status,
      final WebRequest request) {
    LOGGER.error("The request %s produced a server error: ".formatted(format(request)), exception);
    return super.handleErrorResponseException(exception, headers, status, request);
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      final MethodArgumentNotValidException exception,
      final HttpHeaders headers,
      final HttpStatusCode status, final WebRequest request) {
    final var response = super.handleMethodArgumentNotValid(exception, headers, status, request);
    if (response.getBody() instanceof final ProblemDetail problem) {
      problem.setType(requestUri().resolve("/probs/validation-error.html"));
      problem.setProperty("errors", getValidationErrors(exception));
    }
    return response;
  }

  @Override
  protected ResponseEntity<Object> createResponseEntity(
      final Object body, final HttpHeaders headers, final HttpStatusCode statusCode, final WebRequest request) {
    if (body instanceof final ProblemDetail problem) {
      problem.setType(requestUri().resolve("/probs/error.html"));
    }
    return super.createResponseEntity(body, headers, statusCode, request);
  }

  private static String format(final WebRequest request) {
    if (request instanceof final ServletWebRequest servletRequest) {
      return servletRequest.getRequest().getMethod() + " " + servletRequest.getRequest().getRequestURI();
    }
    return "";
  }

  private static URI requestUri() {
    return ServletUriComponentsBuilder.fromCurrentRequest().build().toUri();
  }

  private static List<Error> getValidationErrors(final MethodArgumentNotValidException exception) {
    return exception.getAllErrors()
        .stream()
        .map(error -> {
          if (error instanceof final FieldError fieldError) {
            return new Error(fieldError.getField(), fieldError.getDefaultMessage());
          }
          return new Error(null, error.getDefaultMessage());
        }).toList();
  }

  private static record Error(String attribute, String detail) {
  }

}
