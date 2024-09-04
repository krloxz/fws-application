package io.github.krloxz.fws.freelancer;

import java.util.Optional;

import io.github.krloxz.fws.freelancer.application.dtos.AddressDto;
import io.github.krloxz.fws.freelancer.application.dtos.CommunicationChannelDto;
import io.github.krloxz.fws.freelancer.application.dtos.FreelancerDto;
import io.github.krloxz.fws.freelancer.application.dtos.HourlyWageDto;
import io.github.krloxz.fws.test.gherkin.restapi.RestApiAction;

/**
 * Provides actions to interact with the Freelancers API.
 *
 * @author Carlos Gomez
 */
final class FreelancerActions {

  private final Optional<FreelancerDto> freelancer;

  private FreelancerActions(final FreelancerDto freelancer) {
    this.freelancer = Optional.ofNullable(freelancer);
  }

  static FreelancerActions freelancer(final FreelancerDto dto) {
    return new FreelancerActions(dto);
  }

  static FreelancerActions freelancer(final String firstName, final String lastName) {
    return freelancer(FreelancerMother.freelancer(firstName, lastName));
  }

  static FreelancerActions freelancers() {
    return freelancer(null);
  }

  RestApiAction retrieved() {
    return restApi -> restApi.get("/freelancers/" + freelancerId());
  }

  RestApiAction listed(final int page, final int size) {
    return restApi -> restApi.get("/freelancers?page={page}&size={size}", page, size);
  }

  RestApiAction listed() {
    return restApi -> restApi.get("/freelancers");
  }

  RestApiAction collection() {
    return listed();
  }

  RestApiAction registered() {
    return restApi -> restApi.post("/freelancers", freelancer());
  }

  RestApiAction movesTo(final AddressDto newAddress) {
    return restApi -> restApi.patch("/freelancers/" + freelancerId() + "/address", newAddress);
  }

  RestApiAction updatesNicknames(final String... nicknames) {
    return restApi -> restApi.patch("/freelancers/" + freelancerId() + "/nicknames", nicknames);
  }

  RestApiAction updatesWage(final HourlyWageDto wage) {
    return restApi -> restApi.patch("/freelancers/" + freelancerId() + "/wage", wage);
  }

  RestApiAction addsCommunicationChannel(final CommunicationChannelDto channel) {
    return restApi -> restApi.post("/freelancers/" + freelancerId() + "/communication-channels", channel);
  }

  RestApiAction removesCommunicationChannel(final String id) {
    return restApi -> restApi.delete("/freelancers/" + freelancerId() + "/communication-channels/" + id);
  }

  private FreelancerDto freelancer() {
    return this.freelancer.orElseThrow(() -> new IllegalStateException("No freelancer provided"));
  }

  private String freelancerId() {
    return freelancer().id().orElseThrow();
  }

}
