package io.github.krloxz.fws.test.gherkin.actions;

/**
 * An {@link Action} that does not require any input or produce any result.
 *
 * @author Carlos Gomez
 */
public class VoidAction implements Action<Void, Void> {

  @Override
  public Void perform(final Void input) {
    return null;
  }

  @Override
  public final Class<Void> inputType() {
    return Void.class;
  }

}
