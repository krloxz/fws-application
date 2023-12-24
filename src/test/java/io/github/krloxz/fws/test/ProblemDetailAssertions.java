package io.github.krloxz.fws.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.ListAssert;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

/**
 * Assertions to validate the problem details of a response.
 *
 * @author Carlos Gomez
 */
public class ProblemDetailAssertions extends AbstractAssert<ProblemDetailAssertions, ProblemDetail> {

  ProblemDetailAssertions(final ProblemDetail actual) {
    super(actual, ProblemDetailAssertions.class);
  }

  /**
   * Asserts that the actual problem detail has the supplied HTTP status.
   *
   * @param status
   *        the HTTP status
   * @return this instance
   */
  public ProblemDetailAssertions hasStatus(final HttpStatus status) {
    isNotNull();

    final var actualStatus = HttpStatus.valueOf(this.actual.getStatus());
    if (actualStatus != status) {
      failWithMessage("Expected problem's status to be <%s> but was <%s>", status, actualStatus);
    }

    return this;
  }

  /**
   * @return a new {@link ListAssert} which actual object is the {@code errors} property of the actual
   *         problem detail
   */
  @SuppressWarnings("unchecked")
  public ListAssert<Object> hasValidationErrorsThat() {
    isNotNull();

    return assertThat(
        Optional.ofNullable(this.actual.getProperties())
            .map(properties -> (List<Object>) properties.get("errors"))
            .orElse(null));
  }

}
