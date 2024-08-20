package io.github.krloxz.fws;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

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
 * Tests the list endpoint of the Freelancers API.
 *
 * @author Carlos Gomez
 */
@FwsApplicationTest
class FreelancersApiListEndpointTest {

  @Autowired
  private TestFwsApplication fwsApplication;

  @Test
  void listsRandomPage() {
    this.fwsApplication.runningWith()
        .freelancers(
            freelancer("Bruce", "Banner"),
            freelancer("Clint", "Barton"),
            freelancer("Thor", "Odinson"),
            freelancer("Steve", "Rogers"),
            freelancer("Natasha", "Romanoff"),
            freelancer("Tony", "Stark"),
            freelancer("Stephen", "Strange"))
        .when()
        .freelancers().listed(1, 3)
        .then()
        .response()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("_embedded.freelancers.length()").isEqualTo(3)
        .jsonPath("_embedded.freelancers[0].firstName").isEqualTo("Steve")
        .jsonPath("_embedded.freelancers[1].firstName").isEqualTo("Natasha")
        .jsonPath("_embedded.freelancers[2].firstName").isEqualTo("Tony")
        .jsonPath("_links.self.href").isEqualTo("freelancers?page=1&size=3")
        .jsonPath("_links.register.href").isNotEmpty()
        .jsonPath("_links.first.href").isEqualTo("freelancers?page=0&size=3")
        .jsonPath("_links.prev.href").isEqualTo("freelancers?page=0&size=3")
        .jsonPath("_links.next.href").isEqualTo("freelancers?page=2&size=3")
        .jsonPath("_links.last.href").isEqualTo("freelancers?page=2&size=3")
        .jsonPath("page.size").isEqualTo(3)
        .jsonPath("page.totalElements").isEqualTo(7)
        .jsonPath("page.totalPages").isEqualTo(3)
        .jsonPath("page.number").isEqualTo(1);
  }

  @Test
  void listsFirstPage() {
    this.fwsApplication.runningWith()
        .freelancers(
            freelancer("Bruce", "Banner"),
            freelancer("Clint", "Barton"),
            freelancer("Thor", "Odinson"),
            freelancer("Steve", "Rogers"),
            freelancer("Natasha", "Romanoff"),
            freelancer("Tony", "Stark"),
            freelancer("Stephen", "Strange"))
        .when()
        .freelancers().listed(0, 3)
        .then()
        .response()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("_embedded.freelancers.length()").isEqualTo(3)
        .jsonPath("_embedded.freelancers[0].firstName").isEqualTo("Bruce")
        .jsonPath("_embedded.freelancers[1].firstName").isEqualTo("Clint")
        .jsonPath("_embedded.freelancers[2].firstName").isEqualTo("Thor")
        .jsonPath("_links.self.href").isEqualTo("freelancers?page=0&size=3")
        .jsonPath("_links.register.href").isNotEmpty()
        .jsonPath("_links.first.href").doesNotExist()
        .jsonPath("_links.prev.href").doesNotExist()
        .jsonPath("_links.next.href").isEqualTo("freelancers?page=1&size=3")
        .jsonPath("_links.last.href").isEqualTo("freelancers?page=2&size=3")
        .jsonPath("page.size").isEqualTo(3)
        .jsonPath("page.totalElements").isEqualTo(7)
        .jsonPath("page.totalPages").isEqualTo(3)
        .jsonPath("page.number").isEqualTo(0);
  }

  @Test
  void listsLastPage() {
    this.fwsApplication.runningWith()
        .freelancers(
            freelancer("Bruce", "Banner"),
            freelancer("Clint", "Barton"),
            freelancer("Thor", "Odinson"),
            freelancer("Steve", "Rogers"),
            freelancer("Natasha", "Romanoff"),
            freelancer("Tony", "Stark"),
            freelancer("Stephen", "Strange"))
        .when()
        .freelancers().listed(2, 3)
        .then()
        .response()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("_embedded.freelancers.length()").isEqualTo(1)
        .jsonPath("_embedded.freelancers[0].firstName").isEqualTo("Stephen")
        .jsonPath("_links.self.href").isEqualTo("freelancers?page=2&size=3")
        .jsonPath("_links.register.href").isNotEmpty()
        .jsonPath("_links.first.href").isEqualTo("freelancers?page=0&size=3")
        .jsonPath("_links.prev.href").isEqualTo("freelancers?page=1&size=3")
        .jsonPath("_links.next.href").doesNotExist()
        .jsonPath("_links.last.href").doesNotExist()
        .jsonPath("page.size").isEqualTo(3)
        .jsonPath("page.totalElements").isEqualTo(7)
        .jsonPath("page.totalPages").isEqualTo(3)
        .jsonPath("page.number").isEqualTo(2);
  }

  @Test
  void listsSinglePage() {
    this.fwsApplication.runningWith()
        .freelancers(
            freelancer("Bruce", "Banner"),
            freelancer("Clint", "Barton"))
        .when()
        .freelancers().listed(0, 3)
        .then()
        .response()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("_embedded.freelancers.length()").isEqualTo(2)
        .jsonPath("_embedded.freelancers[0].firstName").isEqualTo("Bruce")
        .jsonPath("_embedded.freelancers[1].firstName").isEqualTo("Clint")
        .jsonPath("_links.self.href").isEqualTo("freelancers?page=0&size=3")
        .jsonPath("_links.register.href").isNotEmpty()
        .jsonPath("_links.first.href").doesNotExist()
        .jsonPath("_links.prev.href").doesNotExist()
        .jsonPath("_links.next.href").doesNotExist()
        .jsonPath("_links.last.href").doesNotExist()
        .jsonPath("page.size").isEqualTo(3)
        .jsonPath("page.totalElements").isEqualTo(2)
        .jsonPath("page.totalPages").isEqualTo(1)
        .jsonPath("page.number").isEqualTo(0);
  }

  @Test
  void listsEmptyPage() {
    this.fwsApplication.runningWith()
        .freelancers()
        .when()
        .freelancers().listed(0, 3)
        .then()
        .response()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("_embedded.freelancers").doesNotExist()
        .jsonPath("_links.self.href").isEqualTo("freelancers?page=0&size=3")
        .jsonPath("_links.register.href").isNotEmpty()
        .jsonPath("_links.first.href").doesNotExist()
        .jsonPath("_links.prev.href").doesNotExist()
        .jsonPath("_links.next.href").doesNotExist()
        .jsonPath("_links.last.href").doesNotExist()
        .jsonPath("page.size").isEqualTo(3)
        .jsonPath("page.totalElements").isEqualTo(0)
        .jsonPath("page.totalPages").isEqualTo(0)
        .jsonPath("page.number").isEqualTo(0);
  }

  @Test
  void listsDefaultPageWithDefaultSize() {
    final var defaultPageSize = 5;
    this.fwsApplication.runningWith()
        .freelancers(
            freelancer("Bruce", "Banner"),
            freelancer("Clint", "Barton"),
            freelancer("Thor", "Odinson"),
            freelancer("Steve", "Rogers"),
            freelancer("Natasha", "Romanoff"),
            freelancer("Tony", "Stark"),
            freelancer("Stephen", "Strange"))
        .when()
        .freelancers().listed()
        .then()
        .response()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("_embedded.freelancers.length()").isEqualTo(defaultPageSize)
        .jsonPath("_links.self.href").isEqualTo("freelancers?page=0&size=5")
        .jsonPath("_links.register.href").isNotEmpty()
        .jsonPath("_links.first.href").doesNotExist()
        .jsonPath("_links.prev.href").doesNotExist()
        .jsonPath("_links.next.href").isEqualTo("freelancers?page=1&size=5")
        .jsonPath("_links.last.href").isEqualTo("freelancers?page=1&size=5")
        .jsonPath("page.size").isEqualTo(defaultPageSize)
        .jsonPath("page.totalElements").isEqualTo(7)
        .jsonPath("page.totalPages").isEqualTo(2)
        .jsonPath("page.number").isEqualTo(0);
  }

  // TODO: Add tests for the following scenarios:
  // sorting? -
  // https://docs.spring.io/spring-data/rest/reference/paging-and-sorting.html#paging-and-sorting.sorting
  // failsToListWithInvalidPageSize
  // failsToListInvalidPage

  private static FreelancerDto freelancer(final String firstName, final String lastName) {
    return new FreelancerDtoBuilder()
        .firstName(firstName)
        .lastName(lastName)
        .gender(Gender.MALE)
        .birthDate(LocalDate.parse("1970-01-01"))
        .address(
            new AddressDtoBuilder()
                .street("123 Main St")
                .apartment("Apt 1A")
                .city("Anytown")
                .state("NY")
                .zipCode("12345")
                .country("USA")
                .build())
        .wage(new HourlyWageDto(new BigDecimal("30"), "USD"))
        .addNickname(firstName + " Nickname")
        .addCommunicationChannel(email(firstName + "@email.com"))
        .build();
  }

  private static CommunicationChannelDto email(final String email) {
    return new CommunicationChannelDtoBuilder()
        .id(UUID.randomUUID().toString())
        .value(email)
        .type(CommunicationChannel.Type.EMAIL)
        .build();
  }

}
