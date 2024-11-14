package io.github.krloxz.fws;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType;
import org.springframework.modulith.Modulithic;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Main class that bootstraps the Freelancer Web Services application.
 *
 * @author Carlos Gomez
 */
@SpringBootApplication
@EnableHypermediaSupport(type = {HypermediaType.HAL})
@Modulithic(sharedModules = {"core", "support"})
@EnableAsync
public class FwsApplication {

  public static void main(final String[] args) {
    SpringApplication.run(FwsApplication.class, args);
  }

}
