package io.github.krloxz.fws;

import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.linkTo;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.methodOn;

import org.springframework.hateoas.CollectionModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import io.github.krloxz.fws.freelancer.application.FreelancersApiController;
import io.github.krloxz.fws.springframework.AffordanceLink;
import reactor.core.publisher.Mono;

/**
 * Restful controller that exposes the resources that compose the Freelancer Web Services API.
 *
 * @author Carlos Gomez
 */
@RestController
@RequestMapping("/")
public class FwsApiController {

  /**
   * @param exchange
   *        {@link ServerWebExchange} used to create resource links
   * @return the resources that compose the Freelancer Web Services API
   */
  @GetMapping
  public Mono<CollectionModel<Object>> getResources(final ServerWebExchange exchange) {
    return linkTo(methodOn(FwsApiController.class).getResources(exchange), exchange)
        .withSelfRel()
        .toMono(AffordanceLink::new)
        .concatWith(
            linkTo(methodOn(FreelancersApiController.class).getAll(), exchange)
                .withRel("freelancers")
                .toMono(AffordanceLink::new))
        .collectList()
        .map(CollectionModel::empty);
  }

}
