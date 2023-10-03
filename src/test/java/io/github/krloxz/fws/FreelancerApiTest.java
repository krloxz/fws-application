package io.github.krloxz.fws;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import io.github.krloxz.fws.freelancer.application.FreelancerDto;
import io.github.krloxz.fws.test.FwsApplicationTest;
import io.github.krloxz.fws.test.TestFwsApplication;

/**
 * Tests the Freelancers API.
 *
 * @author Carlos Gomez
 */
@FwsApplicationTest
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

  private FreelancerDto tonyStark() {
    return new FreelancerDto("a", "Tony");
  }

  private FreelancerDto steveRogers() {
    return new FreelancerDto("b", "Steve");
  }

}
