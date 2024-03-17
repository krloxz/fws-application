package io.github.krloxz.fws.freelancer.infra;

import static io.github.krloxz.fws.infra.jooq.Tables.ADDRESSES;
import static io.github.krloxz.fws.infra.jooq.Tables.COMMUNICATION_CHANNELS;
import static io.github.krloxz.fws.infra.jooq.Tables.FREELANCERS;
import static java.util.function.Predicate.not;

import java.util.List;
import java.util.UUID;

import org.jooq.Batch;
import org.jooq.DSLContext;
import org.jooq.DeleteConditionStep;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import io.github.krloxz.fws.freelancer.domain.Freelancer;
import io.github.krloxz.fws.freelancer.domain.FreelancerId;
import io.github.krloxz.fws.freelancer.domain.FreelancerRepository;
import io.github.krloxz.fws.infra.jooq.tables.records.CommunicationChannelsRecord;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * JOOQ implementation of the {@link FreelancerRepository}.
 *
 * @author Carlos Gomez
 */
@Repository
@Transactional(propagation = Propagation.MANDATORY)
class JooqFreelancerRepository implements FreelancerRepository {

  private final DSLContext create;
  private final FreelancersRecordMapper mapper;

  JooqFreelancerRepository(final DSLContext create, final FreelancersRecordMapper mapper) {
    this.create = create;
    this.mapper = mapper;
  }

  @Override
  public Mono<Void> deleteAll() {
    return Mono.from(this.create.delete(FREELANCERS)).then();
  }

  @Override
  public Mono<Freelancer> save(final Freelancer freelancer) {
    return Flux.from(this.create.insertInto(FREELANCERS).set(this.mapper.toFreelancersRecord(freelancer)))
        .thenMany(this.create.insertInto(ADDRESSES).set(this.mapper.toAddressesRecord(freelancer)))
        .thenMany(insertChannels(freelancer))
        .then(Mono.just(freelancer));
  }

  @Override
  public Mono<Freelancer> update(final Freelancer freelancer) {
    return Flux.from(
        this.create.update(FREELANCERS)
            .set(this.mapper.toFreelancersRecord(freelancer))
            .where(FREELANCERS.ID.eq(freelancer.id().value())))
        .thenMany(this.create.delete(ADDRESSES).where(ADDRESSES.FREELANCER_ID.eq(freelancer.id().value())))
        .thenMany(this.create.insertInto(ADDRESSES).set(this.mapper.toAddressesRecord(freelancer)))
        .thenMany(deleteChannels(freelancer))
        .thenMany(insertChannels(freelancer))
        .then(Mono.just(freelancer));
  }

  @Override
  public Flux<Freelancer> findAll() {
    return Flux.from(
        this.create.select(FREELANCERS.ID).from(FREELANCERS).orderBy(FREELANCERS.FIRST_NAME))
        .<UUID>map(Record1::value1)
        .flatMap(this::findRecordById)
        .map(this.mapper::fromRecords);
  }

  @Override
  public Mono<Freelancer> findById(final FreelancerId id) {
    return findRecordById(id.value())
        .filter(not(List::isEmpty))
        .map(this.mapper::fromRecords);
  }

  private DeleteConditionStep<CommunicationChannelsRecord> deleteChannels(final Freelancer freelancer) {
    return this.create.delete(COMMUNICATION_CHANNELS)
        .where(COMMUNICATION_CHANNELS.FREELANCER_ID.eq(freelancer.id().value()));
  }

  private Batch insertChannels(final Freelancer freelancer) {
    return this.create.batch(
        this.mapper.toCommunicationChannelsRecords(freelancer)
            .stream()
            .map(this.create.insertInto(COMMUNICATION_CHANNELS)::set)
            .toList());
  }

  private Mono<List<Record>> findRecordById(final UUID id) {
    return Flux.from(
        this.create.select(DSL.asterisk())
            .from(FREELANCERS)
            .join(ADDRESSES).onKey()
            .leftJoin(COMMUNICATION_CHANNELS).onKey()
            .where(FREELANCERS.ID.eq(id)))
        .collectList();
  }

}
