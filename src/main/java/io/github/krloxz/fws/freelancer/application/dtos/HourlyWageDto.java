package io.github.krloxz.fws.freelancer.application.dtos;

import java.math.BigDecimal;

import io.github.krloxz.fws.freelancer.domain.HourlyWage;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * DTO to carry an {@link HourlyWage}.
 *
 * @author Carlos Gomez
 */
public record HourlyWageDto(
    @NotNull @PositiveOrZero @Digits(integer = 7, fraction = 2) BigDecimal amount,
    @NotBlank String currency) {

}
