package io.github.krloxz.fws.freelancer.application;

import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.linkTo;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.methodOn;

import java.util.List;
import java.util.Optional;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.PagedModel.PageMetadata;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.reactive.ReactiveRepresentationModelAssembler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import io.github.krloxz.fws.freelancer.application.dtos.FreelancerDto;
import io.github.krloxz.fws.freelancer.application.dtos.PageDto;
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
public class FreelancerDtoAssembler
    implements ReactiveRepresentationModelAssembler<FreelancerDto, EntityModel<FreelancerDto>> {

  @Override
  public Mono<EntityModel<FreelancerDto>> toModel(final FreelancerDto dto, final ServerWebExchange exchange) {
    return linkTo(method().list(Optional.of(0), Optional.of(0)), exchange)
        .withRel(IanaLinkRelations.COLLECTION)
        .toMono(AffordanceLink::new)
        .concatWith(
            linkTo(method().get(dto.id().orElse("")), exchange)
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
            linkTo(method().updateWage(dto.id().orElse(""), null), exchange)
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
    return linkTo(method().list(null, null), exchange)
        .withSelfRel()
        .toMono(AffordanceLink::new)
        .flatMap(
            selfLink -> entities.flatMap(entity -> toModel(entity, exchange))
                .collectList()
                .map(dtos -> CollectionModel.of(dtos, selfLink)));
  }

  /**
   * Converts a {@link PageDto} of {@link FreelancerDto}'s into a {@link PagedModel} instance.
   *
   * @param page
   *        the page to convert
   * @param exchange
   *        the current server exchange
   * @return the converted page
   * @implNote this method should be added to a new interface that extends
   *           {@link ReactiveRepresentationModelAssembler} to support pagination
   */
  public Mono<PagedModel<EntityModel<FreelancerDto>>> toPagedModel(
      final PageDto<FreelancerDto> page, final ServerWebExchange exchange) {
    return linkTo(method().list(Optional.of(page.number()), Optional.of(page.size())), exchange)
        .withSelfRel()
        .toMono(AffordanceLink::new)
        .concatWith(
            linkTo(method().register(null), exchange)
                .withRel(AffordanceLink.AFFORDANCE_REL)
                .toMono(AffordanceLink::new))
        .concatWith(
            linkTo(method().list(Optional.of(0), Optional.of(page.size())), exchange)
                .withRel(IanaLinkRelations.FIRST)
                .toMono(AffordanceLink::new)
                .filter(link -> page.number() != 0))
        .concatWith(
            linkTo(method().list(Optional.of(page.number() - 1), Optional.of(page.size())), exchange)
                .withRel(IanaLinkRelations.PREV)
                .toMono(AffordanceLink::new)
                .filter(link -> page.number() != 0))
        .concatWith(
            linkTo(method().list(Optional.of(page.number() + 1), Optional.of(page.size())), exchange)
                .withRel(IanaLinkRelations.NEXT)
                .toMono(AffordanceLink::new)
                .filter(link -> page.number() < page.totalElements() / page.size()))
        .concatWith(
            linkTo(method().list(Optional.of(page.totalElements() / page.size()), Optional.of(page.size())),
                exchange)
                    .withRel(IanaLinkRelations.LAST)
                    .toMono(AffordanceLink::new)
                    .filter(link -> page.number() < page.totalElements() / page.size()))
        .collectList()
        .flatMap(
            links -> Flux.fromIterable(page.elements()).flatMap(entity -> toModel(entity, exchange))
                .collectList()
                .map(content -> pageModelOf(content, links, page)));
  }

  private PagedModel<EntityModel<FreelancerDto>> pageModelOf(
      final List<EntityModel<FreelancerDto>> content, final List<Link> links, final PageDto<FreelancerDto> page) {
    return PagedModel.of(
        content, new PageMetadata(page.size(), page.number(), page.totalElements()), links);
  }

  private static FreelancersApiController method() {
    return methodOn(FreelancersApiController.class);
  }

}
