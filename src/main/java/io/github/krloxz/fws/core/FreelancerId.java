package io.github.krloxz.fws.core;

import java.util.UUID;

import org.jmolecules.ddd.annotation.ValueObject;

/**
 * Value object representing a freelancer's unique identifier.
 *
 * @author Carlos Gomez
 */
@ValueObject
public record FreelancerId(UUID value) {

  public FreelancerId(final String value) {
    this(UUID.fromString(value));
  }

}
