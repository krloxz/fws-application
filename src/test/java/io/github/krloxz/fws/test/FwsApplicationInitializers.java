package io.github.krloxz.fws.test;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.stream.Stream;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

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
  private FwsApplicationRestApi restApi;

  @Override
  public void setApplicationContext(final ApplicationContext context) {
    this.context = context;
    this.restApi = context.getBean(FwsApplicationRestApi.class);
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
   * @param dtos
   *        DTOs representing the freelancers to register
   * @return this instance to allow method chaining
   */
  public FwsApplicationInitializers freelancers(final FreelancerDto... dtos) {
    Stream.of(dtos).forEach(
        dto -> this.restApi.post("/freelancers", dto).andExpect(status().isCreated()));
    return this;
  }

}
