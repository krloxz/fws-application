package io.github.krloxz.fws.project.domain;

import org.jmolecules.event.types.DomainEvent;

/**
 * A {@link DomainEvent} published when a freelancer's commitment to a project fails.
 *
 * @author Carlos Gomez
 */
public record FreelancerProjectCommitmentFailed(
    FreelancerId freelancerId,
    ProjectId projectId,
    int committedHours,
    int availableHours) implements DomainEvent {

}
