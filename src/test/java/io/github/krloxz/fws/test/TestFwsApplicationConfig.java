package io.github.krloxz.fws.test;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;

import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcBuilderCustomizer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.servlet.ResultMatcher;

import com.atlassian.oai.validator.OpenApiInteractionValidator;
import com.atlassian.oai.validator.mockmvc.OpenApiValidationMatchers;
import com.atlassian.oai.validator.whitelist.StatusType;
import com.atlassian.oai.validator.whitelist.ValidationErrorsWhitelist;
import com.atlassian.oai.validator.whitelist.rule.WhitelistRules;

/**
 * Configures beans required for the {@link TestFwsApplication}.
 *
 * @author Carlos Gomez
 */
@TestConfiguration
class TestFwsApplicationConfig {

  @Bean
  MockMvcBuilderCustomizer MockMvcBuilderCustomizer() {
    return builder -> {
      builder.alwaysDo(log()).alwaysExpect(validOpenApiSpec());
    };
  }

  private static ResultMatcher validOpenApiSpec() {
    final var whitelist = ValidationErrorsWhitelist.create()
        .withRule(
            "Ignore request body validation when response isn't successful",
            WhitelistRules.allOf(
                WhitelistRules.responseStatusTypeIs(StatusType.SUCCESS).not(),
                WhitelistRules.anyOf(
                    WhitelistRules.messageHasKey("validation.request.body.schema.type"),
                    WhitelistRules.messageHasKey("validation.request.body.schema.enum"),
                    WhitelistRules.messageHasKey("validation.request.body.schema.required"))));
    final var validator = OpenApiInteractionValidator.createFor("/static/openapi.yaml")
        .withWhitelist(whitelist)
        .build();
    return OpenApiValidationMatchers.openApi().isValid(validator);
  }

}
