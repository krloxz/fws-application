package io.github.krloxz.fws.test;

import java.time.Duration;
import java.util.List;

import org.awaitility.Awaitility;
import org.jmolecules.event.types.DomainEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.modulith.events.CompletedEventPublications;
import org.springframework.modulith.events.EventPublication;

import io.github.krloxz.fws.support.DomainEventPublisher;
import io.github.krloxz.fws.test.gherkin.actions.Action;

/**
 * A generic {@link Action} interface designed for declaring actions that trigger the publication of
 * a {@link DomainEvent} and wait until the publication is completed.
 *
 * @author Carlos Gomez
 */
@FunctionalInterface
public interface DomainEventAction extends Action<ApplicationContext, Void> {

  /**
   * @return the {@link DomainEvent} to be published and waited for
   */
  DomainEvent event();

  @Override
  default Void perform(final ApplicationContext context) {
    publishEvent(context);
    Awaitility.await("eventPublicationIsCompleted")
        .timeout(Duration.ofSeconds(1))
        .until(() -> eventPublicationIsCompleted(context));
    return null;
  }

  @Override
  default Class<ApplicationContext> inputType() {
    return ApplicationContext.class;
  }

  private void publishEvent(final ApplicationContext context) {
    context.getBean(DomainEventPublisher.class)
        .publish(() -> List.of(event()));
  }

  private boolean eventPublicationIsCompleted(final ApplicationContext context) {
    return context.getBean(CompletedEventPublications.class)
        .findAll()
        .stream()
        .map(EventPublication::getEvent)
        .anyMatch(event()::equals);
  }

}
