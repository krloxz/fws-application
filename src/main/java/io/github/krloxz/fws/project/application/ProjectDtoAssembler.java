package io.github.krloxz.fws.project.application;

import static io.github.krloxz.fws.support.AffordanceLink.affordanceLinkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkRelation;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.mediatype.hal.HalModelBuilder;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import io.github.krloxz.fws.freelancer.application.FreelancersApiController;
import io.github.krloxz.fws.support.AffordanceLink;

/**
 * Assembles {@link ProjectDto} instances into {@link EntityModel} instances.
 *
 * @author Carlos Gomez
 */
@Component
class ProjectDtoAssembler implements RepresentationModelAssembler<ProjectDto, EntityModel<ProjectDto>> {

  private final PagedResourcesAssembler<ProjectDto> pageAssembler;

  ProjectDtoAssembler(final PagedResourcesAssembler<ProjectDto> pageAssembler) {
    this.pageAssembler = pageAssembler;
  }

  @Override
  @SuppressWarnings("unchecked")
  public EntityModel<ProjectDto> toModel(final ProjectDto dto) {
    final var freelancers = dto.freelancers()
        .stream()
        .map(this::toModel)
        .toList();
    final var model = HalModelBuilder.halModelOf(dto.withoutFreelancers())
        .embed(freelancers, LinkRelation.of("freelancers"))
        .link(affordanceLinkTo(method().projects(null)).withRel(IanaLinkRelations.COLLECTION))
        .link(affordanceLinkTo(method().project(dto.id().orElse(""))).withSelfRel())
        .build();
    if (!isProjectFullyAllocated(dto)) {
      model.add(affordanceLinkTo(method().join(dto.id().orElse(""), null)));
    }
    return (EntityModel<ProjectDto>) model;
  }

  /**
   * Converts a {@link Page} of {@link ProjectDto}'s into a {@link PagedModel} instance.
   *
   * @param page
   *        the page to convert
   * @return the converted page
   */
  CollectionModel<EntityModel<ProjectDto>> toPagedModel(final Page<ProjectDto> page) {
    final var pagedModel = this.pageAssembler.toModel(page, this)
        .add(affordanceLinkTo(method().create(null)));
    final var affordanceLinks = pagedModel.getLinks().stream()
        .map(AffordanceLink::toAffordanceLink)
        .toArray(Link[]::new);
    return pagedModel.removeLinks().add(affordanceLinks);
  }

  private EntityModel<FreelancerDto> toModel(final FreelancerDto dto) {
    return EntityModel.of(dto)
        .add(affordanceLinkTo(methodOn(FreelancersApiController.class).get(dto.id().toString())).withSelfRel());
  }

  private boolean isProjectFullyAllocated(final ProjectDto dto) {
    final var projectAllocation = dto.freelancers().stream()
        .mapToInt(FreelancerDto::allocatedHours)
        .sum();
    return projectAllocation == dto.requiredHours();
  }

  private static ProjectsApiController method() {
    return methodOn(ProjectsApiController.class);
  }

}
