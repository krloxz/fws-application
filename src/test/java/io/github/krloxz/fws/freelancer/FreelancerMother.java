package io.github.krloxz.fws.freelancer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import io.github.krloxz.fws.freelancer.application.dtos.AddressDtoBuilder;
import io.github.krloxz.fws.freelancer.application.dtos.CommunicationChannelDto;
import io.github.krloxz.fws.freelancer.application.dtos.CommunicationChannelDtoBuilder;
import io.github.krloxz.fws.freelancer.application.dtos.FreelancerDto;
import io.github.krloxz.fws.freelancer.application.dtos.FreelancerDtoBuilder;
import io.github.krloxz.fws.freelancer.application.dtos.HourlyWageDto;
import io.github.krloxz.fws.freelancer.domain.CommunicationChannel;
import io.github.krloxz.fws.freelancer.domain.Gender;

/**
 * Object Mother that provides canned objects to test Freelancer functionalities.
 *
 * @author Carlos Gomez
 * @see https://martinfowler.com/bliki/ObjectMother.html
 */
abstract class FreelancerMother {

  private FreelancerMother() {
    throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
  }

  static FreelancerDto tonyStark() {
    return new FreelancerDtoBuilder()
        .id("fa8508ed-8b7b-4be7-b372-ac1094c709b5")
        .firstName("Tony")
        .middleName("E")
        .lastName("Stark")
        .gender(Gender.MALE)
        .birthDate(LocalDate.parse("1970-05-29"))
        .address(
            new AddressDtoBuilder()
                .street("10880 Malibu Point")
                .city("Malibu")
                .state("CA")
                .zipCode("90265")
                .country("USA")
                .build())
        .wage(new HourlyWageDto(new BigDecimal("5000000"), "USD"))
        .addNickname("Iron Man")
        .addCommunicationChannels(email("tony@avengers.org"), email("ironman@avengers.org"))
        .build();
  }

  static FreelancerDto steveRogers() {
    return new FreelancerDtoBuilder()
        .firstName("Steve")
        .lastName("Rogers")
        .gender(Gender.MALE)
        .birthDate(LocalDate.parse("1918-07-04"))
        .address(
            new AddressDtoBuilder()
                .street("569 Leaman Place")
                .apartment("Apt 2A")
                .city("Brooklyn Heights")
                .state("NY")
                .zipCode("11201")
                .country("USA")
                .build())
        .wage(new HourlyWageDto(new BigDecimal("500"), "USD"))
        .addNickname("Captain America")
        .addCommunicationChannel(email("cap@avengers.org"))
        .build();
  }

  static FreelancerDto freelancer(final String firstName, final String lastName) {
    return new FreelancerDtoBuilder()
        .firstName(firstName)
        .lastName(lastName)
        .gender(Gender.MALE)
        .birthDate(LocalDate.parse("1970-01-01"))
        .address(
            new AddressDtoBuilder()
                .street("123 Main St")
                .apartment("Apt 1A")
                .city("Anytown")
                .state("NY")
                .zipCode("12345")
                .country("USA")
                .build())
        .wage(new HourlyWageDto(new BigDecimal("30"), "USD"))
        .addNickname(firstName + " Nickname")
        .addCommunicationChannel(email(firstName + "@email.com"))
        .build();
  }

  static CommunicationChannelDto mobile(final String number) {
    return new CommunicationChannelDtoBuilder()
        .value(number)
        .type(CommunicationChannel.Type.MOBILE)
        .build();
  }

  private static CommunicationChannelDto email(final String email) {
    return new CommunicationChannelDtoBuilder()
        .id(UUID.randomUUID().toString())
        .value(email)
        .type(CommunicationChannel.Type.EMAIL)
        .build();
  }

}
