package io.github.krloxz.fws;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType;

/**
 * Main class that bootstraps the Freelancer Web Services application.
 *
 * @author Carlos Gomez
 */
@SpringBootApplication
@EnableHypermediaSupport(type = {HypermediaType.HAL})
public class FwsApplication {

  public static void main(final String[] args) {
    SpringApplication.run(FwsApplication.class, args);
  }

}
