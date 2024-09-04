package io.github.krloxz.fws.test.gherkin.actions;

import org.jooq.DSLContext;

/**
 * A generic {@link Action} interface designed for declaring actions that interact with a jOOQ
 * {@link DSLContext} to perform database operations.
 * <p>
 * This interface is intended to be implemented as a lambda expression or method reference,
 * providing a simple and flexible way to define database-related actions.
 *
 * @param <T>
 *        type of the result produced by the action
 * @author Carlos Gomez
 */
@FunctionalInterface
public interface JooqAction<T> extends Action<DSLContext, T> {

  @Override
  default Class<DSLContext> inputType() {
    return DSLContext.class;
  }

}
