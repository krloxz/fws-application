package io.github.krloxz.fws;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import io.github.krloxz.fws.freelancer.application.dtos.AddressDtoBuilder;
import io.github.krloxz.fws.freelancer.application.dtos.CommunicationChannelDto;
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
    return new CommunicationChannelDto(email, CommunicationChannel.Type.EMAIL);
  }

  private static CommunicationChannelDto mobile(final String number) {
    return new CommunicationChannelDto(number, CommunicationChannel.Type.MOBILE);
  }

}
