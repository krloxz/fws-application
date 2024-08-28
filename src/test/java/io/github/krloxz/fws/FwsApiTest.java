package io.github.krloxz.fws;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import io.github.krloxz.fws.test.FwsApplicationRestApi;
import io.github.krloxz.fws.test.FwsApplicationTest;

/**
 * Tests access to the Freelancer Web Services (FWS) API.
 *
 * @author Carlos Gomez
 */
@FwsApplicationTest
class FwsApiTest {

  @Autowired
  private FwsApplicationRestApi restApi;

  @Test
  void listResources() {
    this.restApi.get("/").andExpect(status().isOk());
  }

}
