package io.github.krloxz.fws.freelancer.application;

import org.springframework.hateoas.server.core.Relation;

/**
 * DTO to carry freelancer's data.
 *
 * @author Carlos Gomez
 */
@Relation(collectionRelation = "freelancers")
public record FreelancerDto(String id, String firstName, String lastName) {

  public FreelancerDto(final String firstName, final String lastName) {
    this(null, firstName, lastName);
  }

}
