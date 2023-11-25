package io.github.krloxz.fws.test;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.reactive.server.WebTestClientBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.hateoas.config.HypermediaWebTestClientConfigurer;

/**
 * Configures beans required for the {@link TestFwsApplication}.
 *
 * @author Carlos Gomez
 */
@TestConfiguration
class TestFwsApplicationConfig {

  @Bean
  WebTestClientBuilderCustomizer customizer(final HypermediaWebTestClientConfigurer hypermediaConfigurer) {
    return builder -> {
      builder.apply(hypermediaConfigurer);
      builder.filter(new AfterExchangeFilter(new ExchangeLogger()))
          .filter(new AfterExchangeFilter(new OpenApiValidator()));
    };
  }

}
