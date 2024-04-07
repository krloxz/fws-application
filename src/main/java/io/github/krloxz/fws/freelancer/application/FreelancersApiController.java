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
   * Adds a new communication channel to the freelancer identified by the given identifier.
   *
   * @param id
   *        freelancer identifier
   * @param channel
   *        communication channel data
   * @return a {@link Mono} that emits the updated freelancer's data
   * @implNote This operation is modeled as the addition of a freelancer sub-resource because a patch
   *           would imply that the whole sub-resource, a collection of communication channels in this
   *           case, is updated at once while the actual purpose is to add channels one at a time.
   *           This may be because of a business requirement or a technology constraint. In any case
   *           this is an example of how an RPC can be modeled as a state transition: the freelancer
   *           transitions from not having a link to delete a communication channel to having one.
   */
  @PostMapping("/{id}/communication-channels")
  public Mono<FreelancerDto> addCommunicationChannel(
      @PathVariable final String id,
      @Validated @RequestBody final CommunicationChannelDto channel) {
    return findById(id)
        .map(freelancer -> freelancer.add(this.mapper.fromDto(channel)))
        .flatMap(this.repository::update)
        .map(this.mapper::toDto);
  }

  @DeleteMapping("/{id}/communication-channels/{channelId}")
  public Mono<FreelancerDto> removeCommunicationChannel(
      @PathVariable final String id,
      @PathVariable final String channelId) {
    throw new UnsupportedOperationException("Not implemented yet");
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

}
