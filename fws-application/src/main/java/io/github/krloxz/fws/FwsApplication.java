package io.github.krloxz.fws;

import static io.github.krloxz.fws.infra.jooq.Tables.ADDRESSES;
import static io.github.krloxz.fws.infra.jooq.Tables.COMMUNICATION_CHANNELS;
import static io.github.krloxz.fws.infra.jooq.Tables.FREELANCERS;
import static java.util.stream.Collectors.joining;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.immutables.value.Value;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.r2dbc.connection.TransactionAwareConnectionFactoryProxy;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.krloxz.fws.infra.jooq.tables.records.AddressesRecord;
import io.github.krloxz.fws.infra.jooq.tables.records.CommunicationChannelsRecord;
import io.github.krloxz.fws.infra.jooq.tables.records.FreelancersRecord;
import io.r2dbc.spi.ConnectionFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class FwsApplication {

  public static void main(final String[] args) {
    SpringApplication.run(FwsApplication.class, args);
  }

  @Bean
  DSLContext dslContext(final ConnectionFactory factory) {
    return DSL.using(new TransactionAwareConnectionFactoryProxy(factory), SQLDialect.H2);
  }

}


@RestController
@RequestMapping("freelancers")
class FreelancerController {

  private final FreelancerRepository repository;

  public FreelancerController(final FreelancerRepository repository) {
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


@Component
class SampleDataInitializer {

  private static final Log LOGGER = LogFactory.getLog(SampleDataInitializer.class);
  private final FreelancerRepository repository;

  SampleDataInitializer(final FreelancerRepository repository) {
    this.repository = repository;
  }

  @EventListener(ApplicationReadyEvent.class)
  @Transactional
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
            CommunicationChannel.of("freelancer@freelancer.com", CommunicationChannelType.EMAIL))
        .build();
  }

}


interface FreelancerRepository {

  Mono<Void> deleteAll();

  Mono<Freelancer> save(Freelancer freelancer);

  Flux<Freelancer> findAll();

}


@Repository
class ReactiveFreelancerRepository implements FreelancerRepository {

  @Autowired
  private FreelancersDao freelancersDao;

  @Autowired
  private AddressesDao addressesDao;

  @Autowired
  private CommunicationChannelsDao channelsDao;

  @Override
  public Mono<Void> deleteAll() {
    return this.freelancersDao.deleteAll();
  }

  @Override
  public Mono<Freelancer> save(final Freelancer freelancer) {
    return this.freelancersDao.save(toFreelancersRecord(freelancer))
        .then(this.addressesDao.save(toAddressesRecord(freelancer)))
        .thenMany(this.channelsDao.saveAll(toCommunicationChannelsRecords(freelancer)))
        .then(Mono.just(freelancer));
  }

  private Freelancers toFreelancersRecord(final Freelancer freelancer) {
    return new Freelancers(
        freelancer.id().value(),
        freelancer.name().first(),
        freelancer.name().last(),
        freelancer.name().middle().orElse(null),
        freelancer.gender().map(Gender::name).orElse(null),
        freelancer.wage().value(),
        freelancer.wage().currency().getCurrencyCode(),
        freelancer.nicknames().stream().collect(joining(",")),
        null);
  }

  private Addresses toAddressesRecord(final Freelancer freelancer) {
    return new Addresses(
        freelancer.id().value(),
        freelancer.address().street(),
        freelancer.address().apartment().orElse(null),
        freelancer.address().city(),
        freelancer.address().state(),
        freelancer.address().country(),
        freelancer.address().zipCode(),
        null);
  }

  private Flux<CommunicationChannels> toCommunicationChannelsRecords(final Freelancer freelancer) {
    return Flux.fromIterable(freelancer.communicationChannels())
        .map(
            channel -> new CommunicationChannels(
                null,
                freelancer.id().value(),
                channel.value(),
                channel.type().name(),
                null));
  }

  @Override
  public Flux<Freelancer> findAll() {
    return Flux.empty();
  }

}


interface FreelancersDao extends ReactiveCrudRepository<Freelancers, UUID> {
}


interface AddressesDao extends ReactiveCrudRepository<Addresses, UUID> {
}


interface CommunicationChannelsDao extends ReactiveCrudRepository<CommunicationChannels, Long> {
}


record Freelancers(
    @Id UUID id,
    String firstName,
    String lastName,
    String middleName,
    String gender,
    BigDecimal hourlyWageValue,
    String hourlyWageCurrency,
    String nicknames,
    @Version Long version) {
}


record Addresses(
    @Id UUID freelancerId,
    String street,
    String apartment,
    String city,
    String state,
    String country,
    String zip_code,
    @Version Long version) {
}


record CommunicationChannels(
    @Id Long id,
    UUID freelancerId,
    String value1,
    String type,
    @Version Long version) {
}


// @Repository
// @Transactional(propagation = Propagation.MANDATORY)
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

  @Override
  public Flux<Freelancer> findAll() {
    return Flux.from(
        this.create.select(FREELANCERS.ID)
            .from(FREELANCERS))
        .log()
        .<UUID>map(Record1::value1)
        .flatMap(this::findFreelancer)
        .map(this::toFreelancer);
  }

  private Mono<List<Record>> findFreelancer(final UUID id) {
    return Flux.from(
        this.create.select(DSL.asterisk())
            .from(FREELANCERS)
            .join(ADDRESSES).onKey()
            .leftJoin(COMMUNICATION_CHANNELS).onKey()
            .where(FREELANCERS.ID.eq(id)))
        .log()
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
        .map(record -> CommunicationChannel.of(record.getValue1(), CommunicationChannelType.valueOf(record.getType())))
        .toList();
  }

}


@Value.Immutable
@Value.Style(depluralize = true)
abstract class Freelancer {

  abstract FreelancerId id();

  abstract PersonName name();

  abstract Optional<Gender> gender();

  // TODO: add speciality and skills

  abstract Address address();

  abstract HourlyWage wage();

  abstract Set<String> nicknames();

  abstract Set<CommunicationChannel> communicationChannels();

  public static ImmutableFreelancer.Builder builder() {
    return ImmutableFreelancer.builder();
  }

  public Freelancer movedTo(final Address newAddress) {
    return null;
  }

  public Freelancer add(final String nickName) {
    return null;
  }

  public Freelancer add(final CommunicationChannel channel) {
    return null;
  }

  public Freelancer remove(final String nickName) {
    return null;
  }

  public Freelancer remove(final CommunicationChannel channel) {
    return null;
  }

  public Freelancer update(final HourlyWage wage) {
    return null;
  }
}


@Value.Immutable
interface FreelancerId {

  @Value.Default
  default UUID value() {
    return UUID.randomUUID();
  }

  static FreelancerId create() {
    return ImmutableFreelancerId.builder().build();
  }

  static FreelancerId of(final UUID id) {
    return ImmutableFreelancerId.builder().value(id).build();
  }
}


@Value.Immutable
interface PersonName {

  String first();

  String last();

  Optional<String> middle();

  static PersonName of(final String first, final String last) {
    return ImmutablePersonName.builder()
        .first(first)
        .last(last)
        .build();
  }

  static PersonName of(final String first, final String last, final String middle) {
    return ImmutablePersonName.builder()
        .first(first)
        .last(last)
        .middle(middle)
        .build();
  }
}


enum Gender {
  MALE, FEMALE
}


@Value.Immutable
interface Address {

  String street();

  Optional<String> apartment();

  String city();

  String state();

  @Value.Default
  default String country() {
    return "USA";
  }

  String zipCode();

  static ImmutableAddress.Builder builder() {
    return ImmutableAddress.builder();
  }
}


@Value.Immutable
interface CommunicationChannel {

  String value();

  CommunicationChannelType type();

  static CommunicationChannel of(final String value, final CommunicationChannelType type) {
    return ImmutableCommunicationChannel.builder()
        .value(value)
        .type(type)
        .build();
  }
}


enum CommunicationChannelType {
  MOBILE, PHONE, EMAIL, URL;
}


@Value.Immutable
interface HourlyWage {

  BigDecimal value();

  Currency currency();

  static HourlyWage of(final String value, final String currency) {
    return ImmutableHourlyWage.builder()
        .value(new BigDecimal(value))
        .currency(Currency.getInstance(currency))
        .build();
  }
}
