package io.github.krloxz.fws.test.gherkin.actions;

/**
 * A specialized {@link VoidAction} used exclusively in "Then" steps to capture the result of the
 * last action executed during the "When" steps of a Gherkin scenario.
 * <p>
 * The {@code CapturingLastResultAction} acts as a marker, signifying that the outcome of the most
 * recent action should be captured and validated. This action is not meant to be executed directly,
 * as it serves only as a placeholder in the test flow. Attempting to execute this action will
 * result in an {@link UnsupportedOperationException}.
 * <p>
 * <b>Usage Restrictions:</b> This class should only be used in "Then" steps and is not appropriate
 * for "Given" or "When" steps, where actions are intended to set up or interact with the system
 * under test.
 *
 * @author Carlos Gomez
 */
public final class CapturingLastResultAction extends VoidAction {

  @Override
  public Void perform(final Void input) {
    throw new UnsupportedOperationException("CapturingLastResultAction cannot be executed");
  }

}
