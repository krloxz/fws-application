package io.github.krloxz.fws.test;

import java.util.List;

import org.apache.commons.lang3.stream.Streams;
import org.jmolecules.event.types.DomainEvent;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.modulith.test.PublishedEvents;

import io.github.krloxz.fws.test.gherkin.actions.Action;

/**
 * JUnit 5 extension that enables the {@link #publishedEvents() Published Events Action}.
 * <p>
 * WARNING: Won't work when {@link PublishedEvents} parameters are used in test methods.
 *
 * @author Carlos Gomez
 */
public class PublishedEventsActionExtension implements BeforeEachCallback {

  private static PublishedEvents publishedEvents;

  @Override
  public void beforeEach(final ExtensionContext context) throws Exception {
    try {
      context.getExecutableInvoker().invoke(
          PublishedEventsActionExtension.class.getDeclaredMethod("setPublishedEvents", PublishedEvents.class), this);
    } catch (final Exception e) {
      throw new IllegalStateException("Failed to start tracking PublishedEvents", e);
    }
  }

  void setPublishedEvents(final PublishedEvents publishedEvents) {
    PublishedEventsActionExtension.publishedEvents = publishedEvents;
  }

  /**
   * @return an {@link Action} that retrieves the {@link DomainEvent domain events} that were
   *         published during the test method execution
   */
  public static Action<Void, List<DomainEvent>> publishedEvents() {
    return new Action<>() {

      @Override
      public List<DomainEvent> perform(final Void input) {
        if (publishedEvents == null) {
          throw new IllegalStateException("The PublishedEventsAction has not been enabled");
        }
        return Streams.of(publishedEvents.ofType(DomainEvent.class).iterator()).toList();
      }

      @Override
      public Class<Void> inputType() {
        return Void.class;
      }

    };
  }

}
