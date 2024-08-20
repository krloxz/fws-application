package io.github.krloxz.fws.freelancer.domain;

import java.util.UUID;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
  Mono<Void> deleteAll();

  /**
   * Saves a freelancer.
   *
   * @param freelancer
   *        the freelancer to save
   * @return a {@link Mono} that completes when the freelancer is saved
   */
  Mono<Freelancer> save(Freelancer freelancer);

  /**
   * Updates a freelancer.
   *
   * @param freelancer
   *        the freelancer to update
   * @return a {@link Mono} that completes when the freelancer is updated
   */
  Mono<Freelancer> update(Freelancer freelancer);

  /**
   * Returns a page of freelancers meeting the criteria specified by the given {@link PageSpec}.
   *
   * @param pageSpec
   *        specification for the page to return
   * @return a {@link Flux} with the requested page of freelancers
   */
  Flux<Freelancer> findAllBy(PageSpec pageSpec);

  /**
   * Finds a freelancer by its unique identifier.
   *
   * @param id
   *        the unique identifier of the freelancer to find
   * @return a {@link Mono} with the freelancer, if found
   */
  Mono<Freelancer> findById(UUID id);

  /**
   * @return a {@link Mono} with the count of freelancers in the repository
   */
  Mono<Integer> count();

}
