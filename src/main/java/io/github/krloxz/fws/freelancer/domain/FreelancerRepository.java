package io.github.krloxz.fws.freelancer.domain;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Carlos Gomez
 */
public interface FreelancerRepository {

  Mono<Void> deleteAll();

  Mono<Freelancer> save(Freelancer freelancer);

  Flux<Freelancer> findAll();

}
