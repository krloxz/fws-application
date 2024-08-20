package io.github.krloxz.fws;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.web.ReactivePageableHandlerMethodArgumentResolver;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType;
import org.springframework.r2dbc.connection.TransactionAwareConnectionFactoryProxy;

import io.r2dbc.spi.ConnectionFactory;

@SpringBootApplication
@EnableHypermediaSupport(type = {HypermediaType.HAL})
public class FwsApplication {

  public static void main(final String[] args) {
    SpringApplication.run(FwsApplication.class, args);
  }

  @Bean
  DSLContext dslContext(final ConnectionFactory factory) {
    return DSL.using(new TransactionAwareConnectionFactoryProxy(factory), SQLDialect.H2);
  }

  @Bean
  ReactivePageableHandlerMethodArgumentResolver pageableResolver() {
    return new ReactivePageableHandlerMethodArgumentResolver();
  }

}
