package io.github.krloxz.fws.freelancer.application;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.reactive.ReactiveRepresentationModelAssembler;
import org.springframework.hateoas.server.reactive.WebFluxLinkBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import io.github.krloxz.fws.freelancer.application.dtos.FreelancerDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * {@link ReactiveRepresentationModelAssembler} that converts {@link FreelancerDto}'s into
 * {@link RepresentationModel} instances.
 *
 * @author Carlos Gomez
 */
@Component
class FreelancerAssembler implements ReactiveRepresentationModelAssembler<FreelancerDto, EntityModel<FreelancerDto>> {

  @Override
  public Mono<EntityModel<FreelancerDto>> toModel(final FreelancerDto dto, final ServerWebExchange exchange) {
    final var getOneMethod = WebFluxLinkBuilder.methodOn(FreelancersApiController.class).getOne(dto.id().orElse(""));
    return WebFluxLinkBuilder.linkTo(getOneMethod, exchange)
        .withSelfRel()
        .toMono()
        .map(link -> EntityModel.of(dto, link));
  }

  @Override
  public Mono<CollectionModel<EntityModel<FreelancerDto>>> toCollectionModel(
      final Flux<? extends FreelancerDto> entities, final ServerWebExchange exchange) {
    final var getAllMethod = WebFluxLinkBuilder.methodOn(FreelancersApiController.class).getAll();
    return WebFluxLinkBuilder.linkTo(getAllMethod, exchange)
        .withSelfRel()
        .toMono()
        .flatMap(
            selfLink -> entities.flatMap(entity -> toModel(entity, exchange))
                .collectList()
                .map(dtos -> CollectionModel.of(dtos, selfLink)));
  }

}
