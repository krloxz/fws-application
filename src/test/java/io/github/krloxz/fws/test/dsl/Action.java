package io.github.krloxz.fws.test.dsl;

/**
 * @author Carlos Gomez
 */
@FunctionalInterface
public interface Action {

  ResultAssertions execute(RestApi restApi);

  public static class NoOpAction implements Action {

    @Override
    public ResultAssertions execute(final RestApi restApi) {
      throw new UnsupportedOperationException("No operation action");
    }

  }

}
