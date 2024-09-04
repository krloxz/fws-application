package io.github.krloxz.fws.test.gherkin.actions;

/**
 * Thrown to indicate that an action has produced an invalid result.
 *
 * @author Carlos Gomez
 */
public class InvalidActionResultException extends RuntimeException {

  private static final long serialVersionUID = -1721768995097280995L;

  /**
   * Constructs a new exception with the specified detail message.
   *
   * @param message
   *        the detail message
   */
  public InvalidActionResultException(final String message) {
    super(message);
  }

}
