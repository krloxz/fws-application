package io.github.krloxz.fws.core;

/**
 * Exception thrown when a domain rule is violated.
 *
 * @author Carlos Gomez
 */
public class DomainException extends RuntimeException {

  private static final long serialVersionUID = -2526715680267106761L;

  public DomainException(final String message, final Object... args) {
    super(message.formatted(args));
  }

  public DomainException(final String message, final Throwable cause, final Object... args) {
    super(message.formatted(args), cause);
  }

}
