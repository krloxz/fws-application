package io.github.krloxz.fws.test;

import org.springframework.hateoas.MediaTypes;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Actions to invoke the Freelancers API on the collection of freelancers registered in the system.
 *
 * @author Carlos Gomez
 */
public class FreelancerCollectionActions {

  private final FwsApplicationActions applicationActions;
  private final WebTestClient webClient;
  private final ActionsRecorder actionRecorder;

  FreelancerCollectionActions(
      final FwsApplicationActions applicationActions) {
    this.applicationActions = applicationActions;
    this.webClient = applicationActions.webClient();
    this.actionRecorder = applicationActions.actionsRecorder();
  }

  /**
   * Registers an action to invoke the endpoint that retrieves all freelancers.
   *
   * @return the {@link FwsApplicationActions}
   */
  public FwsApplicationActions listed() {
    this.actionRecorder.add(
        () -> this.webClient.get()
            .uri("/freelancers")
            .accept(MediaTypes.HAL_JSON)
            .exchange());
    return this.applicationActions;
  }

  /**
   * Registers an action to invoke the endpoint that retrieves a page of freelancers.
   *
   * @param page
   *        the page number
   * @param size
   *        the page size
   * @return the {@link FwsApplicationActions}
   */
  public FwsApplicationActions listed(final int page, final int size) {
    this.actionRecorder.add(
        () -> this.webClient.get()
            .uri("/freelancers?page={page}&size={size}", page, size)
            .accept(MediaTypes.HAL_JSON)
            .exchange());
    return this.applicationActions;
  }

}
