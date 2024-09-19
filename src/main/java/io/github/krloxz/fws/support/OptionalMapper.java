package io.github.krloxz.fws.support;

import java.util.Optional;

import org.springframework.stereotype.Component;

/**
 * Maps between {@link Optional} and its wrapped value.
 * <p>
 * This class is intended to be used, mainly, by MapStruct mappers.
 *
 * @author Carlos Gomez
 */
@Component
public class OptionalMapper {

  public <T> T unwrap(final Optional<T> value) {
    return value.orElse(null);
  }

  public <T> Optional<T> wrap(final T value) {
    return Optional.ofNullable(value);
  }

}
