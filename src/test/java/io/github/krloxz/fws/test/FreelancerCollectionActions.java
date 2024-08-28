package io.github.krloxz.fws.test;

/**
 * Actions to invoke the Freelancers API on the collection of freelancers registered in the system.
 *
 * @author Carlos Gomez
 */
public class FreelancerCollectionActions {

  private final FwsApplicationActions applicationActions;
  private final FwsApplicationRestApi restApi;

  FreelancerCollectionActions(final FwsApplicationActions applicationActions) {
    this.applicationActions = applicationActions;
    this.restApi = applicationActions.restApi();
  }

  /**
   * Registers an action to invoke the endpoint that retrieves the first page of freelancers using the
   * default page size.
   *
   * @return the {@link FwsApplicationActions}
   */
  public FwsApplicationActions listed() {
    this.applicationActions.register(() -> this.restApi.get("/freelancers"));
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
    this.applicationActions.register(
        () -> this.restApi.get("/freelancers?page={page}&size={size}", page, size));
    return this.applicationActions;
  }

}
