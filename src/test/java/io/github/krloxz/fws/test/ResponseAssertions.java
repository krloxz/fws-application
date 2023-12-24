package io.github.krloxz.fws.test;

import org.assertj.core.api.AbstractAssert;
import org.springframework.http.ProblemDetail;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;

/**
 * Assertions to validate the response of a recoded action.
 *
 * @author Carlos Gomez
 */
public class ResponseAssertions extends AbstractAssert<ResponseAssertions, ResponseSpec> {

  ResponseAssertions(final ResponseSpec actual) {
    super(actual, ResponseAssertions.class);
  }

  /**
   * @return the assertions that can be used to validate the problem details of the actual response
   */
  public ProblemDetailAssertions hasProblemThat() {
    isNotNull();
    return new ProblemDetailAssertions(this.actual.expectBody(ProblemDetail.class).returnResult().getResponseBody());
  }

}
