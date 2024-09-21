package io.github.krloxz.fws.project.domain;

import org.jmolecules.event.types.DomainEvent;

import io.github.krloxz.fws.core.FreelancerId;

/**
 * An event that signals that a freelancer has been assigned to a project.
 *
 * @author Carlos Gomez
 */
public record FreelancerAssigned(ProjectId projectId, FreelancerId freelancerId, int allocatedHours)
    implements DomainEvent {

}
