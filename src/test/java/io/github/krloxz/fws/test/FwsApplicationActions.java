package io.github.krloxz.fws.test;

import java.util.List;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
import org.springframework.test.web.reactive.server.WebTestClient;

import io.github.krloxz.fws.freelancer.application.FreelancerDto;

/**
 * Entry point to execute actions that invoke the functionalities of the FwsApplication.
 * <p>
 * Actions implement the WHEN clause of the Gherkin DSL.
 *
 * @author Carlos Gomez
 */
@Component
public class FwsApplicationActions {

  private final WebTestClient webClient;
  private final FwsApplicationAssertions assertions;

  /**
   * Creates a new instance
   *
   * @param assertions
   *        the {@link FwsApplicationAssertions}
   * @param webClient
   *        {@link WebTestClient} ready to perform HTTP request over the mock server provided by
   *        {@link SpringBootTest}
   */
  public FwsApplicationActions(final FwsApplicationAssertions assertions, final WebTestClient webClient) {
    this.webClient = webClient;
    this.assertions = assertions;
  }

  /**
   * @param freelancers
   *        DTO's representing the freelancers that will be affected by the invoked actions
   * @return the {@link FreelancerActions}
   */
  public FreelancerActions freelancers(final FreelancerDto... freelancers) {
    return new FreelancerActions(List.of(freelancers), this.webClient, this);
  }

  /**
   * @return the {@link FwsApplicationAssertions}
   */
  public FwsApplicationAssertions then() {
    return this.assertions;
  }

}
