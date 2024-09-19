package io.github.krloxz.fws.freelancer;

import static io.github.krloxz.fws.freelancer.FreelancerActions.freelancer;
import static io.github.krloxz.fws.freelancer.FreelancerActions.freelancers;
import static io.github.krloxz.fws.freelancer.FreelancerMother.mobile;
import static io.github.krloxz.fws.freelancer.FreelancerMother.steveRogers;
import static io.github.krloxz.fws.freelancer.FreelancerMother.tonyStark;
import static io.github.krloxz.fws.freelancer.FreelancerMother.unregistered;
import static io.github.krloxz.fws.test.gherkin.TestScenario.given;
import static io.github.krloxz.fws.test.gherkin.actions.Actions.response;
import static io.github.krloxz.fws.test.gherkin.actions.Actions.systemReady;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import io.github.krloxz.fws.freelancer.application.dtos.FreelancerDto;
import io.github.krloxz.fws.freelancer.application.dtos.FreelancerDtoBuilder;
import io.github.krloxz.fws.freelancer.application.dtos.HourlyWageDto;
import io.github.krloxz.fws.test.FwsApplicationTest;

/**
 * Tests the Freelancers API.
 *
 * @author Carlos Gomez
 */
@FwsApplicationTest
class FreelancersApiTest {

  @Test
  void registersFreelancers() {
    given(systemReady())
        .when(freelancer(tonyStark()).registered())
        .and(freelancer(steveRogers()).registered())
        .then(freelancers().collection())
        .contains(jsonPath("_embedded.freelancers[*].firstName").value(hasItems("Tony", "Steve")));
  }

  @Test
  void returnsAffordancesWhenRegisteringFreelancers() {
    given(systemReady())
        .when(freelancer(tonyStark()).registered())
        .then(response())
        .contains(status().isCreated())
        .contains(jsonPath("_links.collection.href").isNotEmpty())
        .contains(jsonPath("_links.self.href").isNotEmpty())
        .contains(jsonPath("_links.changeAddress.href").isNotEmpty())
        .contains(jsonPath("_links.updateNicknames.href").isNotEmpty())
        .contains(jsonPath("_links.updateWage.href").isNotEmpty())
        .contains(jsonPath("_links.addCommunicationChannel.href").isNotEmpty())
        .contains(jsonPath("_links.removeCommunicationChannel")
            .value(hasSize(tonyStark().communicationChannels().size())));
  }

  @Test
  void failsToRegisterFreelancerWithInvalidData() {
    given(systemReady())
        .when(freelancer(invalidFreelancer()).registered())
        .then(response())
        .contains(status().isBadRequest())
        .contains(jsonPath("type").value(endsWith("/probs/validation-error.html")))
        .contains(jsonPath("errors").isArray())
        .contains(jsonPath("errors").isNotEmpty());
  }

  @Test
  void retrievesRegisteredFreelancer() {
    given(freelancer(tonyStark()).registered())
        .when(freelancer(tonyStark()).retrieved())
        .then(response())
        .contains(status().isOk())
        .contains(jsonPath("firstName").value("Tony"));
  }

  @Test
  void returnsNotFoundWhenRetrievingUnregisteredFreelancer() {
    given(systemReady())
        .when(freelancer(unregistered()).retrieved())
        .then(response())
        .contains(status().isNotFound())
        .contains(jsonPath("type").value(endsWith("/probs/error.html")));
  }

  @Test
  void updatesFreelancerWhenMovingToNewAddress() {
    given(freelancer(tonyStark()).registered())
        .when(freelancer(tonyStark()).movesTo(steveRogers().address()))
        .then(freelancers().collection())
        .contains(jsonPath("_embedded.freelancers[0].address.street").value(steveRogers().address().street()))
        .contains(jsonPath("_embedded.freelancers[0].address.apartment")
            .value(steveRogers().address().apartment().orElseThrow()))
        .contains(jsonPath("_embedded.freelancers[0].address.city").value(steveRogers().address().city()))
        .contains(jsonPath("_embedded.freelancers[0].address.state").value(steveRogers().address().state()))
        .contains(jsonPath("_embedded.freelancers[0].address.zipCode").value(steveRogers().address().zipCode()))
        .contains(jsonPath("_embedded.freelancers[0].address.country").value(steveRogers().address().country()));
  }

  @Test
  void returnsNotFoundWhenUpdatingAddressOfUnregisteredFreelancer() {
    given(systemReady())
        .when(freelancer(unregistered()).movesTo(steveRogers().address()))
        .then(response())
        .contains(status().isNotFound())
        .contains(jsonPath("type").value(endsWith("/probs/error.html")));
  }

  @Test
  void updatesFreelancerWhenAddingCommunicationChannel() {
    given(freelancer(tonyStark()).registered())
        .when(freelancer(tonyStark()).addsCommunicationChannel(mobile("901-234-8765")))
        .then(freelancers().collection())
        .contains(jsonPath("_embedded.freelancers[0].communicationChannels.length()").value(is(greaterThan(1))))
        .contains(jsonPath("_embedded.freelancers[0].communicationChannels.[*].value").value(hasItem("901-234-8765")))
        .contains(jsonPath("_embedded.freelancers[0].communicationChannels.[*].type").value(hasItem("MOBILE")));
  }

  @Test
  void returnsAffordancesToRemoveRegisteredCommunicationChannel() {
    given(freelancer(tonyStark()).registered())
        .when(freelancer(tonyStark()).addsCommunicationChannel(mobile("901-234-8765")))
        .then(response())
        .contains(jsonPath("_links.removeCommunicationChannel")
            .value(hasSize(tonyStark().communicationChannels().size() + 1)));
  }

  @Test
  void returnsNotFoundWhenAddingCommunicationChannelToUnregisteredFreelancer() {
    given(systemReady())
        .when(freelancer(unregistered()).addsCommunicationChannel(mobile("901-234-8765")))
        .then(response())
        .contains(status().isNotFound())
        .contains(jsonPath("type").value(endsWith("/probs/error.html")));
  }

  @Test
  void updatesFreelancerWhenRemovingCommunicationChannel() {
    final var tonyStark = tonyStark();
    final var removedChannelId = tonyStark.communicationChannels().iterator().next().id().orElseThrow();
    given(freelancer(tonyStark).registered())
        .when(freelancer(tonyStark).removesCommunicationChannel(removedChannelId))
        .then(freelancers().collection())
        .contains(jsonPath("_embedded.freelancers[0].communicationChannels[*].id")
            .value(not(hasItem(removedChannelId))));
  }

  @Test
  void removesAffordancesWhenRemovingAllCommunicationChannels() {
    final var tonyStark = tonyStark();
    final var channels = tonyStark.communicationChannels().iterator();
    final var channel1 = channels.next().id().orElseThrow();
    final var channel2 = channels.next().id().orElseThrow();
    given(freelancer(tonyStark).registered())
        .when(freelancer(tonyStark).removesCommunicationChannel(channel1))
        .and(freelancer(tonyStark).removesCommunicationChannel(channel2))
        .then(response())
        .contains(jsonPath("_links.removeCommunicationChannel").doesNotExist());
  }

  @Test
  void returnsNotFoundWhenRemovingCommunicationChannelFromUnregisteredFreelancer() {
    given(systemReady())
        .when(freelancer(unregistered()).removesCommunicationChannel(UUID.randomUUID().toString()))
        .then(response())
        .contains(status().isNotFound())
        .contains(jsonPath("type").value(endsWith("/probs/error.html")));
  }

  @Test
  void returnsUnprocessableEntityWhenRemovingNonExistentCommunicationChannel() {
    given(freelancer(tonyStark()).registered())
        .when(freelancer(tonyStark()).removesCommunicationChannel(UUID.randomUUID().toString()))
        .then(response())
        .contains(status().isUnprocessableEntity())
        .contains(jsonPath("type").value(endsWith("/probs/error.html")));
  }

  @Test
  void updatesFreelancerWhenUpdatingNicknames() {
    given(freelancer(tonyStark()).registered())
        .when(freelancer(tonyStark()).updatesNicknames("Ironman", "Tony", "Mr. Stark"))
        .then(freelancers().collection())
        .contains(jsonPath("_embedded.freelancers[0].nicknames").value(hasItems("Ironman", "Tony", "Mr. Stark")));
  }

  @Test
  void returnsNotFoundWhenUpdatingNicknamesOfUnregisteredFreelancer() {
    given(systemReady())
        .when(freelancer(unregistered()).updatesNicknames("Ironman", "Tony"))
        .then(response())
        .contains(status().isNotFound())
        .contains(jsonPath("type").value(endsWith("/probs/error.html")));
  }

  @Test
  void updatesFreelancerWhenUpdatingWage() {
    given(freelancer(tonyStark()).registered())
        .when(freelancer(tonyStark()).updatesWage(new HourlyWageDto(new BigDecimal("1000000"), "USD")))
        .then(freelancers().collection())
        .contains(jsonPath("_embedded.freelancers[0].wage.amount").value(is(closeTo(1_000_000, 0))))
        .contains(jsonPath("_embedded.freelancers[0].wage.currency").value("USD"));
  }

  @Test
  void returnsNotFoundWhenUpdatingWageOfUnregisteredFreelancer() {
    given(systemReady())
        .when(freelancer(unregistered()).updatesWage(new HourlyWageDto(new BigDecimal("1000000"), "USD")))
        .then(response())
        .contains(status().isNotFound())
        .contains(jsonPath("type").value(endsWith("/probs/error.html")));
  }

  private static FreelancerDto invalidFreelancer() {
    return new FreelancerDtoBuilder().build();
  }

}
