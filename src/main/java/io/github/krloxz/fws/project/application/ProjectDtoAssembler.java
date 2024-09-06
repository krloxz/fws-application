package io.github.krloxz.fws.project.application;

import static io.github.krloxz.fws.springframework.AffordanceLink.affordanceLinkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import io.github.krloxz.fws.springframework.AffordanceLink;

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
  public EntityModel<ProjectDto> toModel(final ProjectDto dto) {
    return EntityModel.of(dto)
        .add(affordanceLinkTo(method().projects(null)).withRel(IanaLinkRelations.COLLECTION))
        .add(affordanceLinkTo(method().project(dto.id().orElse(""))).withSelfRel());
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

  private static ProjectsApiController method() {
    return methodOn(ProjectsApiController.class);
  }

}
