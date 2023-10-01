package io.github.krloxz.fws;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import io.github.krloxz.fws.freelancer.domain.Address;
import io.github.krloxz.fws.freelancer.domain.CommunicationChannel;
import io.github.krloxz.fws.freelancer.domain.Freelancer;
import io.github.krloxz.fws.freelancer.domain.FreelancerId;
import io.github.krloxz.fws.freelancer.domain.FreelancerRepository;
import io.github.krloxz.fws.freelancer.domain.Gender;
import io.github.krloxz.fws.freelancer.domain.HourlyWage;
import io.github.krloxz.fws.freelancer.domain.PersonName;
import reactor.core.publisher.Flux;

@Component
class SampleDataInitializer {

  private static final Log LOGGER = LogFactory.getLog(SampleDataInitializer.class);
  private final FreelancerRepository repository;

  SampleDataInitializer(final FreelancerRepository repository) {
    this.repository = repository;
  }

  @Transactional
  @EventListener(ApplicationReadyEvent.class)
  Flux<Freelancer> initialize() {
    return this.repository.deleteAll()
        .thenMany(
            Flux.just("Josh", "Cornelia", "Dr. Syer", "Violetta", "Stephane", "Olga", "Sebastian", "Madhura")
                .map(this::freelancer))
        .flatMap(this.repository::save)
        .thenMany(this.repository.findAll())
        .doOnNext(LOGGER::info);
  }

  Freelancer freelancer(final String name) {
    return Freelancer.builder()
        .id(FreelancerId.create())
        .name(PersonName.of(name, "Doe"))
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
