package io.github.krloxz.fws.freelancer.application;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.krloxz.fws.freelancer.domain.Freelancer;
import io.github.krloxz.fws.freelancer.domain.FreelancerRepository;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("freelancers")
class FreelancerController {

  private final FreelancerRepository repository;

  FreelancerController(final FreelancerRepository repository) {
    this.repository = repository;
  }

  @GetMapping()
  @Transactional(readOnly = true)
  Flux<FreelancerDto> listFreelancers() {
    return this.repository.findAll().map(this::toDto);
  }

  FreelancerDto toDto(final Freelancer freelancer) {
    return new FreelancerDto(
        freelancer.id().value().toString(),
        freelancer.name().first());
  }

}


record FreelancerDto(String id, String firstName) {
}
