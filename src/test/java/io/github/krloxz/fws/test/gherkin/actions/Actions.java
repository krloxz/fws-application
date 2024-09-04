package io.github.krloxz.fws.test.gherkin.actions;

/**
 * A utility class offering convenient static methods to instantiate common action objects.
 *
 * @author Carlos Gomez
 */
public abstract class Actions {

  private Actions() {
    throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
  }

  /**
   * Creates a new {@link VoidAction} instance indicating that the system is ready for the next steps
   * in a test scenario.
   * <p>
   * This method is typically used in "Given" steps to signal that the system has been initialized, or
   * needs no initialization, and is ready for interaction.
   *
   * @return a new instance of {@link VoidAction}
   */
  public static VoidAction systemReady() {
    return new VoidAction();
  }

  /**
   * Creates a new {@link CapturingLastResultAction} instance to capture the result produced by the
   * system.
   * <p>
   * This method is used in "Then" steps to capture the result of the last action recorded in "When"
   * steps.
   *
   * @return a new instance of {@link CapturingLastResultAction}
   */
  public static CapturingLastResultAction result() {
    return new CapturingLastResultAction();
  }

  /**
   * Synonym for {@link #result()}.
   */
  public static CapturingLastResultAction response() {
    return result();
  }

}
