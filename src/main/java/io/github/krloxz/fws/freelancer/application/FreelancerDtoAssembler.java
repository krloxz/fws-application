package io.github.krloxz.fws.freelancer.application;

import static io.github.krloxz.fws.springframework.AffordanceLink.affordanceLinkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import io.github.krloxz.fws.freelancer.application.dtos.CommunicationChannelDto;
import io.github.krloxz.fws.freelancer.application.dtos.FreelancerDto;
import io.github.krloxz.fws.springframework.AffordanceLink;

/**
 * {@link RepresentationModelAssembler} that converts {@link FreelancerDto}'s into
 * {@link RepresentationModel} instances.
 *
 * @author Carlos Gomez
 */
@Component
class FreelancerDtoAssembler implements RepresentationModelAssembler<FreelancerDto, EntityModel<FreelancerDto>> {

  private final PagedResourcesAssembler<FreelancerDto> pageAssembler;

  FreelancerDtoAssembler(final PagedResourcesAssembler<FreelancerDto> pageAssembler) {
    this.pageAssembler = pageAssembler;
  }

  @Override
  public EntityModel<FreelancerDto> toModel(final FreelancerDto dto) {
    final var freelancerId = dto.id().orElse("");
    return EntityModel.of(dto)
        .add(affordanceLinkTo(method().list(null)).withRel(IanaLinkRelations.COLLECTION))
        .add(affordanceLinkTo(method().get(freelancerId)).withSelfRel())
        .add(affordanceLinkTo(method().changeAddress(freelancerId, null)))
        .add(affordanceLinkTo(method().updateNicknames(freelancerId, null)))
        .add(affordanceLinkTo(method().updateWage(freelancerId, null)))
        .add(affordanceLinkTo(method().addCommunicationChannel(freelancerId, null)))
        .add(dto.communicationChannels()
            .stream()
            .map(CommunicationChannelDto::id)
            .flatMap(Optional::stream)
            .map(channelId -> affordanceLinkTo(method().removeCommunicationChannel(freelancerId, channelId)))
            .toArray(AffordanceLink[]::new));
  }

  /**
   * Converts a {@link Page} of {@link FreelancerDto}'s into a {@link PagedModel} instance.
   *
   * @param page
   *        the page to convert
   * @return the converted page
   */
  CollectionModel<EntityModel<FreelancerDto>> toPagedModel(final Page<FreelancerDto> page) {
    final var pagedModel = this.pageAssembler.toModel(page, this)
        .add(affordanceLinkTo(method().register(null)));
    final var affordanceLinks = pagedModel.getLinks().stream()
        .map(AffordanceLink::toAffordanceLink)
        .toArray(Link[]::new);
    return pagedModel.removeLinks().add(affordanceLinks);
  }

  private static FreelancersApiController method() {
    return methodOn(FreelancersApiController.class);
  }

}
