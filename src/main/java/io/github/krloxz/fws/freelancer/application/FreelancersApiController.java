package io.github.krloxz.fws.freelancer.application;

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

import io.github.krloxz.fws.freelancer.domain.Address;
import io.github.krloxz.fws.freelancer.domain.CommunicationChannel;
import io.github.krloxz.fws.freelancer.domain.Freelancer;
import io.github.krloxz.fws.freelancer.domain.FreelancerId;
import io.github.krloxz.fws.freelancer.domain.FreelancerRepository;
import io.github.krloxz.fws.freelancer.domain.Gender;
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
    return this.repository.findAll()
        .filter(freelancer -> freelancer.id().value().toString().equals(id))
        .map(this::toDto)
        .next();
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

  FreelancerDto toDto(final Freelancer freelancer) {
    return new FreelancerDto(
        freelancer.id().value().toString(),
        freelancer.name().first(),
        freelancer.name().last());
  }

  Freelancer toFreelancer(final FreelancerDto dto) {
    return Freelancer.builder()
        .id(FreelancerId.create())
        .name(PersonName.of(dto.firstName(), dto.lastName()))
        .gender(Gender.MALE)
        .address(
            Address.builder()
                .street("1 Main St")
                .city("Boston")
                .state("MA")
                .zipCode("01234")
                .build())
        .wage(HourlyWage.of("50.00", "USD"))
        .addNickname("Freelancer")
        .addCommunicationChannel(
            CommunicationChannel.of("freelancer@freelancer.com", CommunicationChannel.Type.EMAIL))
        .build();
  }

}
