package io.github.krloxz.fws;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

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
        .extracting(FreelancerDto::firstName)
        .contains("Tony", "Steve");
  }

  @Test
  void failsToRegisterFreelancersWhenDataIsInvalid() {
    this.fwsApplication.running()
        .when()
        .freelancers(invalidFreelancer()).registered()
        .then()
        .response()
        .hasProblemThat()
        .hasStatus(HttpStatus.BAD_REQUEST)
        .hasValidationErrorsThat()
        .extracting("attribute")
        .contains("firstName", "lastName");
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
