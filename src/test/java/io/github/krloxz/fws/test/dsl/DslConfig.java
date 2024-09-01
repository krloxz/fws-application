package io.github.krloxz.fws.test.dsl;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Configures beans required for
 *
 * @author Carlos Gomez
 */
@TestConfiguration
public class DslConfig {

  @Bean
  TestComponent testComponent(final ApplicationContext context) {
    return new TestComponent(context);
  }

  @Bean
  RestApi restApi(final MockMvc mockMvc, final ObjectMapper objectMapper) {
    return new RestApi(mockMvc, objectMapper);
  }

}
