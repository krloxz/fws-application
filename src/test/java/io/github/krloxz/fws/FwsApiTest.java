package io.github.krloxz.fws;

import static io.github.krloxz.fws.test.gherkin.TestScenario.given;
import static io.github.krloxz.fws.test.gherkin.actions.Actions.response;
import static io.github.krloxz.fws.test.gherkin.actions.Actions.systemReady;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;

import io.github.krloxz.fws.test.FwsApplicationTest;
import io.github.krloxz.fws.test.gherkin.restapi.RestApiAction;

/**
 * Tests access to the Freelancer Web Services (FWS) API.
 *
 * @author Carlos Gomez
 */
@FwsApplicationTest
class FwsApiTest {

  @Test
  void listResources() {
    given(systemReady())
        .when(rootUrlRequested())
        .then(response())
        .contains(status().isOk())
        .contains(jsonPath("_links.self.href").value("http://localhost/"))
        .contains(jsonPath("_links.freelancers.href").value("http://localhost/freelancers"))
        .contains(jsonPath("_links.projects.href").value("http://localhost/projects"));
  }

  private RestApiAction rootUrlRequested() {
    return restApi -> restApi.get("/");
  }

}
