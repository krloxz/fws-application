package io.github.krloxz.fws.test;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.modulith.test.ApplicationModuleTest;

import io.github.krloxz.fws.test.gherkin.GherkinConfig;

/**
 * Indicates that a class implements tests for the FwsApplication and ensures that the application
 * is properly configured for testing.
 *
 * @author Carlos Gomez
 */
@Inherited
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest
@AutoConfigureMockMvc
@Import({FwsApplicationTestConfig.class, GherkinConfig.class})
@ExtendWith({DatabaseCleaner.class, PublishedEventsActionExtension.class})
@ApplicationModuleTest(verifyAutomatically = false, extraIncludes = "test")
public @interface FwsApplicationTest {

}
