package io.github.krloxz.fws;

import static java.util.stream.Collectors.joining;

import org.jmolecules.archunit.JMoleculesArchitectureRules;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;

/**
 * Test methods to verify the application architecture.
 *
 * @author Carlos Gomez
 */
@Tag("architecture")
class ArchitectureTest {

  private final ApplicationModules modules = ApplicationModules.of(
      FwsApplication.class,
      JavaClass.Predicates.resideInAnyPackage(
          "io.github.krloxz.fws.test..",
          "io.github.krloxz.fws.mapstruct.."));

  @Test
  void followOnionArchitecture() {
    final var result = JMoleculesArchitectureRules.ensureOnionSimple().evaluate(allClasses());
    if (result.hasViolation()) {
      final var violations = result.getFailureReport().getDetails().stream().collect(joining("\n"));
      throw new AssertionError("Not following onion architecture, violations:\n" + violations);
    }
  }

  @Test
  void followModularDesign() {
    this.modules.verify();
  }

  private JavaClasses allClasses() {
    try {
      final var field = ApplicationModules.class.getDeclaredField("allClasses");
      field.setAccessible(true);
      return (JavaClasses) field.get(this.modules);
    } catch (final IllegalAccessException | NoSuchFieldException | SecurityException e) {
      throw new IllegalStateException("Unable to access allClasses field", e);
    }
  }

}
