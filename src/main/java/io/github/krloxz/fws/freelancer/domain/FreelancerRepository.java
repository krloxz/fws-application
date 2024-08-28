package io.github.krloxz.fws.freelancer.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for freelancers.
 *
 * @author Carlos Gomez
 */
public interface FreelancerRepository {

  /**
   * Deletes all freelancers.
   *
   * @return a {@link Mono} that completes when all freelancers are deleted
   */
  void deleteAll();

  /**
   * Saves a freelancer.
   *
   * @param freelancer
   *        the freelancer to save
   * @return a {@link Mono} that completes when the freelancer is saved
   */
  Freelancer save(Freelancer freelancer);

  /**
   * Updates a freelancer.
   *
   * @param freelancer
   *        the freelancer to update
   * @return a {@link Mono} that completes when the freelancer is updated
   */
  Freelancer update(Freelancer freelancer);

  /**
   * Returns a page of freelancers meeting the criteria specified by the given {@link PageSpec}.
   *
   * @param pageSpec
   *        specification for the page to return
   * @return a {@link Flux} with the requested page of freelancers
   */
  List<Freelancer> findAllBy(PageSpec pageSpec);

  /**
   * Finds a freelancer by its unique identifier.
   *
   * @param id
   *        the unique identifier of the freelancer to find
   * @return a {@link Mono} with the freelancer, if found
   */
  Optional<Freelancer> findById(UUID id);

  /**
   * @return a {@link Mono} with the count of freelancers in the repository
   */
  int count();

}
