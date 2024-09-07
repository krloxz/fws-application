package io.github.krloxz.fws.project.domain;

import java.util.UUID;

import org.jmolecules.ddd.annotation.ValueObject;

/**
 * Represents a project id.
 *
 * @author Carlos Gomez
 */
@ValueObject
public record ProjectId(UUID value) {

  public ProjectId() {
    this(UUID.randomUUID());
  }

}