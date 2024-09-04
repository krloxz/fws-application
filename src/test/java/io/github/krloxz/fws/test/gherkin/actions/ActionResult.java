package io.github.krloxz.fws.test.gherkin.actions;

import java.util.Objects;
import java.util.function.Consumer;

import io.github.krloxz.fws.test.gherkin.ThenStep;
import jakarta.annotation.Nullable;

/**
 * Encapsulates the result of an action execution, including the result value and an optional
 * validation function.
 * <p>
 * This class serves as a container for the outcome of an action, allowing the result to be
 * validated against custom logic provided by the action itself.
 *
 * @param <T>
 *        the type of the result value produced by an action
 * @author Carlos Gomez
 * @see Action
 */
public class ActionResult<T> {

  private final T value;
  private final Consumer<T> validationFunction;

  /**
   * Constructs a new {@code ActionResult} with the specified value and validation function.
   * <p>
   * The validation function is intended to be executed when the {@link #validate()} method is called,
   * allowing the result value to be checked for correctness. If no validation is required, the
   * validation function can be a no-op.
   *
   * @param value
   *        the result value produced by an action; may be {@code null} if the action does not produce
   *        a result
   * @param validationFunction
   *        a {@link Consumer} that performs validation on the result value; if the result is invalid,
   *        the consumer should throw an {@link InvalidActionResultException}
   * @throws NullPointerException
   *         if the {@code validationFunction} is {@code null}
   */
  ActionResult(final T value, final Consumer<T> validationFunction) {
    this.value = value;
    this.validationFunction = Objects.requireNonNull(validationFunction);
  }

  /**
   * Returns the result value produced by the action.
   * <p>
   * The result value may be used in {@link ThenStep}s to validate assertions.
   *
   * @return the result value; may be {@code null}
   */
  @Nullable
  public T getValue() {
    return this.value;
  }

  /**
   * Validates the result value using the provided validation function.
   *
   * @return this {@code ActionResult} instance, for method chaining
   * @throws InvalidActionResultException
   *         if the result value is determined to be invalid by the validation function
   */
  public ActionResult<T> validate() throws InvalidActionResultException {
    this.validationFunction.accept(this.value);
    return this;
  }

}
