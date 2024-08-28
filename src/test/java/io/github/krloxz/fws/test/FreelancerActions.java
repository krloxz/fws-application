package io.github.krloxz.fws.test;

import io.github.krloxz.fws.freelancer.application.dtos.AddressDto;
import io.github.krloxz.fws.freelancer.application.dtos.CommunicationChannelDto;
import io.github.krloxz.fws.freelancer.application.dtos.FreelancerDto;
import io.github.krloxz.fws.freelancer.application.dtos.HourlyWageDto;

/**
 * Actions to invoke the Freelancers API on an individual freelancer.
 *
 * @author Carlos Gomez
 */
public class FreelancerActions {

  private final FreelancerDto freelancer;
  private final FwsApplicationActions applicationActions;
  private final FwsApplicationRestApi restApi;

  FreelancerActions(final FreelancerDto freelancer, final FwsApplicationActions applicationActions) {
    this.freelancer = freelancer;
    this.applicationActions = applicationActions;
    this.restApi = applicationActions.restApi();
  }

  /**
   * Registers an action to invoke the registration endpoint.
   *
   * @return the {@link FwsApplicationActions}
   */
  public FwsApplicationActions registered() {
    this.applicationActions.register(() -> this.restApi.post("/freelancers", this.freelancer));
    return this.applicationActions;
  }

  /**
   * Registers an action to invoke the endpoint that retrieves freelancers by ID.
   *
   * @return the {@link FwsApplicationActions}
   */
  public FwsApplicationActions retrieved() {
    this.applicationActions.register(() -> this.restApi.get("/freelancers/" + freelancerId()));
    return this.applicationActions;
  }

  /**
   * Registers an action to invoke the endpoint that moves a freelancer to a new address.
   *
   * @param newAddress
   *        the new address
   * @return the {@link FwsApplicationActions}
   */
  public FwsApplicationActions movesTo(final AddressDto newAddress) {
    this.applicationActions.register(
        () -> this.restApi.patch("/freelancers/" + freelancerId() + "/address", newAddress));
    return this.applicationActions;
  }

  /**
   * Registers an action to invoke the endpoint that updates the nicknames of a freelancer.
   *
   * @param nicknames
   *        the new nicknames
   * @return the {@link FwsApplicationActions}
   */
  public FwsApplicationActions updatesNicknames(final String... nicknames) {
    this.applicationActions.register(
        () -> this.restApi.patch("/freelancers/" + freelancerId() + "/nicknames", nicknames));
    return this.applicationActions;
  }

  /**
   * Registers an action to invoke the endpoint that updates the hourly wage of a freelancer.
   *
   * @param wage
   *        the new hourly wage
   * @return the {@link FwsApplicationActions}
   */
  public FwsApplicationActions updatesWage(final HourlyWageDto wage) {
    this.applicationActions.register(() -> this.restApi.patch("/freelancers/" + freelancerId() + "/wage", wage));
    return this.applicationActions;
  }

  /**
   * Registers an action to invoke the endpoint that adds a communication channel.
   *
   * @param channel
   *        the communication channel to add
   * @return the {@link FwsApplicationActions}
   */
  public FwsApplicationActions addsCommunicationChannel(final CommunicationChannelDto channel) {
    this.applicationActions.register(
        () -> this.restApi.post("/freelancers/" + freelancerId() + "/communication-channels", channel));
    return this.applicationActions;
  }

  /**
   * Registers an action to invoke the endpoint that removes a communication channel.
   *
   * @param id
   *        the identifier of the communication channel to remove
   * @return the {@link FwsApplicationActions}
   */
  public FwsApplicationActions removesCommunicationChannel(final String id) {
    this.applicationActions.register(
        () -> this.restApi.delete("/freelancers/" + freelancerId() + "/communication-channels/" + id));
    return this.applicationActions;
  }

  private String freelancerId() {
    return this.freelancer.id().orElseThrow();
  }

}
