package io.github.krloxz.fws.freelancer.application;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

import io.github.krloxz.fws.freelancer.application.dtos.AddressDto;
import io.github.krloxz.fws.freelancer.application.dtos.CommunicationChannelDto;
import io.github.krloxz.fws.freelancer.application.dtos.FreelancerDto;
import io.github.krloxz.fws.freelancer.domain.Freelancer;
import io.github.krloxz.fws.freelancer.domain.FreelancerRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Restful controller that exposes the Freelancers API.
 *
 * @author Carlos Gomez
 */
@Transactional
@RestController
@RequestMapping("freelancers")
public class FreelancersApiController {

  private final FreelancerRepository repository;
  private final FreelancerDtoMapper mapper;

  FreelancersApiController(final FreelancerRepository repository, final FreelancerDtoMapper mapper) {
    this.repository = repository;
    this.mapper = mapper;
  }

  /**
   * @return all the freelancers registered on the system
   */
  @GetMapping
  @Transactional(readOnly = true)
  public Flux<FreelancerDto> getAll() {
    return this.repository.findAll().map(this.mapper::toDto);
  }

  /**
   * @param id
   *        freelancer identifier
   * @return the freelancer identified by the given identifier
   */
  @GetMapping("/{id}")
  @Transactional(readOnly = true)
  public Mono<FreelancerDto> getOne(@PathVariable final String id) {
    return findById(id).map(this.mapper::toDto);
  }

  /**
   * Registers a new freelancer.
   *
   * @param dto
   *        freelancer's data with no identifier
   * @return the freelancer's data including the identifier that was assigned by the system
   */
  @PostMapping(consumes = MediaType.ALL_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  public Mono<FreelancerDto> register(@Validated @RequestBody final FreelancerDto dto) {
    return this.repository.save(this.mapper.fromDto(dto)).map(this.mapper::toDto);
  }

  /**
   * Changes the address of the freelancer identified by the given identifier.
   *
   * @param id
   *        freelancer identifier
   * @param newAddress
   *        new address data
   * @return a {@link Mono} that emits the updated freelancer's data
   */
  @PatchMapping("/{id}/address")
  public Mono<FreelancerDto> changeAddress(
      @PathVariable final String id,
      @Validated @RequestBody final AddressDto newAddress) {
    return findById(id)
        .map(freelancer -> freelancer.movedTo(this.mapper.fromDto(newAddress)))
        .flatMap(this.repository::update)
        .map(this.mapper::toDto);
  }

  /**
   * Updates the nicknames of the freelancer identified by the given identifier.
   *
   * @param id
   *        freelancer identifier
   * @param nicknames
   *        new nicknames
   * @return a {@link Mono} that emits the updated freelancer's data
   */
  @PatchMapping("/{id}/nicknames")
  public Mono<FreelancerDto> updateNicknames(
      @PathVariable final String id,
      @RequestBody final String[] nicknames) {
    return findById(id)
        .map(freelancer -> freelancer.withNicknames(nicknames))
        .flatMap(this.repository::update)
        .map(this.mapper::toDto);
  }

  /**
   * Adds a new communication channel to the freelancer identified by the given identifier.
   *
   * @param id
   *        freelancer identifier
   * @param channel
   *        communication channel data
   * @return a {@link Mono} that emits the updated freelancer's data
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
  public Mono<FreelancerDto> addCommunicationChannel(
      @PathVariable final String id,
      @Validated @RequestBody final CommunicationChannelDto channel) {
    return findById(id)
        .map(freelancer -> freelancer.addCommunicationChannel(this.mapper.fromDto(channel)))
        .flatMap(this.repository::update)
        .map(this.mapper::toDto);
  }

  /**
   * Removes a communication channel from the freelancer identified by the given identifier.
   *
   * @param id
   *        freelancer identifier
   * @param channelId
   *        identifier of the communication channel to be removed
   * @return a {@link Mono} that emits the updated freelancer's data
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
   *           additional challenge for the standardization of the API since there are other
   *           affordances that will never have multiple items and picking a single model that fits
   *           all is more difficult.
   *           </ul>
   *           I think this approach is valuable to model RPC calls but it's not the best choice to
   *           manage sub-resources, in that case patch operations look more appropriate.
   */
  @DeleteMapping("/{id}/communication-channels/{channelId}")
  public Mono<FreelancerDto> removeCommunicationChannel(
      @PathVariable final String id,
      @PathVariable final String channelId) {
    return findById(id)
        .flatMap(freelancer -> removeCommunicationChannel(freelancer, channelId))
        .flatMap(this.repository::update)
        .map(this.mapper::toDto);
  }

  private Mono<Freelancer> findById(final String id) {
    return toFreelancerId(id)
        .flatMap(this.repository::findById)
        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)));
  }

  private Mono<UUID> toFreelancerId(final String id) {
    try {
      return Mono.just(UUID.fromString(id));
    } catch (final IllegalArgumentException e) {
      return Mono.empty();
    }
  }

  private Mono<Freelancer> removeCommunicationChannel(final Freelancer freelancer, final String channelId) {
    return freelancer.removeCommunicationChannel(UUID.fromString(channelId))
        .map(Mono::just)
        .orElse(
            Mono.error(
                new ResponseStatusException(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    "This communication channel doesn't exist: " + channelId)));
  }

}
