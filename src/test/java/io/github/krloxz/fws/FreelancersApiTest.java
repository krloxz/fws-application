package io.github.krloxz.fws;

import static org.hamcrest.CoreMatchers.hasItems;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.server.core.TypeReferences.EntityModelType;

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
public class FreelancersApiTest {

  @Autowired
  private TestFwsApplication fwsApplication;

  @Test
  void registersFreelancers() {
    this.fwsApplication.running()
        .when()
        .freelancers(tonyStark(), steveRogers()).registered()
        .then()
        .freelancers()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("_embedded.freelancers[*].firstName").value(hasItems("Tony", "Steve"));
  }

  @Test
  void reportsValidationErrorWhenRegisteringFreelancersWithInvalidData() {
    this.fwsApplication.running()
        .when()
        .freelancers(invalidFreelancer()).registered()
        .then()
        .response()
        .expectStatus().isBadRequest()
        .expectBody()
        .jsonPath("type").isEqualTo("/probs/validation-error.html")
        .jsonPath("errors").isArray()
        .jsonPath("errors").isNotEmpty();
  }

  @Test
  void retrievesRegisteredFreelancers() {
    final var selfLink = this.fwsApplication.running()
        .when()
        .freelancers(tonyStark()).registered()
        .then()
        .response()
        .expectStatus().isCreated()
        .expectBody(new EntityModelType<FreelancerDto>() {})
        .returnResult()
        .getResponseBody()
        .getRequiredLink(IanaLinkRelations.SELF)
        .getHref()
        .split("/")[1];

    this.fwsApplication.running()
        .when()
        .freelancers().retrieved(selfLink)
        .then()
        .response()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("firstName").isEqualTo("Tony");
  }

  @Test
  void reportsNotFoundErrorWhenRetrievingUnregisteredFreelancers() {
    this.fwsApplication.running()
        .when()
        .freelancers().retrieved("UNREGISTERED")
        .then()
        .response()
        .expectStatus().isNotFound()
        .expectBody()
        .jsonPath("type").isEqualTo("/probs/error.html");
  }

  private static FreelancerDto tonyStark() {
    return new FreelancerDtoBuilder()
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
        .addCommunicationChannel(
            new CommunicationChannelDto("ironman@avengers.org", CommunicationChannel.Type.EMAIL))
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
                .city("Brooklyn Heights")
                .state("NY")
                .zipCode("11201")
                .country("USA")
                .build())
        .wage(new HourlyWageDto(new BigDecimal("500"), "USD"))
        .addNickname("Captain America")
        .addCommunicationChannel(
            new CommunicationChannelDto("cap@avengers.org", CommunicationChannel.Type.EMAIL))
        .build();
  }

  private static FreelancerDto invalidFreelancer() {
    return new FreelancerDtoBuilder().build();
  }

}
