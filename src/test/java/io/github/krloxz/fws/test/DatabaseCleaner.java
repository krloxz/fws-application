package io.github.krloxz.fws.test;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * JUnit extension to clean up the database before each test.
 *
 * @author Carlos Gomez
 */
public class DatabaseCleaner implements BeforeAllCallback, BeforeEachCallback {

  private boolean isFirstTest;

  @Override
  public void beforeAll(final ExtensionContext context) throws Exception {
    this.isFirstTest = true;
  }

  @Override
  public void beforeEach(final ExtensionContext context) throws Exception {
    if (this.isFirstTest) {
      this.isFirstTest = false;
    } else {
      final var flyway = SpringExtension.getApplicationContext(context).getBean(Flyway.class);
      flyway.clean();
      flyway.migrate();
    }
  }

}
