package io.github.krloxz.fws;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.test.web.reactive.server.WebTestClient;

import io.github.krloxz.fws.test.FwsApplicationTest;

/**
 * Tests access to the Freelancer Web Services (FWS) API.
 *
 * @author Carlos Gomez
 */
@FwsApplicationTest
class FwsApiTest {

  @Autowired
  private WebTestClient webClient;

  @Test
  void listResources() {
    this.webClient.get()
        .uri("/")
        .accept(MediaTypes.HAL_JSON)
        .exchange()
        .expectStatus().isOk()
        .expectBody();
  }

}
