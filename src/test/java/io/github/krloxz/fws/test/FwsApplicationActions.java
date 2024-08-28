package io.github.krloxz.fws.test;

import java.util.function.Supplier;

import org.springframework.context.ApplicationContext;

import io.github.krloxz.fws.freelancer.application.dtos.FreelancerDto;

/**
 * Entry point to execute actions that invoke the functionalities of the FwsApplication.
 * <p>
 * Actions implement the WHEN clause of the Gherkin DSL.
 *
 * @author Carlos Gomez
 */
public class FwsApplicationActions {

  private final RecordedActions recordedActions;
  private final ApplicationContext context;

  /**
   * Creates a new instance.
   *
   * @param context
   *        the current Spring application context, used to manually retrieve required dependencies
   */
  FwsApplicationActions(final ApplicationContext context) {
    this.context = context;
    this.recordedActions = new RecordedActions();
  }

  /**
   * @param freelancer
   *        DTO representing the freelancer that will be affected by the invoked actions
   * @return the actions that can be executed on an individual freelancer
   */
  public FreelancerActions freelancer(final FreelancerDto freelancer) {
    return new FreelancerActions(freelancer, this);
  }

  /**
   * @return the actions that can be executed on the collection of freelancers registered in the
   *         system
   */
  public FreelancerCollectionActions freelancers() {
    return new FreelancerCollectionActions(this);
  }

  /**
   * @return the {@link FwsApplicationAssertions}
   */
  public FwsApplicationAssertions then() {
    return new FwsApplicationAssertions(this.recordedActions, this.context);
  }

  void register(final Supplier<ResultAssertions> result) {
    this.recordedActions.add(result);
  }

  FwsApplicationRestApi restApi() {
    return this.context.getBean(FwsApplicationRestApi.class);
  }

}
