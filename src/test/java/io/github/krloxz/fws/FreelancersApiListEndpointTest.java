package io.github.krloxz.fws;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
        .result()
        .contains(status().isOk())
        .contains(jsonPath("_embedded.freelancers.length()").value(3))
        .contains(jsonPath("_embedded.freelancers[0].firstName").value("Steve"))
        .contains(jsonPath("_embedded.freelancers[1].firstName").value("Natasha"))
        .contains(jsonPath("_embedded.freelancers[2].firstName").value("Tony"))
        .contains(jsonPath("_links.register.href").isNotEmpty())
        .contains(jsonPath("_links.self.href").value("http://localhost/freelancers?page=1&size=3"))
        .contains(jsonPath("_links.first.href").value("http://localhost/freelancers?page=0&size=3"))
        .contains(jsonPath("_links.prev.href").value("http://localhost/freelancers?page=0&size=3"))
        .contains(jsonPath("_links.next.href").value("http://localhost/freelancers?page=2&size=3"))
        .contains(jsonPath("_links.last.href").value("http://localhost/freelancers?page=2&size=3"))
        .contains(jsonPath("page.size").value(3))
        .contains(jsonPath("page.totalElements").value(7))
        .contains(jsonPath("page.totalPages").value(3))
        .contains(jsonPath("page.number").value(1));
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
        .result()
        .contains(status().isOk())
        .contains(jsonPath("_embedded.freelancers.length()").value(3))
        .contains(jsonPath("_embedded.freelancers[0].firstName").value("Bruce"))
        .contains(jsonPath("_embedded.freelancers[1].firstName").value("Clint"))
        .contains(jsonPath("_embedded.freelancers[2].firstName").value("Thor"))
        .contains(jsonPath("_links.register.href").isNotEmpty())
        .contains(jsonPath("_links.self.href").value("http://localhost/freelancers?page=0&size=3"))
        .contains(jsonPath("_links.first.href").value("http://localhost/freelancers?page=0&size=3"))
        .contains(jsonPath("_links.prev.href").doesNotExist())
        .contains(jsonPath("_links.next.href").value("http://localhost/freelancers?page=1&size=3"))
        .contains(jsonPath("_links.last.href").value("http://localhost/freelancers?page=2&size=3"))
        .contains(jsonPath("page.size").value(3))
        .contains(jsonPath("page.totalElements").value(7))
        .contains(jsonPath("page.totalPages").value(3))
        .contains(jsonPath("page.number").value(0));
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
        .result()
        .contains(status().isOk())
        .contains(jsonPath("_embedded.freelancers.length()").value(1))
        .contains(jsonPath("_embedded.freelancers[0].firstName").value("Stephen"))
        .contains(jsonPath("_links.register.href").isNotEmpty())
        .contains(jsonPath("_links.self.href").value("http://localhost/freelancers?page=2&size=3"))
        .contains(jsonPath("_links.first.href").value("http://localhost/freelancers?page=0&size=3"))
        .contains(jsonPath("_links.prev.href").value("http://localhost/freelancers?page=1&size=3"))
        .contains(jsonPath("_links.next.href").doesNotExist())
        .contains(jsonPath("_links.last.href").value("http://localhost/freelancers?page=2&size=3"))
        .contains(jsonPath("page.size").value(3))
        .contains(jsonPath("page.totalElements").value(7))
        .contains(jsonPath("page.totalPages").value(3))
        .contains(jsonPath("page.number").value(2));
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
        .result()
        .contains(status().isOk())
        .contains(jsonPath("_embedded.freelancers.length()").value(2))
        .contains(jsonPath("_embedded.freelancers[0].firstName").value("Bruce"))
        .contains(jsonPath("_embedded.freelancers[1].firstName").value("Clint"))
        .contains(jsonPath("_links.register.href").isNotEmpty())
        .contains(jsonPath("_links.self.href").value("http://localhost/freelancers?page=0&size=3"))
        .contains(jsonPath("_links.first.href").doesNotExist())
        .contains(jsonPath("_links.prev.href").doesNotExist())
        .contains(jsonPath("_links.next.href").doesNotExist())
        .contains(jsonPath("_links.last.href").doesNotExist())
        .contains(jsonPath("page.size").value(3))
        .contains(jsonPath("page.totalElements").value(2))
        .contains(jsonPath("page.totalPages").value(1))
        .contains(jsonPath("page.number").value(0));
  }

  @Test
  void listsEmptyPage() {
    this.fwsApplication.runningWith()
        .freelancers()
        .when()
        .freelancers().listed(0, 3)
        .then()
        .result()
        .contains(status().isOk())
        .contains(jsonPath("_embedded.freelancers").doesNotExist())
        .contains(jsonPath("_links.register.href").isNotEmpty())
        .contains(jsonPath("_links.self.href").value("http://localhost/freelancers?page=0&size=3"))
        .contains(jsonPath("_links.first.href").doesNotExist())
        .contains(jsonPath("_links.prev.href").doesNotExist())
        .contains(jsonPath("_links.next.href").doesNotExist())
        .contains(jsonPath("_links.last.href").doesNotExist())
        .contains(jsonPath("page.size").value(3))
        .contains(jsonPath("page.totalElements").value(0))
        .contains(jsonPath("page.totalPages").value(0))
        .contains(jsonPath("page.number").value(0));
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
        .result()
        .contains(status().isOk())
        .contains(jsonPath("_embedded.freelancers.length()").value(defaultPageSize))
        .contains(jsonPath("_links.register.href").isNotEmpty())
        .contains(jsonPath("_links.self.href").value("http://localhost/freelancers?page=0&size=" + defaultPageSize))
        .contains(jsonPath("_links.first.href").value("http://localhost/freelancers?page=0&size=" + defaultPageSize))
        .contains(jsonPath("_links.prev.href").doesNotExist())
        .contains(jsonPath("_links.next.href").value("http://localhost/freelancers?page=1&size=" + defaultPageSize))
        .contains(jsonPath("_links.last.href").value("http://localhost/freelancers?page=1&size=" + defaultPageSize))
        .contains(jsonPath("page.size").value(defaultPageSize))
        .contains(jsonPath("page.totalElements").value(7))
        .contains(jsonPath("page.totalPages").value(2))
        .contains(jsonPath("page.number").value(0));
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
