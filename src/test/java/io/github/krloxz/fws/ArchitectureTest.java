package io.github.krloxz.fws;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

import com.tngtech.archunit.core.domain.JavaClass;

/**
 * Test methods to verify the application architecture and generate module documentation.
 *
 * @author Carlos Gomez
 */
class ArchitectureTest {

  private final ApplicationModules modules = ApplicationModules.of(
      FwsApplication.class,
      JavaClass.Predicates.resideInAnyPackage(
          "io.github.krloxz.fws.test..",
          "io.github.krloxz.fws.mapstruct.."));

  @Test
  void verifyArchitecture() {
    this.modules.forEach(System.out::println);
    this.modules.verify();
  }

  @Test
  void generateDocumentation() {
    new Documenter(this.modules).writeDocumentation();
  }
}
