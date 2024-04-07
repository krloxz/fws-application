package io.github.krloxz.fws.test;

import org.springframework.hateoas.MediaTypes;
import org.springframework.test.web.reactive.server.WebTestClient;

import io.github.krloxz.fws.freelancer.application.dtos.AddressDto;
import io.github.krloxz.fws.freelancer.application.dtos.CommunicationChannelDto;
import io.github.krloxz.fws.freelancer.application.dtos.FreelancerDto;

/**
 * Actions to invoke the Freelancers API.
 *
 * @author Carlos Gomez
 */
public class FreelancerActions {

  private final FreelancerDto freelancer;
  private final FwsApplicationActions applicationActions;
  private final WebTestClient webClient;
  private final ActionsRecorder actionRecorder;

  FreelancerActions(
      final FreelancerDto freelancer,
      final FwsApplicationActions applicationActions) {
    this.freelancer = freelancer;
    this.applicationActions = applicationActions;
    this.webClient = applicationActions.webClient();
    this.actionRecorder = applicationActions.actionsRecorder();
  }

  /**
   * Registers an action to invoke the registration endpoint.
   *
   * @return the {@link FwsApplicationActions}
   */
  public FwsApplicationActions registered() {
    this.actionRecorder.add(
        () -> this.webClient.post()
            .uri("/freelancers")
            .accept(MediaTypes.HAL_JSON)
            .bodyValue(this.freelancer)
            .exchange());
    return this.applicationActions;
  }

  /**
   * Registers an action to invoke the endpoint that retrieves freelancers by ID.
   *
   * @return the {@link FwsApplicationActions}
   */
  public FwsApplicationActions retrieved() {
    this.actionRecorder.add(
        () -> this.webClient.get()
            .uri("/freelancers/" + freelancerId())
            .accept(MediaTypes.HAL_JSON)
            .exchange());
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
    this.actionRecorder.add(
        () -> this.webClient.patch()
            .uri("/freelancers/" + freelancerId() + "/address")
            .accept(MediaTypes.HAL_JSON)
            .bodyValue(newAddress)
            .exchange());
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
    this.actionRecorder.add(
        () -> this.webClient.post()
            .uri("/freelancers/" + freelancerId() + "/communication-channels")
            .accept(MediaTypes.HAL_JSON)
            .bodyValue(channel)
            .exchange());
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
    this.actionRecorder.add(
        () -> this.webClient.delete()
            .uri("/freelancers/" + freelancerId() + "/communication-channels/" + id)
            .accept(MediaTypes.HAL_JSON)
            .exchange());
    return this.applicationActions;
  }

  private String freelancerId() {
    return this.freelancer.id().orElseThrow();
  }

}
