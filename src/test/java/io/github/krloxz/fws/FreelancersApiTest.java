package io.github.krloxz.fws;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import io.github.krloxz.fws.freelancer.application.dtos.AddressDtoBuilder;
import io.github.krloxz.fws.freelancer.application.dtos.CommunicationChannelDto;
import io.github.krloxz.fws.freelancer.application.dtos.CommunicationChannelDtoBuilder;
import io.github.krloxz.fws.freelancer.application.dtos.FreelancerDto;
import io.github.krloxz.fws.freelancer.application.dtos.FreelancerDtoBuilder;
import io.github.krloxz.fws.freelancer.application.dtos.HourlyWageDto;
import io.github.krloxz.fws.freelancer.domain.CommunicationChannel;
import io.github.krloxz.fws.freelancer.domain.Gender;
import io.github.krloxz.fws.test.FwsApplicationTest;
import io.github.krloxz.fws.test.TestFwsApplication;

/**
 * Tests the Freelancers API.
 *
 * @author Carlos Gomez
 */
@FwsApplicationTest
class FreelancersApiTest {

  @Autowired
  private TestFwsApplication fwsApplication;

  @Test
  void registersFreelancers() {
    this.fwsApplication.running()
        .when()
        .freelancer(tonyStark()).registered()
        .freelancer(steveRogers()).registered()
        .then()
        .freelancers()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("_embedded.freelancers[*].firstName").value(hasItems("Tony", "Steve"));
  }

  @Test
  void providesBasicAffordancesWhenFreelancerRegistered() {
    this.fwsApplication.running()
        .when()
        .freelancer(tonyStark()).registered()
        .then()
        .response()
        .expectStatus().isCreated()
        .expectBody()
        .jsonPath("_links.collection.href").isNotEmpty()
        .jsonPath("_links.self.href").isNotEmpty()
        .jsonPath("_links.changeAddress.href").isNotEmpty()
        .jsonPath("_links.updateNicknames.href").isNotEmpty()
        .jsonPath("_links.updateWage.href").isNotEmpty()
        .jsonPath("_links.addCommunicationChannel.href").isNotEmpty()
        .jsonPath("_links.removeCommunicationChannel").value(hasSize(tonyStark().communicationChannels().size()));
  }

  @Test
  void reportsValidationErrorWhenRegisteringFreelancerWithInvalidData() {
    this.fwsApplication.running()
        .when()
        .freelancer(invalidFreelancer()).registered()
        .then()
        .response()
        .expectStatus().isBadRequest()
        .expectBody()
        .jsonPath("type").isEqualTo("/probs/validation-error.html")
        .jsonPath("errors").isArray()
        .jsonPath("errors").isNotEmpty();
  }

  @Test
  void retrievesRegisteredFreelancer() {
    this.fwsApplication.runningWith()
        .freelancers(tonyStark())
        .when()
        .freelancer(tonyStark()).retrieved()
        .then()
        .response()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("firstName").isEqualTo("Tony");
  }

  @Test
  void reportsNotFoundErrorWhenRetrievingUnregisteredFreelancer() {
    this.fwsApplication.running()
        .when()
        .freelancer(unregistered()).retrieved()
        .then()
        .response()
        .expectStatus().isNotFound()
        .expectBody()
        .jsonPath("type").isEqualTo("/probs/error.html");
  }

  @Test
  void updatesFreelancerWhenMovingToNewAddress() {
    this.fwsApplication.runningWith()
        .freelancers(tonyStark())
        .when()
        .freelancer(tonyStark()).movesTo(steveRogers().address())
        .then()
        .freelancers()
        .expectBody()
        .jsonPath("_embedded.freelancers[0].address.street").isEqualTo(steveRogers().address().street())
        .jsonPath("_embedded.freelancers[0].address.apartment")
        .isEqualTo(steveRogers().address().apartment().orElseThrow())
        .jsonPath("_embedded.freelancers[0].address.city").isEqualTo(steveRogers().address().city())
        .jsonPath("_embedded.freelancers[0].address.state").isEqualTo(steveRogers().address().state())
        .jsonPath("_embedded.freelancers[0].address.zipCode").isEqualTo(steveRogers().address().zipCode())
        .jsonPath("_embedded.freelancers[0].address.country").isEqualTo(steveRogers().address().country());
  }

  @Test
  void reportsNotFoundErrorWhenUpdatingAddressOfUnregisteredFreelancer() {
    this.fwsApplication.running()
        .when()
        .freelancer(unregistered()).movesTo(steveRogers().address())
        .then()
        .response()
        .expectStatus().isNotFound()
        .expectBody()
        .jsonPath("type").isEqualTo("/probs/error.html");
  }

  @Test
  void updatesFreelancerWhenAddingCommunicationChannel() {
    this.fwsApplication.runningWith()
        .freelancers(tonyStark())
        .when()
        .freelancer(tonyStark()).addsCommunicationChannel(mobile("901-234-8765"))
        .then()
        .freelancers()
        .expectBody()
        .jsonPath("_embedded.freelancers[0].communicationChannels.length()").value(is(greaterThan(1)))
        .jsonPath("_embedded.freelancers[0].communicationChannels.[*].value").value(hasItem("901-234-8765"))
        .jsonPath("_embedded.freelancers[0].communicationChannels.[*].type").value(hasItem("MOBILE"));
  }

  @Test
  void providesAffordancesToRemoveRegisteredCommunicationChannel() {
    this.fwsApplication.runningWith()
        .freelancers(tonyStark())
        .when()
        .freelancer(tonyStark()).addsCommunicationChannel(mobile("901-234-8765"))
        .then()
        .response()
        .expectBody()
        .jsonPath("_links.removeCommunicationChannel").value(hasSize(tonyStark().communicationChannels().size() + 1));
  }

  @Test
  void reportsNotFoundWhenAddingCommunicationChannelToUnregisteredFreelancer() {
    this.fwsApplication.runningWith()
        .when()
        .freelancer(unregistered()).addsCommunicationChannel(mobile("901-234-8765"))
        .then()
        .response()
        .expectStatus().isNotFound()
        .expectBody()
        .jsonPath("type").isEqualTo("/probs/error.html");
  }

  @Test
  void updatesFreelancerWhenRemovingCommunicationChannel() {
    final var tonyStark = tonyStark();
    final var removedChannelId = tonyStark.communicationChannels().iterator().next().id().orElseThrow();
    this.fwsApplication.runningWith()
        .freelancers(tonyStark)
        .when()
        .freelancer(tonyStark).removesCommunicationChannel(removedChannelId)
        .then()
        .freelancers()
        .expectBody()
        .jsonPath("_embedded.freelancers[0].communicationChannels[*].id").value(not(hasItem(removedChannelId)));
  }

  @Test
  void removesAffordancesWhenRemovingAllCommunicationChannels() {
    final var tonyStark = tonyStark();
    final var channels = tonyStark.communicationChannels().iterator();
    final var channel1 = channels.next().id().orElseThrow();
    final var channel2 = channels.next().id().orElseThrow();
    this.fwsApplication.runningWith()
        .freelancers(tonyStark)
        .when()
        .freelancer(tonyStark).removesCommunicationChannel(channel1)
        .freelancer(tonyStark).removesCommunicationChannel(channel2)
        .then()
        .response()
        .expectBody()
        .jsonPath("_links.removeCommunicationChannel").doesNotExist();
  }

  @Test
  void reportsNotFoundWhenRemovingCommunicationChannelFromUnregisteredFreelancer() {
    this.fwsApplication.runningWith()
        .when()
        .freelancer(unregistered()).removesCommunicationChannel(UUID.randomUUID().toString())
        .then()
        .response()
        .expectStatus().isNotFound()
        .expectBody()
        .jsonPath("type").isEqualTo("/probs/error.html");
  }

  @Test
  void reportsUnprocessableEntityWhenRemovingNonExistentCommunicationChannel() {
    this.fwsApplication.runningWith()
        .freelancers(tonyStark())
        .when()
        .freelancer(tonyStark()).removesCommunicationChannel(UUID.randomUUID().toString())
        .then()
        .response()
        .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
        .expectBody()
        .jsonPath("type").isEqualTo("/probs/error.html");
  }

  @Test
  void updatesFreelancerWhenUpdatingNicknames() {
    this.fwsApplication.runningWith()
        .freelancers(tonyStark())
        .when()
        .freelancer(tonyStark()).updatesNicknames("Ironman", "Tony", "Mr. Stark")
        .then()
        .freelancers()
        .expectBody()
        .jsonPath("_embedded.freelancers[0].nicknames").value(hasItems("Ironman", "Tony", "Mr. Stark"));
  }

  @Test
  void reportsNotFoundWhenUpdatingNicknamesOfUnregisteredFreelancer() {
    this.fwsApplication.running()
        .when()
        .freelancer(unregistered()).updatesNicknames("Ironman", "Tony")
        .then()
        .response()
        .expectStatus().isNotFound()
        .expectBody()
        .jsonPath("type").isEqualTo("/probs/error.html");
  }

  @Test
  void updatesFreelancerWhenUpdatingWage() {
    this.fwsApplication.runningWith()
        .freelancers(tonyStark())
        .when()
        .freelancer(tonyStark()).updatesWage(new HourlyWageDto(new BigDecimal("1000000"), "USD"))
        .then()
        .freelancers()
        .expectBody()
        .jsonPath("_embedded.freelancers[0].wage.amount").value(is(closeTo(1_000_000, 0)))
        .jsonPath("_embedded.freelancers[0].wage.currency").isEqualTo("USD");
  }

  @Test
  void reportsNotFoundWhenUpdatingWageOfUnregisteredFreelancer() {
    this.fwsApplication.running()
        .when()
        .freelancer(unregistered()).updatesWage(new HourlyWageDto(new BigDecimal("1000000"), "USD"))
        .then()
        .response()
        .expectStatus().isNotFound()
        .expectBody()
        .jsonPath("type").isEqualTo("/probs/error.html");
  }

  private static FreelancerDto tonyStark() {
    return new FreelancerDtoBuilder()
        .id("fa8508ed-8b7b-4be7-b372-ac1094c709b5")
        .firstName("Tony")
        .middleName("E")
        .lastName("Stark")
        .gender(Gender.MALE)
        .birthDate(LocalDate.parse("1970-05-29"))
        .address(
            new AddressDtoBuilder()
                .street("10880 Malibu Point")
                .city("Malibu")
                .state("CA")
                .zipCode("90265")
                .country("USA")
                .build())
        .wage(new HourlyWageDto(new BigDecimal("5000000"), "USD"))
        .addNickname("Iron Man")
        .addCommunicationChannels(email("tony@avengers.org"), email("ironman@avengers.org"))
        .build();
  }

  private static FreelancerDto steveRogers() {
    return new FreelancerDtoBuilder()
        .firstName("Steve")
        .lastName("Rogers")
        .gender(Gender.MALE)
        .birthDate(LocalDate.parse("1918-07-04"))
        .address(
            new AddressDtoBuilder()
                .street("569 Leaman Place")
                .apartment("Apt 2A")
                .city("Brooklyn Heights")
                .state("NY")
                .zipCode("11201")
                .country("USA")
                .build())
        .wage(new HourlyWageDto(new BigDecimal("500"), "USD"))
        .addNickname("Captain America")
        .addCommunicationChannel(email("cap@avengers.org"))
        .build();
  }

  private static FreelancerDto unregistered() {
    return new FreelancerDtoBuilder().id("UNREGISTERED").build();
  }

  private static FreelancerDto invalidFreelancer() {
    return new FreelancerDtoBuilder().build();
  }

  private static CommunicationChannelDto email(final String email) {
    return new CommunicationChannelDtoBuilder()
        .id(UUID.randomUUID().toString())
        .value(email)
        .type(CommunicationChannel.Type.EMAIL)
        .build();
  }

  private static CommunicationChannelDto mobile(final String number) {
    return new CommunicationChannelDtoBuilder()
        .value(number)
        .type(CommunicationChannel.Type.MOBILE)
        .build();
  }

}
