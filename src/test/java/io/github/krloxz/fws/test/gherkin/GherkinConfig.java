package io.github.krloxz.fws.test.gherkin;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.krloxz.fws.test.gherkin.restapi.RestApi;

/**
 * Spring configuration class that configures beans required to enable the Gherkin DSL.
 *
 * @author Carlos Gomez
 * @see TestScenario
 */
@TestConfiguration
public class GherkinConfig {

  @Bean
  TestScenario testScenario(final ApplicationContext context) {
    return new TestScenario(context);
  }

  @Bean
  RestApi restApi(final MockMvc mockMvc, final ObjectMapper objectMapper) {
    return new RestApi(mockMvc, objectMapper);
  }

}
