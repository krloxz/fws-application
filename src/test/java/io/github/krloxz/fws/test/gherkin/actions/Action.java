package io.github.krloxz.fws.test.gherkin.actions;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

import jakarta.annotation.Nullable;

/**
 * Represents an action that is executed to implement a step in a Gherkin scenario.
 * <p>
 * Actions serve as the backbone of the Gherkin framework by providing the logic for initializing
 * the system in "Given" steps, performing interactions in "When" steps, and exposing the system
 * state for "Then" steps. They are tightly integrated with the Spring framework, allowing actions
 * to access any bean within the application context. To execute an action, the framework injects
 * the required bean based on the input type specified by the action.
 * <p>
 * Actions are designed to be implemented as lambda expressions, offering a concise and functional
 * approach to defining Gherkin step logic. To facilitate this, generic actions should be declared
 * as functional interfaces that extend the {@code Action} interface. These interfaces should
 * override the {@link #inputType()} and {@link #validateResult(Object)} methods, while the
 * {@link #perform(Object)} method remains the functional interface method to be implemented.
 *
 * @param <T>
 *        Type of the input value, which must correspond to the class of a Spring bean from the
 *        application context. If no input is required, {@link Void} can be used as a placeholder.
 * @param <R>
 *        Type of the result value produced by the action. If no result is expected, {@link Void}
 *        can be used as a placeholder.
 * @author Carlos Gomez
 * @see ApplicationContext
 */
public interface Action<T, R> {

  /**
   * Executes this action within the provided Spring application context.
   * <p>
   * This method serves as a convenience method to simplify action execution. It should not be
   * overridden in subclasses; instead, subclasses should override {@link #perform(Object)} to provide
   * the specific action logic. The {@code execute} method retrieves the necessary input bean from the
   * application context, performs the action, and wraps the result in an {@link ActionResult}, which
   * also includes any validation logic provided by {@link #validateResult(Object)}.
   *
   * @param context
   *        the Spring application context from which the input bean is retrieved
   * @return the result of {@link #perform(Object)} wrapped in an {@link ActionResult}
   * @throws BeansException
   *         if a bean of the specified input type is not found in the application context
   * @see ApplicationContext#getBean(Class)
   * @see ActionResult
   */
  default ActionResult<R> execute(final ApplicationContext context) {
    final var result = perform(inputValue(context));
    return new ActionResult<>(result, this::validateResult);
  }

  /**
   * Performs the core logic of this action using the provided input bean.
   * <p>
   * This method should be overridden by the final implementations of this interface to define the
   * specific logic that this action will perform.
   *
   * @param input
   *        a bean of the type specified by {@link #inputType()}, retrieved from the application
   *        context
   * @return the result of performing the action, can be {@code null}
   */
  @Nullable
  R perform(T input);

  /**
   * Returns the type of the input value required by this action.
   * <p>
   * This method defines the expected type of the input bean that will be retrieved from the Spring
   * application context. If no input is required, {@link Void} can be used as a placeholder.
   *
   * @return the class of the input bean required by this action
   */
  Class<T> inputType();

  /**
   * Validates the result produced by this action.
   * <p>
   * This method provides an optional mechanism to validate the result of the action after it has been
   * performed. Implementations can override this method to provide specific validation logic. If the
   * result is invalid according to the implemented validation logic, an
   * {@link InvalidActionResultException} should be thrown. If no validation is required, this method
   * can be left as is, performing no operations.
   *
   * @param result
   *        the result produced by {@link #perform(Object)}, to be validated; can be {@code null}
   * @throws InvalidActionResultException
   *         if the result is deemed invalid based on the implemented validation logic
   */
  default void validateResult(@Nullable final R result) throws InvalidActionResultException {
    // No-op by default; override to provide validation logic
  }

  @SuppressWarnings("unchecked")
  private T inputValue(final ApplicationContext context) {
    if (Void.class.equals(inputType())) {
      return null;
    }
    if (ApplicationContext.class.equals(inputType())) {
      return (T) context;
    }
    return context.getBean(inputType());
  }

}
