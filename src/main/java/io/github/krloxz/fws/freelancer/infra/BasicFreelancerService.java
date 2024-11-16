package io.github.krloxz.fws.freelancer.infra;

import java.util.Optional;
import java.util.UUID;

import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import io.github.krloxz.fws.core.DomainException;
import io.github.krloxz.fws.core.FreelancerId;
import io.github.krloxz.fws.core.PersonName;
import io.github.krloxz.fws.freelancer.application.FreelancersApiController;
import io.github.krloxz.fws.freelancer.application.dtos.FreelancerDto;
import io.github.krloxz.fws.project.domain.Freelancer;
import io.github.krloxz.fws.project.domain.FreelancerService;

/**
 * Basic implementation of the {@link FreelancerService} that uses the
 * {@link FreelancersApiController} to fetch the freelancer data.
 *
 * @author Carlos Gomez
 */
@Component
class BasicFreelancerService implements FreelancerService {

  private final FreelancersApiController controller;

  BasicFreelancerService(final FreelancersApiController controller) {
    this.controller = controller;
  }

  @Override
  public Freelancer findFreelancer(final FreelancerId id) {
    try {
      return Optional.of(id)
          .map(FreelancerId::value)
          .map(UUID::toString)
          .map(this.controller::get)
          .map(EntityModel::getContent)
          .map(this::toFreelancer)
          .orElseThrow();
    } catch (final ResponseStatusException e) {
      if (HttpStatus.NOT_FOUND == e.getStatusCode()) {
        throw new DomainException("Freelancer is not allowed to join this project", e);
      }
      throw e;
    }
  }

  private Freelancer toFreelancer(final FreelancerDto dto) {
    return new Freelancer(
        new FreelancerId(UUID.fromString(dto.id().orElseThrow())),
        PersonName.builder()
            .first(dto.firstName())
            .last(dto.lastName())
            .middle(dto.middleName())
            .build());
  }

}
