package io.github.krloxz.fws.support;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import io.github.krloxz.fws.core.DomainEventSupplier;

/**
 * Convenient class to publish events supplied by {@link DomainEventPublisher}s.
 *
 * @author Carlos Gomez
 */
@Component
public class DomainEventPublisher {

  private final ApplicationEventPublisher delegate;

  /**
   * Creates a new instance of {@link DomainEventPublisher}.
   *
   * @param delegate
   *        Spring's event publisher which will perform the actual event publishing
   */
  public DomainEventPublisher(final ApplicationEventPublisher delegate) {
    this.delegate = delegate;
  }

  /**
   * Publishes the domain events supplied by the given {@link DomainEventSupplier}.
   *
   * @param supplier
   *        the supplier
   * @param <T>
   *        the actual type of the supplier
   * @return the original supplier to enable method chaining
   */
  public <T extends DomainEventSupplier> T publish(final T supplier) {
    supplier.domainEvents().forEach(this.delegate::publishEvent);
    return supplier;
  }

}
