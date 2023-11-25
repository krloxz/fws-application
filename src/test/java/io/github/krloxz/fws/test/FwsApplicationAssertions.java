package io.github.krloxz.fws.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.assertj.core.api.ListAssert;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.core.TypeReferences.CollectionModelType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.reactive.server.WebTestClient;

import io.github.krloxz.fws.freelancer.application.FreelancerDto;

/**
 * Entry point to the assertions for the FwsApplication.
 * <p>
 * Assertions are designed to look at the state of the system from the end user perspective in order
 * to validate the side effects of functionalities that have been previously executed.
 * <p>
 * Assertions implement the THEN clause of the Gherkin DSL.
 *
 * @author Carlos Gomez
 */
@Component
public class FwsApplicationAssertions {

  private final WebTestClient webClient;

  /**
   * Creates a new instance
   *
   * @param webClient
   *        {@link WebTestClient} ready to perform HTTP request over the mock server provided by
   *        {@link SpringBootTest}
   */
  public FwsApplicationAssertions(final WebTestClient webClient) {
    this.webClient = webClient;
  }

  /**
   * @return the assertions to validate the freelancers registered in the system
   */
  public ListAssert<FreelancerDto> freelancers() {
    final var freelancers = this.webClient.get()
        .uri("/freelancers")
        .accept(MediaTypes.HAL_JSON)
        .exchange()
        .expectStatus().isOk()
        .returnResult(new CollectionModelType<FreelancerDto>() {})
        .getResponseBody()
        .blockLast()
        .getContent();

    return assertThat(List.copyOf(freelancers));
  }

}
