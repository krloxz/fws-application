package io.github.krloxz.fws.freelancer.infra;

import static io.github.krloxz.fws.freelancer.infra.jooq.Tables.ADDRESSES;
import static io.github.krloxz.fws.freelancer.infra.jooq.Tables.COMMUNICATION_CHANNELS;
import static io.github.krloxz.fws.freelancer.infra.jooq.Tables.FREELANCERS;
import static java.util.stream.Collectors.joining;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.jooq.Record;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import io.github.krloxz.fws.freelancer.domain.CommunicationChannel;
import io.github.krloxz.fws.freelancer.domain.Freelancer;
import io.github.krloxz.fws.freelancer.domain.Gender;
import io.github.krloxz.fws.freelancer.infra.jooq.tables.records.AddressesRecord;
import io.github.krloxz.fws.freelancer.infra.jooq.tables.records.CommunicationChannelsRecord;
import io.github.krloxz.fws.freelancer.infra.jooq.tables.records.FreelancersRecord;

/**
 * Maps {@link Freelancer}'s between the JOOQ record and domain models.
 * <p>
 * Use public methods only, protected methods are designed only to support MapStruct.
 *
 * @author Carlos Gomez
 */
@Mapper(componentModel = "spring")
abstract class FreelancersRecordMapper {

  @Mapping(target = "firstName", source = "name.first")
  @Mapping(target = "lastName", source = "name.last")
  @Mapping(target = "middleName", source = "name.middle")
  @Mapping(target = "hourlyWageAmount", source = "wage.amount")
  @Mapping(target = "hourlyWageCurrency", source = "wage.currency")
  public abstract FreelancersRecord toFreelancersRecord(Freelancer freelancer);

  @Mapping(target = "freelancerId", source = "id")
  @Mapping(target = ".", source = "address")
  public abstract AddressesRecord toAddressesRecord(Freelancer freelancer);

  public List<CommunicationChannelsRecord> toCommunicationChannelsRecords(final Freelancer freelancer) {
    return freelancer.communicationChannels()
        .stream()
        .map(channel -> toCommunicationChannelsRecord(channel, freelancer.id()))
        .toList();
  }

  public Freelancer fromRecords(final List<Record> records) {
    final var freelancersRecord = records.get(0).into(FREELANCERS);
    final var addressesRecord = records.get(0).into(ADDRESSES);
    final var channelsRecords = records.stream()
        .map(record -> record.into(COMMUNICATION_CHANNELS))
        .filter(record -> Objects.nonNull(record.getId()))
        .toList();
    return fromRecords(freelancersRecord, addressesRecord, channelsRecords);
  }

  @Mapping(target = "freelancerId", source = "id")
  @Mapping(target = "value_", source = "channel.value")
  abstract CommunicationChannelsRecord toCommunicationChannelsRecord(CommunicationChannel channel, UUID id);

  String toFreelancersRecordNicknames(final Set<String> value) {
    return value.stream().collect(joining(","));
  }

  String toFreelancersRecordGender(final Optional<Gender> value) {
    return value.map(Gender::name).orElse(null);
  }

  <T> T unwrap(final Optional<T> value) {
    return value.orElse(null);
  }

  @Mapping(target = "name.first", source = "freelancersRecord.firstName")
  @Mapping(target = "name.last", source = "freelancersRecord.lastName")
  @Mapping(target = "name.middle", source = "freelancersRecord.middleName")
  @Mapping(target = "wage.amount", source = "freelancersRecord.hourlyWageAmount")
  @Mapping(target = "wage.currency", source = "freelancersRecord.hourlyWageCurrency")
  @Mapping(target = "address", source = "addressesRecord")
  @Mapping(target = "communicationChannels", source = "channelsRecords")
  abstract Freelancer fromRecords(
      FreelancersRecord freelancersRecord,
      AddressesRecord addressesRecord,
      List<CommunicationChannelsRecord> channelsRecords);

  @Mapping(target = "value", source = "value_")
  abstract CommunicationChannel fromRecord(CommunicationChannelsRecord record);

  Set<String> fromFreelancersRecordNicknames(final String value) {
    return Set.of(value.split(","));
  }

  Optional<Gender> fromFreelancersRecordGender(final String value) {
    return Optional.ofNullable(value).map(Gender::valueOf);
  }

  Optional<String> wrap(final String value) {
    return Optional.ofNullable(value);
  }

}
