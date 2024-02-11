package io.github.krloxz.fws;

import static org.hamcrest.CoreMatchers.hasItems;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.server.core.TypeReferences.EntityModelType;

import io.github.krloxz.fws.freelancer.application.FreelancerDto;
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
    return new FreelancerDto("Tony", "Stark");
  }

  private static FreelancerDto steveRogers() {
    return new FreelancerDto("Steve", "Rogers");
  }

  private static FreelancerDto invalidFreelancer() {
    return new FreelancerDto(null, null);
  }

}
