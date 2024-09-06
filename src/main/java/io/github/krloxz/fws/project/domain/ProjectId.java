package io.github.krloxz.fws.project.domain;

import java.util.UUID;

/**
 * Represents a project id.
 *
 * @author Carlos Gomez
 */
public record ProjectId(UUID value) {

  public ProjectId() {
    this(UUID.randomUUID());
  }

}
