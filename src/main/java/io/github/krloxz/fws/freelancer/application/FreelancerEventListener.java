package io.github.krloxz.fws.freelancer.application;

import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

import io.github.krloxz.fws.freelancer.domain.FreelancerRepository;
import io.github.krloxz.fws.project.domain.FreelancerJoinedProject;
import io.github.krloxz.fws.support.DomainEventPublisher;

/**
 * Event listener for events related to freelancers.
 *
 * @author Carlos Gomez
 */
@Component
public class FreelancerEventListener {

  private final FreelancerRepository repository;
  private final DomainEventPublisher eventPublisher;

  FreelancerEventListener(final FreelancerRepository repository, final DomainEventPublisher eventPublisher) {
    this.repository = repository;
    this.eventPublisher = eventPublisher;
  }

  /**
   * Updates the freelancer's weekly availability when the freelancer joins a project.
   *
   * @param event
   *        the details of how the freelancer joined the project
   */
  @ApplicationModuleListener
  public void on(final FreelancerJoinedProject event) {
    this.repository.findById(event.freelancerId().value())
        .map(freelancer -> freelancer.reduceWeeklyAvailability(event.allocatedHours(), event.projectId()))
        .map(this.repository::update)
        .map(this.eventPublisher::publish);
  }

}
