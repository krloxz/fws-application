package io.github.krloxz.fws.core;

import java.util.Optional;

import org.immutables.value.Value;
import org.jmolecules.ddd.annotation.ValueObject;

/**
 * Value object representing a person's name.
 *
 * @author Carlos Gomez
 */
@ValueObject
@Value.Immutable
public interface PersonName {

  String first();

  String last();

  Optional<String> middle();

  static ImmutablePersonName.Builder builder() {
    return ImmutablePersonName.builder();
  }

}
