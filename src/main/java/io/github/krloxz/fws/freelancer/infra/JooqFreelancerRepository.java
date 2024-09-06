package io.github.krloxz.fws.freelancer.infra;

import static io.github.krloxz.fws.infra.jooq.freelancer.Tables.ADDRESSES;
import static io.github.krloxz.fws.infra.jooq.freelancer.Tables.COMMUNICATION_CHANNELS;
import static io.github.krloxz.fws.infra.jooq.freelancer.Tables.FREELANCERS;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import io.github.krloxz.fws.freelancer.domain.Freelancer;
import io.github.krloxz.fws.freelancer.domain.FreelancerRepository;
import io.github.krloxz.fws.freelancer.domain.PageSpec;

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
  public void deleteAll() {
    this.create.delete(FREELANCERS).execute();
  }

  @Override
  public Freelancer save(final Freelancer freelancer) {
    this.create.insertInto(FREELANCERS)
        .set(this.mapper.toFreelancersRecord(freelancer))
        .execute();
    this.create.insertInto(ADDRESSES)
        .set(this.mapper.toAddressesRecord(freelancer))
        .execute();
    this.mapper.toCommunicationChannelsRecords(freelancer)
        .forEach(channel -> this.create.insertInto(COMMUNICATION_CHANNELS).set(channel).execute());
    return freelancer;
  }

  @Override
  public Freelancer update(final Freelancer freelancer) {
    this.create.update(FREELANCERS)
        .set(this.mapper.toFreelancersRecord(freelancer))
        .where(FREELANCERS.ID.eq(freelancer.id()))
        .execute();
    this.create.delete(ADDRESSES)
        .where(ADDRESSES.FREELANCER_ID.eq(freelancer.id()))
        .execute();
    this.create.insertInto(ADDRESSES)
        .set(this.mapper.toAddressesRecord(freelancer))
        .execute();
    this.create.delete(COMMUNICATION_CHANNELS)
        .where(COMMUNICATION_CHANNELS.FREELANCER_ID.eq(freelancer.id()))
        .execute();
    this.mapper.toCommunicationChannelsRecords(freelancer)
        .forEach(channel -> this.create.insertInto(COMMUNICATION_CHANNELS).set(channel).execute());
    return freelancer;
  }

  @Override
  public List<Freelancer> findAllBy(final PageSpec pageSpec) {
    return this.create.select(FREELANCERS.ID)
        .from(FREELANCERS)
        .orderBy(FREELANCERS.LAST_NAME)
        .offset(pageSpec.number() * pageSpec.size())
        .limit(pageSpec.size())
        .fetch()
        .map(Record1::value1)
        .stream()
        .map(this::findRecordById)
        .flatMap(Optional::stream)
        .map(this.mapper::fromRecords)
        .collect(Collectors.toList());
  }

  @Override
  public Optional<Freelancer> findById(final UUID id) {
    return findRecordById(id).map(this.mapper::fromRecords);
  }

  @Override
  public int count() {
    return this.create.selectCount().from(FREELANCERS).fetchOne().value1();
  }

  private Optional<List<Record>> findRecordById(final UUID id) {
    final var records = this.create.select(DSL.asterisk())
        .from(FREELANCERS)
        .join(ADDRESSES).onKey()
        .leftJoin(COMMUNICATION_CHANNELS).onKey()
        .where(FREELANCERS.ID.eq(id))
        .fetch();
    return records.isEmpty() ? Optional.empty() : Optional.of(records);
  }

}
