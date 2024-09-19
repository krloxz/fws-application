package io.github.krloxz.fws.project.application;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import io.github.krloxz.fws.core.FreelancerId;
import io.github.krloxz.fws.core.PageSpec;
import io.github.krloxz.fws.project.domain.Freelancer;
import io.github.krloxz.fws.project.domain.FreelancerService;
import io.github.krloxz.fws.project.domain.ProjectId;
import io.github.krloxz.fws.project.domain.ProjectRepository;

/**
 * Restful controller that exposes the Projects API.
 *
 * @author Carlos Gomez
 */
@Transactional
@RestController
@RequestMapping("/projects")
public class ProjectsApiController {

  private final ProjectRepository repository;
  private final ProjectDtoMapper mapper;
  private final ProjectDtoAssembler assembler;
  private final FreelancerService freelancerService;

  ProjectsApiController(
      final ProjectRepository repository,
      final ProjectDtoMapper mapper,
      final ProjectDtoAssembler assembler,
      final FreelancerService freelancerService) {
    this.repository = repository;
    this.mapper = mapper;
    this.assembler = assembler;
    this.freelancerService = freelancerService;
  }

  /**
   * @param pageRequest
   *        details of the requested page
   * @return a paginated list of projects
   */
  @GetMapping
  @Transactional(readOnly = true)
  public CollectionModel<EntityModel<ProjectDto>> projects(final Pageable pageRequest) {
    return pageRequest.toOptional()
        .map(this::toPageSpec)
        .map(this.repository::findAllBy)
        .map(this.mapper::toDto)
        .map(dtos -> getPage(dtos, pageRequest))
        .map(this.assembler::toPagedModel)
        .orElseThrow();
  }

  /**
   * @param id
   *        a project identifier
   * @return the project with the given ID
   * @throws ResponseStatusException
   *         404 - if the project with the given ID does not exist
   */
  @GetMapping("/{id}")
  @Transactional(readOnly = true)
  public EntityModel<ProjectDto> project(@PathVariable final String id) {
    return projectId(id)
        .flatMap(this.repository::findById)
        .map(this.mapper::toDto)
        .map(this.assembler::toModel)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
  }

  /**
   * Creates a new project.
   *
   * @param dto
   *        project's data with no identifier
   * @return the project's data including an identifier assigned by the system
   */
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public EntityModel<ProjectDto> create(@Validated @RequestBody final ProjectDto dto) {
    return Optional.of(dto)
        .map(this.mapper::fromDto)
        .map(this.repository::save)
        .map(this.mapper::toDto)
        .map(this.assembler::toModel)
        .orElseThrow();
  }

  /**
   * Allows a freelancer to join a project.
   *
   * @param id
   *        the project identifier
   * @param request
   *        the join request
   * @return the updated project data
   */
  @PostMapping("/{id}/join")
  public EntityModel<ProjectDto> join(
      @PathVariable final String id,
      @Validated @RequestBody final JoinRequest request) {
    return projectId(id)
        .flatMap(this.repository::findById)
        .map(project -> project.assign(findFreelancer(request), request.committedHours()))
        .map(this.repository::update)
        .map(this.mapper::toDto)
        .map(this.assembler::toModel)
        .orElseThrow();
  }

  private PageSpec toPageSpec(final Pageable pageable) {
    return new PageSpec(pageable.getPageNumber(), pageable.getPageSize());
  }

  private Page<ProjectDto> getPage(final List<ProjectDto> content, final Pageable pageRequest) {
    return PageableExecutionUtils.getPage(content, pageRequest, this.repository::count);
  }

  private Optional<ProjectId> projectId(final String id) {
    try {
      return Optional.of(new ProjectId(UUID.fromString(id)));
    } catch (final IllegalArgumentException e) {
      return Optional.empty();
    }
  }

  private Freelancer findFreelancer(final JoinRequest request) {
    return this.freelancerService.findFreelancer(new FreelancerId(request.freelancerId()));
  }

}
