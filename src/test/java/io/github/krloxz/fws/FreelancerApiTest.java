package io.github.krloxz.fws;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;

import io.github.krloxz.fws.freelancer.application.FreelancerDto;
import io.github.krloxz.fws.test.DatabaseCleaner;
import io.github.krloxz.fws.test.TestFwsApplication;

/**
 * Tests the Freelancers API.
 *
 * @author Carlos Gomez
 */
@SpringBootTest
@AutoConfigureWebTestClient
@ExtendWith(DatabaseCleaner.class)
public class FreelancerApiTest {

  @Autowired
  private TestFwsApplication fwsApplication;

  @Test
  void registersFreelancers() {
    this.fwsApplication.running()
        .when()
        .freelancers(tonyStark(), steveRogers()).registered()
        .then()
        .freelancers()
        .extracting(FreelancerDto::firstName)
        .contains("Tony", "Steve");
  }

  @Test
  void registersFreelancersAndAssertOnResponseBody() {
    this.fwsApplication.running()
        .when()
        .freelancers(steveRogers(), tonyStark()).registered()
        .then()
        .freelancersBody()
        .jsonPath("[0].firstName").isEqualTo("Steve")
        .jsonPath("[1].firstName").isEqualTo("Tony");
  }

  @Test
  void registersFreelancersAndAssertOnJsonResponse() {
    this.fwsApplication.running()
        .when()
        .freelancers(steveRogers(), tonyStark()).registered()
        .then()
        .freelancersJson(
            json -> json.node("[0].firstName").isEqualTo("Steve"),
            json -> json.node("[1].firstName").isEqualTo("Tony"));
  }

  private FreelancerDto tonyStark() {
    return new FreelancerDto("a", "Tony");
  }

  private FreelancerDto steveRogers() {
    return new FreelancerDto("b", "Steve");
  }

}
