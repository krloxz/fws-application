package io.github.krloxz.fws.project.application;

import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

import io.github.krloxz.fws.project.domain.FreelancerProjectCommitmentFailed;
import io.github.krloxz.fws.project.domain.ProjectRepository;

/**
 * Event listener for events related to projects.
 *
 * @author Carlos Gomez
 */
@Component
public class ProjectEventListener {

  private final ProjectRepository repository;

  ProjectEventListener(final ProjectRepository repository) {
    this.repository = repository;
  }

  /**
   * Removes the freelancer from the project when the freelancer's commitment to the project fails.
   *
   * @param event
   *        the details of how the freelancer's commitment to the project failed
   */
  @ApplicationModuleListener
  public void on(final FreelancerProjectCommitmentFailed event) {
    this.repository.findById(event.projectId())
        .map(project -> project.remove(event.freelancerId()))
        .map(this.repository::update);
  }

}
