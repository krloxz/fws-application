package io.github.krloxz.fws.freelancer.infra;

import static io.github.krloxz.fws.infra.jooq.Tables.ADDRESSES;
import static io.github.krloxz.fws.infra.jooq.Tables.COMMUNICATION_CHANNELS;
import static io.github.krloxz.fws.infra.jooq.Tables.FREELANCERS;

import java.util.List;
import java.util.UUID;

import org.jooq.Batch;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import io.github.krloxz.fws.freelancer.domain.Freelancer;
import io.github.krloxz.fws.freelancer.domain.FreelancerId;
import io.github.krloxz.fws.freelancer.domain.FreelancerRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
  public Flux<Freelancer> findAll() {
    return Flux.from(
        this.create.select(FREELANCERS.ID).from(FREELANCERS).orderBy(FREELANCERS.FIRST_NAME))
        .<UUID>map(Record1::value1)
        .flatMap(this::findRecordById)
        .map(this.mapper::fromRecords);
  }

  @Override
  public Mono<Freelancer> findById(final FreelancerId id) {
    return findRecordById(id.value()).map(this.mapper::fromRecords);
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
