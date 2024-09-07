package io.github.krloxz.fws.project.domain;

import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.Identity;

/**
 * Represents a project.
 *
 * @author Carlos Gomez
 */
@AggregateRoot
public record Project(@Identity ProjectId id, String name, String description) {

}
