@ApplicationRing
@NamedInterface("application")
@Value.Style(validationMethod = ValidationMethod.NONE, depluralize = true)
package io.github.krloxz.fws.project.application;

import org.immutables.value.Value;
import org.immutables.value.Value.Style.ValidationMethod;
import org.jmolecules.architecture.onion.simplified.ApplicationRing;
import org.springframework.modulith.NamedInterface;
