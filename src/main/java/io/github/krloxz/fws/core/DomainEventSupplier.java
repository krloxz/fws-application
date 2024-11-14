package io.github.krloxz.fws.core;

import java.util.List;

import org.jmolecules.event.types.DomainEvent;

/**
 * Interface to be implemented by classes, like aggregate roots, that need to supply domain events.
 *
 * @author Carlos Gomez
 */
@FunctionalInterface
public interface DomainEventSupplier {

  /**
   * @return the domain events
   */
  List<DomainEvent> domainEvents();

}
