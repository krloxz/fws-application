package io.github.krloxz.fws.freelancer.application;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

import io.github.krloxz.fws.freelancer.application.dtos.AddressDtoBuilder;
import io.github.krloxz.fws.freelancer.application.dtos.CommunicationChannelDto;
import io.github.krloxz.fws.freelancer.application.dtos.FreelancerDto;
import io.github.krloxz.fws.freelancer.application.dtos.FreelancerDtoBuilder;
import io.github.krloxz.fws.freelancer.application.dtos.HourlyWageDto;
import io.github.krloxz.fws.freelancer.domain.Address;
import io.github.krloxz.fws.freelancer.domain.CommunicationChannel;
import io.github.krloxz.fws.freelancer.domain.Freelancer;
import io.github.krloxz.fws.freelancer.domain.FreelancerId;
import io.github.krloxz.fws.freelancer.domain.FreelancerRepository;
import io.github.krloxz.fws.freelancer.domain.HourlyWage;
import io.github.krloxz.fws.freelancer.domain.PersonName;
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

  FreelancersApiController(final FreelancerRepository repository) {
    this.repository = repository;
  }

  /**
   * @return all the freelancers registered on the system
   */
  @GetMapping
  @Transactional(readOnly = true)
  public Flux<FreelancerDto> getAll() {
    return this.repository.findAll()
        .map(this::toDto);
  }

  /**
   * @param id
   *        freelancer identifier
   * @return the freelancer identified by the given identifier
   */
  @GetMapping("/{id}")
  @Transactional(readOnly = true)
  public Mono<FreelancerDto> getOne(@PathVariable final String id) {
    return toFreelancerId(id)
        .flatMap(this.repository::findById)
        .map(this::toDto)
        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)));
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
    return this.repository.save(toFreelancer(dto))
        .map(this::toDto);
  }

  private Mono<FreelancerId> toFreelancerId(final String id) {
    try {
      return Mono.just(FreelancerId.of(UUID.fromString(id)));
    } catch (final IllegalArgumentException e) {
      return Mono.empty();
    }
  }

  private FreelancerDto toDto(final Freelancer freelancer) {
    return new FreelancerDtoBuilder()
        .id(freelancer.id().value().toString())
        .firstName(freelancer.name().first())
        .lastName(freelancer.name().last())
        .middleName(freelancer.name().middle())
        .gender(freelancer.gender())
        .birthDate(freelancer.birthDate())
        .address(
            new AddressDtoBuilder()
                .street(freelancer.address().street())
                .apartment(freelancer.address().apartment())
                .city(freelancer.address().city())
                .state(freelancer.address().state())
                .zipCode(freelancer.address().zipCode())
                .country(freelancer.address().country())
                .build())
        .wage(new HourlyWageDto(freelancer.wage().value(), freelancer.wage().currency().getDisplayName()))
        .nicknames(freelancer.nicknames())
        .communicationChannels(freelancer.communicationChannels().stream().map(this::toDto).toList())
        .build();
  }

  private CommunicationChannelDto toDto(final CommunicationChannel channel) {
    return new CommunicationChannelDto(channel.value(), channel.type());
  }

  private Freelancer toFreelancer(final FreelancerDto dto) {
    return Freelancer.builder()
        .id(FreelancerId.create())
        .name(
            PersonName.builder()
                .first(dto.firstName())
                .last(dto.lastName())
                .middle(dto.middleName())
                .build())
        .gender(dto.gender())
        .birthDate(dto.birthDate())
        .address(
            Address.builder()
                .street(dto.address().street())
                .apartment(dto.address().apartment())
                .city(dto.address().city())
                .state(dto.address().state())
                .zipCode(dto.address().zipCode())
                .country(dto.address().country())
                .build())
        .wage(HourlyWage.of(dto.wage().value(), dto.wage().currency()))
        .nicknames(dto.nicknames())
        .communicationChannels(dto.communicationChannels().stream().map(this::toChannel).toList())
        .build();
  }

  private CommunicationChannel toChannel(final CommunicationChannelDto dto) {
    return CommunicationChannel.of(dto.value(), dto.type());
  }

}
