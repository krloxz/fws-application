package io.github.krloxz.fws.project.domain;

import org.jmolecules.ddd.annotation.Entity;
import org.jmolecules.ddd.annotation.Identity;

import io.github.krloxz.fws.core.FreelancerId;
import io.github.krloxz.fws.core.PersonName;

/**
 * A freelancer who is currently assigned or will be allocated to a project.
 *
 * @author Carlos Gomez
 */
@Entity
public record Freelancer(@Identity FreelancerId id, PersonName name, int allocatedHours) {

  /**
   * Creates a new freelancer that is not allocated.
   *
   * @param id
   *        the freelancer's identifier
   * @param name
   *        the freelancer's name
   */
  public Freelancer(final FreelancerId id, final PersonName name) {
    this(id, name, 0);
  }

  /**
   * Allocates this freelancer to a project for a given number of hours.
   *
   * @param hours
   *        the number of hours to allocate
   * @return a new instance of this freelancer with the allocated hours
   */
  Freelancer allocate(final int hours) {
    return new Freelancer(this.id, this.name, hours);
  }

}
