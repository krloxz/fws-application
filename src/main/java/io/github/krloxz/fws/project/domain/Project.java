package io.github.krloxz.fws.project.domain;

import java.util.List;

import org.immutables.value.Value;
import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.Identity;

import io.github.krloxz.fws.core.DomainException;

/**
 * Represents a project.
 *
 * @author Carlos Gomez
 */
@AggregateRoot
@Value.Immutable
public abstract class Project {

  /**
   * @return this project's identifier
   */
  @Identity
  public abstract ProjectId id();

  /**
   * @return this project's name
   */
  public abstract String name();

  /**
   * @return this project's description
   */
  public abstract String description();

  /**
   * @return the number of hours required per week for this project
   */
  public abstract int requiredHours();

  /**
   * @return the freelancers allocated to this project
   */
  public abstract List<Freelancer> freelancers();

  /**
   * Assigns a freelancer to this project for a given number of hours per week.
   *
   * @param freelancer
   *        the freelancer
   * @param committedHours
   *        the number of hours per week that the freelancer will commit to this project
   * @return a new instance of this project with the freelancer assigned
   */
  public Project assign(final Freelancer freelancer, final int committedHours) {
    final var hoursLeft = requiredHours() - allocatedHours();
    if (committedHours > hoursLeft) {
      throw new DomainException(
          "Cannot allocate %d hour(s), only %d hour(s) left", committedHours, hoursLeft);
    }
    return copy()
        .addFreelancer(freelancer.allocate(committedHours))
        .build();
  }

  private ImmutableProject.Builder copy() {
    return ImmutableProject.builder().from(this);
  }

  private int allocatedHours() {
    return freelancers().stream().mapToInt(Freelancer::allocatedHours).sum();
  }

}
