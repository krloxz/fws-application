package io.github.krloxz.fws.test;

import org.springframework.context.ApplicationContext;
import org.springframework.test.web.reactive.server.WebTestClient;

import io.github.krloxz.fws.freelancer.application.dtos.FreelancerDto;

/**
 * Entry point to execute actions that invoke the functionalities of the FwsApplication.
 * <p>
 * Actions implement the WHEN clause of the Gherkin DSL.
 *
 * @author Carlos Gomez
 */
public class FwsApplicationActions {

  private final WebTestClient webClient;
  private final RecordedActions recordedActions;
  private final ApplicationContext context;

  /**
   * Creates a new instance.
   *
   * @param context
   *        the current Spring application context, used to manually retrieve required dependencies
   */
  public FwsApplicationActions(final ApplicationContext context) {
    this.context = context;
    this.webClient = context.getBean(WebTestClient.class);
    this.recordedActions = new RecordedActions();
  }

  /**
   * @param freelancer
   *        DTO representing the freelancer that will be affected by the invoked actions
   * @return the {@link FreelancerActions}
   */
  public FreelancerActions freelancer(final FreelancerDto freelancer) {
    return new FreelancerActions(freelancer, this);
  }

  /**
   * @return the {@link FwsApplicationAssertions}
   */
  public FwsApplicationAssertions then() {
    return new FwsApplicationAssertions(this.recordedActions, this.context);
  }

  /**
   * @return the action recorder where all the actions required for the current test are being
   *         recorded
   */
  ActionsRecorder actionsRecorder() {
    return this.recordedActions;
  }

  /**
   * @return a web test client ready to use
   */
  public WebTestClient webClient() {
    return this.webClient;
  }

}
