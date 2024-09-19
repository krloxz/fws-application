package io.github.krloxz.fws.freelancer.application;

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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import io.github.krloxz.fws.core.PageSpec;
import io.github.krloxz.fws.freelancer.application.dtos.AddressDto;
import io.github.krloxz.fws.freelancer.application.dtos.CommunicationChannelDto;
import io.github.krloxz.fws.freelancer.application.dtos.FreelancerDto;
import io.github.krloxz.fws.freelancer.application.dtos.HourlyWageDto;
import io.github.krloxz.fws.freelancer.domain.Freelancer;
import io.github.krloxz.fws.freelancer.domain.FreelancerRepository;

/**
 * Restful controller that exposes the Freelancers API.
 *
 * @author Carlos Gomez
 */
@Transactional
@RestController
@RequestMapping("/freelancers")
public class FreelancersApiController {

  private final FreelancerRepository repository;
  private final FreelancerDtoMapper mapper;
  private final FreelancerDtoAssembler assembler;

  FreelancersApiController(
      final FreelancerRepository repository,
      final FreelancerDtoMapper mapper,
      final FreelancerDtoAssembler assembler) {
    this.repository = repository;
    this.mapper = mapper;
    this.assembler = assembler;
  }

  /**
   * @param pageRequest
   *        details of the requested page
   * @return a paginated list of freelancers
   */
  @GetMapping
  @Transactional(readOnly = true)
  public CollectionModel<EntityModel<FreelancerDto>> list(final Pageable pageRequest) {
    return pageRequest.toOptional()
        .map(this::toPageSpec)
        .map(this.repository::findAllBy)
        .map(this.mapper::toDto)
        .map(content -> getPage(pageRequest, content))
        .map(this.assembler::toPagedModel)
        .orElseThrow();
  }

  /**
   * @param id
   *        a freelancer identifier
   * @return the freelancer with the given ID
   * @throws ResponseStatusException
   *         404 - if the freelancer with the given ID does not exist
   */
  @GetMapping("/{id}")
  @Transactional(readOnly = true)
  public EntityModel<FreelancerDto> get(@PathVariable final String id) {
    return Optional.of(id)
        .map(this::findById)
        .map(this.mapper::toDto)
        .map(this.assembler::toModel)
        .orElseThrow();
  }

  /**
   * Registers a new freelancer.
   *
   * @param dto
   *        freelancer's data with no identifier
   * @return the freelancer's data including the identifier that was assigned by the system
   */
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public EntityModel<FreelancerDto> register(@Validated @RequestBody final FreelancerDto dto) {
    return Optional.of(dto)
        .map(this.mapper::fromDto)
        .map(this.repository::save)
        .map(this.mapper::toDto)
        .map(this.assembler::toModel)
        .orElseThrow();
  }

  /**
   * Changes the address of the freelancer identified by the given identifier.
   *
   * @param id
   *        freelancer identifier
   * @param newAddress
   *        new address data
   * @return the updated freelancer's data
   * @throws ResponseStatusException
   *         404 - if the freelancer with the given ID does not exist
   */
  @PatchMapping("/{id}/address")
  public EntityModel<FreelancerDto> changeAddress(
      @PathVariable final String id,
      @Validated @RequestBody final AddressDto newAddress) {
    return Optional.of(newAddress)
        .map(this.mapper::fromDto)
        .map(findById(id)::movedTo)
        .map(this.repository::update)
        .map(this.mapper::toDto)
        .map(this.assembler::toModel)
        .orElseThrow();
  }

  /**
   * Updates the nicknames of the freelancer identified by the given identifier.
   *
   * @param id
   *        freelancer identifier
   * @param nicknames
   *        new nicknames
   * @return the updated freelancer's data
   * @throws ResponseStatusException
   *         404 - if the freelancer with the given ID does not exist
   */
  @PatchMapping("/{id}/nicknames")
  public EntityModel<FreelancerDto> updateNicknames(
      @PathVariable final String id,
      @RequestBody final String[] nicknames) {
    return Optional.of(nicknames)
        .map(findById(id)::withNicknames)
        .map(this.repository::update)
        .map(this.mapper::toDto)
        .map(this.assembler::toModel)
        .orElseThrow();
  }

  /**
   * Updates the hourly wage of the freelancer identified by the given identifier.
   *
   * @param id
   *        freelancer identifier
   * @param wage
   *        new hourly wage
   * @return the updated freelancer's data
   * @throws ResponseStatusException
   *         404 - if the freelancer with the given ID does not exist
   */
  @PatchMapping("/{id}/wage")
  public EntityModel<FreelancerDto> updateWage(
      @PathVariable final String id,
      @Validated @RequestBody final HourlyWageDto wage) {
    return Optional.of(wage)
        .map(this.mapper::fromDto)
        .map(findById(id)::withHourlyWage)
        .map(this.repository::update)
        .map(this.mapper::toDto)
        .map(this.assembler::toModel)
        .orElseThrow();
  }

  /**
   * Adds a new communication channel to the freelancer identified by the given identifier.
   *
   * @param id
   *        freelancer identifier
   * @param channel
   *        communication channel data
   * @return the updated freelancer's data
   * @throws ResponseStatusException
   *         404 - if the freelancer with the given ID does not exist
   * @implNote This operation simulates that there is a business requirement or a technology
   *           constraint that prevents from managing the whole collection of communication channels
   *           at once. Therefore, this operation is modeled as the addition of a freelancer
   *           sub-resource instead of a patch operation.
   *           <p>
   *           This operation could be consider an example of how an RPC call can be modeled as a
   *           state transition since the freelancer transitions from not having a link to delete a
   *           communication channel to having one.
   */
  @PostMapping("/{id}/communication-channels")
  public EntityModel<FreelancerDto> addCommunicationChannel(
      @PathVariable final String id,
      @Validated @RequestBody final CommunicationChannelDto channel) {
    return Optional.of(channel)
        .map(this.mapper::fromDto)
        .map(findById(id)::addCommunicationChannel)
        .map(this.repository::update)
        .map(this.mapper::toDto)
        .map(this.assembler::toModel)
        .orElseThrow();
  }

  /**
   * Removes a communication channel from the freelancer identified by the given identifier.
   *
   * @param id
   *        freelancer identifier
   * @param channelId
   *        identifier of the communication channel to be removed
   * @return the updated freelancer's data
   * @throws ResponseStatusException
   *         404 - if the freelancer with the given ID does not exist
   * @implNote This operation simulates that there is a business requirement or a technology
   *           constraint that prevents from managing the whole collection of communication channels
   *           at once. Therefore, this operation is modeled as the removal of a freelancer
   *           sub-resource instead of a patch operation.
   *           <p>
   *           This approach may be useful given the context, but turns out to be very complex,
   *           especially with collection sub-resources:
   *           <ul>
   *           <li>In this case the communication channel was converted from a value object into an
   *           entity because without identity it's impossible to process resources one at a time.
   *           <li>Entities are more difficult to manage than value objects.
   *           <li>Affordances to remove communication channels are more complex because the client
   *           should consider the possibility of having only one or multiple affordances, which is an
   *           additional challenge for the standardization of the API, since there are other
   *           affordances that will never have multiple items and picking a single model that fits
   *           all is more difficult.
   *           </ul>
   *           I think this approach is valuable to model RPC calls but it's not the best choice to
   *           manage sub-resources, in that case patch operations look more appropriate.
   */
  @DeleteMapping("/{id}/communication-channels/{channelId}")
  public EntityModel<FreelancerDto> removeCommunicationChannel(
      @PathVariable final String id,
      @PathVariable final String channelId) {
    return Optional.of(removeCommunicationChannel(findById(id), channelId))
        .map(this.repository::update)
        .map(this.mapper::toDto)
        .map(this.assembler::toModel)
        .orElseThrow();
  }

  private PageSpec toPageSpec(final Pageable pageable) {
    return new PageSpec(pageable.getPageNumber(), pageable.getPageSize());
  }

  private Page<FreelancerDto> getPage(final Pageable pageRequest, final List<FreelancerDto> content) {
    return PageableExecutionUtils.getPage(content, pageRequest, this.repository::count);
  }

  private Freelancer findById(final String id) {
    return toFreelancerId(id)
        .flatMap(this.repository::findById)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
  }

  private Optional<UUID> toFreelancerId(final String id) {
    try {
      return Optional.of(UUID.fromString(id));
    } catch (final IllegalArgumentException e) {
      return Optional.empty();
    }
  }

  private Freelancer removeCommunicationChannel(final Freelancer freelancer, final String channelId) {
    return freelancer.removeCommunicationChannel(UUID.fromString(channelId))
        .orElseThrow(
            () -> new ResponseStatusException(
                HttpStatus.UNPROCESSABLE_ENTITY, "This communication channel doesn't exist: " + channelId));
  }

}
