package io.github.krloxz.fws.freelancer;

import static io.github.krloxz.fws.freelancer.FreelancerActions.freelancer;
import static io.github.krloxz.fws.freelancer.FreelancerActions.freelancers;
import static io.github.krloxz.fws.test.gherkin.TestScenario.given;
import static io.github.krloxz.fws.test.gherkin.actions.Actions.response;
import static io.github.krloxz.fws.test.gherkin.actions.Actions.systemReady;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;

import io.github.krloxz.fws.test.FwsApplicationTest;

/**
 * Tests the list endpoint of the Freelancers API.
 *
 * @author Carlos Gomez
 */
@FwsApplicationTest
class FreelancersApiListEndpointTest {

  @Test
  void listsRandomPage() {
    given(freelancer("Bruce", "Banner").registered())
        .and(freelancer("Clint", "Barton").registered())
        .and(freelancer("Thor", "Odinson").registered())
        .and(freelancer("Steve", "Rogers").registered())
        .and(freelancer("Natasha", "Romanoff").registered())
        .and(freelancer("Tony", "Stark").registered())
        .and(freelancer("Stephen", "Strange").registered())
        .when(freelancers().listed(1, 3))
        .then(response())
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
    given(freelancer("Bruce", "Banner").registered())
        .and(freelancer("Clint", "Barton").registered())
        .and(freelancer("Thor", "Odinson").registered())
        .and(freelancer("Steve", "Rogers").registered())
        .and(freelancer("Natasha", "Romanoff").registered())
        .and(freelancer("Tony", "Stark").registered())
        .and(freelancer("Stephen", "Strange").registered())
        .when(freelancers().listed(0, 3))
        .then(response())
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
    given(freelancer("Bruce", "Banner").registered())
        .and(freelancer("Clint", "Barton").registered())
        .and(freelancer("Thor", "Odinson").registered())
        .and(freelancer("Steve", "Rogers").registered())
        .and(freelancer("Natasha", "Romanoff").registered())
        .and(freelancer("Tony", "Stark").registered())
        .and(freelancer("Stephen", "Strange").registered())
        .when(freelancers().listed(2, 3))
        .then(response())
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
    given(freelancer("Bruce", "Banner").registered())
        .and(freelancer("Clint", "Barton").registered())
        .when(freelancers().listed(0, 3))
        .then(response())
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
    given(systemReady())
        .when(freelancers().listed(0, 3))
        .then(response())
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
    given(freelancer("Bruce", "Banner").registered())
        .and(freelancer("Clint", "Barton").registered())
        .and(freelancer("Thor", "Odinson").registered())
        .and(freelancer("Steve", "Rogers").registered())
        .and(freelancer("Natasha", "Romanoff").registered())
        .and(freelancer("Tony", "Stark").registered())
        .and(freelancer("Stephen", "Strange").registered())
        .when(freelancers().listed())
        .then(response())
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

}
