package io.github.krloxz.fws.test;

import java.util.List;
import java.util.function.Supplier;

import org.springframework.hateoas.MediaTypes;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;

import io.github.krloxz.fws.freelancer.application.dtos.AddressDto;
import io.github.krloxz.fws.freelancer.application.dtos.CommunicationChannelDto;
import io.github.krloxz.fws.freelancer.application.dtos.FreelancerDto;

/**
 * Actions to invoke the Freelancers API.
 *
 * @author Carlos Gomez
 */
public class FreelancerActions {

  private final List<FreelancerDto> freelancers;
  private final FwsApplicationActions applicationActions;
  private final WebTestClient webClient;
  private final ActionsRecorder actionRecorder;

  FreelancerActions(
      final List<FreelancerDto> freelancers,
      final FwsApplicationActions applicationActions) {
    this.freelancers = freelancers;
    this.applicationActions = applicationActions;
    this.webClient = applicationActions.webClient();
    this.actionRecorder = applicationActions.actionsRecorder();
  }

  /**
   * Invokes the registration endpoint.
   *
   * @return the {@link FwsApplicationActions}
   */
  public FwsApplicationActions registered() {
    this.freelancers.stream()
        .map(this::register)
        .forEach(this.actionRecorder::add);
    return this.applicationActions;
  }

  /**
   * Invokes the endpoint that retrieves freelancers by ID.
   *
   * @param id
   *        identifier of the freelancer to retrieve
   * @return the {@link FwsApplicationActions}
   */
  public FwsApplicationActions retrieved(final String id) {
    this.actionRecorder.add(
        () -> this.webClient.get()
            .uri("/freelancers/" + id)
            .accept(MediaTypes.HAL_JSON)
            .exchange());
    return this.applicationActions;
  }

  /**
   * Invokes the endpoint that moves a freelancer to a new address.
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

  private Supplier<ResponseSpec> register(final FreelancerDto freelancer) {
    return () -> this.webClient.post()
        .uri("/freelancers")
        .accept(MediaTypes.HAL_JSON)
        .bodyValue(freelancer)
        .exchange();
  }

  private String freelancerId() {
    if (this.freelancers.size() != 1) {
      throw new IllegalStateException("There are " + this.freelancers.size() + " freelancers, only one is expected.");
    }
    return this.freelancers.get(0).id().orElseThrow();
  }

}
