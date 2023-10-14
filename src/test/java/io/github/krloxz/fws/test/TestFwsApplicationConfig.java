package io.github.krloxz.fws.test;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.reactive.server.WebTestClientBuilderCustomizer;
import org.springframework.context.annotation.Bean;

/**
 * Configures beans required for the {@link TestFwsApplication}.
 *
 * @author Carlos Gomez
 */
@TestConfiguration
class TestFwsApplicationConfig {

  @Bean
  WebTestClientBuilderCustomizer customizer() {
    return builder -> {
      builder.filter(new AfterExchangeFilter(new ExchangeLogger()));
    };
  }

}
