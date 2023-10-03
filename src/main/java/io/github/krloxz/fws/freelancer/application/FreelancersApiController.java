package io.github.krloxz.fws.freelancer.application;

import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
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
 * Restful controller to expose the Freelancers API.
 *
 * @author Carlos Gomez
 */
@RestController
@RequestMapping("freelancers")
@Transactional
class FreelancersApiController {

  private final FreelancerRepository repository;

  FreelancersApiController(final FreelancerRepository repository) {
    this.repository = repository;
  }

  @GetMapping()
  @Transactional(readOnly = true)
  Flux<FreelancerDto> listFreelancers() {
    return this.repository.findAll().map(this::toDto);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  Mono<FreelancerDto> add(@RequestBody final FreelancerDto freelancer) {
    return this.repository.save(toFreelancer(freelancer))
        .map(this::toDto);
  }

  FreelancerDto toDto(final Freelancer freelancer) {
    return new FreelancerDto(
        freelancer.id().value().toString(),
        freelancer.name().first());
  }

  Freelancer toFreelancer(final FreelancerDto dto) {
    return Freelancer.builder()
        .id(FreelancerId.create())
        .name(PersonName.of(dto.firstName(), "Doe"))
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
