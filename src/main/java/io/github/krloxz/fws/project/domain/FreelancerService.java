package io.github.krloxz.fws.project.domain;

import io.github.krloxz.fws.core.DomainException;
import io.github.krloxz.fws.core.FreelancerId;

/**
 * Service to manage freelancers.
 *
 * @author Carlos Gomez
 */
public interface FreelancerService {

  /**
   * Finds a freelancer by its identifier.
   *
   * @param id
   *        the freelancer's identifier
   * @return the found freelancer
   * @throws DomainException
   *         if the freelancer is not found
   */
  Freelancer findFreelancer(FreelancerId id) throws DomainException;

}
