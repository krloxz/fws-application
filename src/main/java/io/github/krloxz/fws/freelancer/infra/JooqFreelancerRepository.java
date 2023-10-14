package io.github.krloxz.fws.freelancer.infra;

import static io.github.krloxz.fws.infra.jooq.Tables.ADDRESSES;
import static io.github.krloxz.fws.infra.jooq.Tables.COMMUNICATION_CHANNELS;
import static io.github.krloxz.fws.infra.jooq.Tables.FREELANCERS;
import static java.util.stream.Collectors.joining;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import io.github.krloxz.fws.freelancer.domain.Address;
import io.github.krloxz.fws.freelancer.domain.CommunicationChannel;
import io.github.krloxz.fws.freelancer.domain.Freelancer;
import io.github.krloxz.fws.freelancer.domain.FreelancerId;
import io.github.krloxz.fws.freelancer.domain.FreelancerRepository;
import io.github.krloxz.fws.freelancer.domain.Gender;
import io.github.krloxz.fws.freelancer.domain.HourlyWage;
import io.github.krloxz.fws.freelancer.domain.PersonName;
import io.github.krloxz.fws.infra.jooq.tables.records.AddressesRecord;
import io.github.krloxz.fws.infra.jooq.tables.records.CommunicationChannelsRecord;
import io.github.krloxz.fws.infra.jooq.tables.records.FreelancersRecord;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@Transactional(propagation = Propagation.MANDATORY)
class JooqFreelancerRepository implements FreelancerRepository {

  @Autowired
  private DSLContext create;

  @Override
  public Mono<Void> deleteAll() {
    return Mono.from(this.create.delete(FREELANCERS)).then();
  }

  @Override
  public Mono<Freelancer> save(final Freelancer freelancer) {
    return Flux.from(
        this.create.insertInto(FREELANCERS).set(toFreelancersRecord(freelancer)))
        .thenMany(
            this.create.insertInto(ADDRESSES).set(toAddressesRecord(freelancer)))
        .thenMany(
            this.create.batch(
                toCommunicationChannelsRecords(freelancer)
                    .map(
                        channelsRecord -> this.create.insertInto(COMMUNICATION_CHANNELS).set(channelsRecord))
                    .toList()))
        .then(Mono.just(freelancer));
  }

  @Override
  public Flux<Freelancer> findAll() {
    return Flux.from(
        this.create.select(FREELANCERS.ID).from(FREELANCERS).orderBy(FREELANCERS.FIRST_NAME))
        .<UUID>map(Record1::value1)
        .flatMap(this::findFreelancer)
        .map(this::toFreelancer);
  }

  private FreelancersRecord toFreelancersRecord(final Freelancer freelancer) {
    return new FreelancersRecord()
        .setId(freelancer.id().value())
        .setFirstName(freelancer.name().first())
        .setLastName(freelancer.name().last())
        .setMiddleName(freelancer.name().middle().orElse(null))
        .setGender(freelancer.gender().map(Gender::name).orElse(null))
        .setNicknames(freelancer.nicknames().stream().collect(joining(",")))
        .setHourlyWageValue(freelancer.wage().value())
        .setHourlyWageCurrency(freelancer.wage().currency().getCurrencyCode());
  }

  private AddressesRecord toAddressesRecord(final Freelancer freelancer) {
    final var address = freelancer.address();
    return new AddressesRecord()
        .setFreelancerId(freelancer.id().value())
        .setStreet(address.street())
        .setApartment(address.apartment().orElse(null))
        .setCity(address.city())
        .setState(address.state())
        .setCountry(address.country())
        .setZipCode(address.zipCode());
  }

  private Stream<CommunicationChannelsRecord> toCommunicationChannelsRecords(final Freelancer freelancer) {
    return freelancer.communicationChannels()
        .stream()
        .map(
            channel -> new CommunicationChannelsRecord()
                .setFreelancerId(freelancer.id().value())
                .setValue1(channel.value())
                .setType(channel.type().name()));
  }

  private Mono<List<Record>> findFreelancer(final UUID id) {
    return Flux.from(
        this.create.select(DSL.asterisk())
            .from(FREELANCERS)
            .join(ADDRESSES).onKey()
            .leftJoin(COMMUNICATION_CHANNELS).onKey()
            .where(FREELANCERS.ID.eq(id)))
        .collectList();
  }

  private Freelancer toFreelancer(final List<Record> result) {
    final var record = result.get(0).into(FREELANCERS);
    return Freelancer.builder()
        .id(FreelancerId.of(record.getId()))
        .name(
            PersonName.of(record.getFirstName(), record.getLastName()))
        .gender(Optional.ofNullable(record.getGender()).map(Gender::valueOf))
        .address(toAddress(result))
        .wage(
            HourlyWage.of(record.getHourlyWageValue().toPlainString(), record.getHourlyWageCurrency()))
        .nicknames(Set.of(record.getNicknames().split(",")))
        .communicationChannels(toCommunicationChannels(result))
        .build();
  }

  private Address toAddress(final List<Record> result) {
    final var record = result.get(0).into(ADDRESSES);
    return Address.builder()
        .street(record.getStreet())
        .apartment(Optional.ofNullable(record.getApartment()))
        .city(record.getCity())
        .state(record.getState())
        .country(record.getCountry())
        .zipCode(record.getZipCode())
        .build();
  }

  private List<CommunicationChannel> toCommunicationChannels(final List<Record> result) {
    return result.stream()
        .map(record -> record.into(COMMUNICATION_CHANNELS))
        .map(record -> CommunicationChannel.of(record.getValue1(), CommunicationChannel.Type.valueOf(record.getType())))
        .toList();
  }

}
