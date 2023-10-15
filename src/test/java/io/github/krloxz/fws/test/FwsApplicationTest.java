package io.github.krloxz.fws.test;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

/**
 * Indicates that a class implements test cases for the FwsApplication and ensures that the Spring
 * context contains a {@link TestFwsApplication} ready for use.
 *
 * @author Carlos Gomez
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@SpringBootTest
@AutoConfigureWebTestClient
@ExtendWith(DatabaseCleaner.class)
@Import(TestFwsApplicationConfig.class)
public @interface FwsApplicationTest {

}