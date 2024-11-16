package io.github.krloxz.fws.project.domain;

import org.jmolecules.event.types.DomainEvent;

import io.github.krloxz.fws.core.FreelancerId;
import io.github.krloxz.fws.core.ProjectId;

/**
 * A domain event signaling that a freelancer has joined a project.
 *
 * @author Carlos Gomez
 */
public record FreelancerJoinedProject(ProjectId projectId, FreelancerId freelancerId, int allocatedHours)
    implements DomainEvent {

}
