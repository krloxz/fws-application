package io.github.krloxz.fws.test;

import java.util.function.Consumer;

import org.springframework.web.client.RestClientException;

import com.atlassian.oai.validator.OpenApiInteractionValidator;
import com.atlassian.oai.validator.model.SimpleRequest;
import com.atlassian.oai.validator.model.SimpleResponse;
import com.atlassian.oai.validator.report.JsonValidationReportFormat;
import com.atlassian.oai.validator.report.ValidationReport;

/**
 * {@link Consumer} that validates an {@link ExchangeResult} against the Open API specification
 * located at the classpath {@code /static/openapi.yml}.
 * <p>
 * Based on
 * https://bitbucket.org/atlassian/swagger-request-validator/src/master/swagger-request-validator-spring-web-client/src/main/java/com/atlassian/oai/validator/springweb/client/OpenApiValidationClientHttpRequestInterceptor.java"
 *
 * @author Carlos Gomez
 */
public class OpenApiValidator implements Consumer<ExchangeResult> {

  @Override
  public void accept(final ExchangeResult result) {
    final var requestBuilder =
        new SimpleRequest.Builder(result.request().method().name(), result.request().url().getPath())
            .withBody(result.requestBody());
    result.request().headers().forEach(requestBuilder::withHeader);
    // TODO: populate query params

    final var responseBuilder = new SimpleResponse.Builder(result.response().statusCode().value())
        .withBody(result.responseBody());
    result.response().headers().asHttpHeaders().forEach(responseBuilder::withHeader);

    final var validator = OpenApiInteractionValidator.createForSpecificationUrl("/static/openapi.yml").build();
    final var report = validator.validate(requestBuilder.build(), responseBuilder.build());
    if (report.hasErrors()) {
      throw new OpenApiValidationException(report);
    }
  }

  /**
   * A {@link RestClientException} which indicates that the request or response does not conform to
   * the Open API spec.
   */
  public static class OpenApiValidationException extends RestClientException {

    private static final long serialVersionUID = 619644394409421778L;

    public OpenApiValidationException(final ValidationReport report) {
      super(JsonValidationReportFormat.getInstance().apply(report));
    }

  }

}
