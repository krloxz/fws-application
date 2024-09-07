package io.github.krloxz.fws;

import org.jmolecules.archunit.JMoleculesArchitectureRules;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

/**
 * Test methods to verify the application architecture and generate module documentation.
 *
 * @author Carlos Gomez
 */
@AnalyzeClasses(packagesOf = FwsApplication.class)
class ArchitectureTest {

  private final ApplicationModules modules = ApplicationModules.of(
      FwsApplication.class,
      JavaClass.Predicates.resideInAnyPackage(
          "io.github.krloxz.fws.test..",
          "io.github.krloxz.fws.mapstruct.."));

  @ArchTest
  private final ArchRule followOnionArchitecture = JMoleculesArchitectureRules.ensureOnionSimple();

  @Test
  void followModularDesign() {
    this.modules.verify();
  }

  @Test
  void generateDocumentation() {
    new Documenter(this.modules).writeDocumentation();
  }

}
