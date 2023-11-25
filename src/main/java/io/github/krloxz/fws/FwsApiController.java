package io.github.krloxz.fws;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.reactive.WebFluxLinkBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import io.github.krloxz.fws.freelancer.application.FreelancersApiController;
import reactor.core.publisher.Flux;
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
    final var resources = WebFluxLinkBuilder.methodOn(FwsApiController.class).getResources(exchange);
    final var freelancersApi = WebFluxLinkBuilder.methodOn(FreelancersApiController.class).getAll();
    return Flux.concat(
        WebFluxLinkBuilder.linkTo(resources, exchange)
            .withSelfRel()
            .toMono())
        .concatWith(
            WebFluxLinkBuilder.linkTo(freelancersApi, exchange)
                .withRel("freelancers")
                .toMono())
        .collectList()
        .map(CollectionModel::empty);
  }

}
