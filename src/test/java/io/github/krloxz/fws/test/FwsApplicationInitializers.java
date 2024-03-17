package io.github.krloxz.fws.test;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.hateoas.MediaTypes;
import org.springframework.stereotype.Component;
import org.springframework.test.web.reactive.server.WebTestClient;

import io.github.krloxz.fws.freelancer.application.dtos.FreelancerDto;

/**
 * Entry point to the test initializers: objects that provide methods to set up the state of the
 * FwsApplication before a test case is executed.
 * <p>
 * Initializers implement the GIVEN clause of the Gherkin DSL.
 *
 * @author Carlos Gomez
 */
@Component
public class FwsApplicationInitializers implements ApplicationContextAware {

  private ApplicationContext context;

  @Override
  public void setApplicationContext(final ApplicationContext context) {
    this.context = context;
  }

  /**
   * @return the {@link FwsApplicationActions}
   */
  public FwsApplicationActions when() {
    return new FwsApplicationActions(this.context);
  }

  /**
   * Initializes the state of the FwsApplication by registering freelancers.
   *
   * @param dto
   *        the DTO representing the freelancer to register
   * @return this instance to allow method chaining
   */
  public FwsApplicationInitializers freelancers(final FreelancerDto dto) {
    this.context.getBean(WebTestClient.class).post()
        .uri("/freelancers")
        .accept(MediaTypes.HAL_JSON)
        .bodyValue(dto)
        .exchange()
        .expectBody(Void.class);
    return this;
  }

}
