package io.github.krloxz.fws.freelancer.application;

import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.linkTo;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.methodOn;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.reactive.ReactiveRepresentationModelAssembler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import io.github.krloxz.fws.freelancer.application.dtos.FreelancerDto;
import io.github.krloxz.fws.springframework.AffordanceLink;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * {@link ReactiveRepresentationModelAssembler} that converts {@link FreelancerDto}'s into
 * {@link RepresentationModel} instances.
 *
 * @author Carlos Gomez
 */
@Component
class FreelancerDtoAssembler
    implements ReactiveRepresentationModelAssembler<FreelancerDto, EntityModel<FreelancerDto>> {

  @Override
  public Mono<EntityModel<FreelancerDto>> toModel(final FreelancerDto dto, final ServerWebExchange exchange) {
    return linkTo(method().getAll(), exchange)
        .withRel(IanaLinkRelations.COLLECTION)
        .toMono(AffordanceLink::new)
        .concatWith(
            linkTo(method().getOne(dto.id().orElse("")), exchange)
                .withSelfRel()
                .toMono(AffordanceLink::new))
        .concatWith(
            linkTo(method().changeAddress(dto.id().orElse(""), null), exchange)
                .withRel(AffordanceLink.AFFORDANCE_REL)
                .toMono(AffordanceLink::new))
        .concatWith(
            linkTo(method().updateNicknames(dto.id().orElse(""), null), exchange)
                .withRel(AffordanceLink.AFFORDANCE_REL)
                .toMono(AffordanceLink::new))
        .concatWith(
            linkTo(method().addCommunicationChannel(dto.id().orElse(""), null), exchange)
                .withRel(AffordanceLink.AFFORDANCE_REL)
                .toMono(AffordanceLink::new))
        .concatWith(
            Flux.fromIterable(dto.communicationChannels())
                .flatMap(
                    channel -> linkTo(method()
                        .removeCommunicationChannel(dto.id().orElse(""), channel.id().orElse("")), exchange)
                            .withRel(AffordanceLink.AFFORDANCE_REL)
                            .toMono(AffordanceLink::new)))
        .collectList()
        .map(links -> EntityModel.of(dto, links));
  }

  @Override
  public Mono<CollectionModel<EntityModel<FreelancerDto>>> toCollectionModel(
      final Flux<? extends FreelancerDto> entities, final ServerWebExchange exchange) {
    return linkTo(method().getAll(), exchange)
        .withSelfRel()
        .toMono(AffordanceLink::new)
        .flatMap(
            selfLink -> entities.flatMap(entity -> toModel(entity, exchange))
                .collectList()
                .map(dtos -> CollectionModel.of(dtos, selfLink)));
  }

  private static FreelancersApiController method() {
    return methodOn(FreelancersApiController.class);
  }

}
