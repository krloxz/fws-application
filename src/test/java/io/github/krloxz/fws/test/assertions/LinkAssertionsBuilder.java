package io.github.krloxz.fws.test.assertions;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.web.util.UriComponentsBuilder;

import io.github.krloxz.fws.support.AffordanceLink;

/**
 * Builder of assertions for {@link AffordanceLink affordance links} contained in the response of an
 * executed request.
 * <p>
 * Use {@link Assertions} to create instances of this class.
 *
 * @author Carlos Gomez
 */
public class LinkAssertionsBuilder {

  private final String relation;
  private final String prefix;

  /**
   * @see Assertions#link(String)
   */
  LinkAssertionsBuilder(final String relation) {
    this(relation, "");
  }

  /**
   * Starts the creation of an assertion that will verify the existence of an affordance link using
   * the JSON path {@code <prefix>_links.<relation>}.
   *
   * @param relation
   *        the link relation
   * @param prefix
   *        the JSON path prefix
   */
  LinkAssertionsBuilder(final String relation, final String prefix) {
    this.relation = relation;
    this.prefix = prefix;
  }

  /**
   * Asserts that the response contains an {@link AffordanceLink affordance link} with the given URL
   * path assuming {@code http://localhost} as the base URL. Additionally, the link is expected to
   * have a GET method when the relation is IANA-based and a POST one otherwise.
   *
   * @param urlPath
   *        the URL path, which may include template variables
   * @param urlVariables
   *        zero or more variables to expand the URL path when it contains a template
   * @return a {@link ResultMatcher} implementing the assertion
   */
  public ResultMatcher withPath(final String urlPath, final Object... urlVariables) {
    return mvcResult -> {
      jsonPath("%s_links.%s.href", this.prefix, this.relation)
          .value(toUrl(urlPath, urlVariables))
          .match(mvcResult);
      jsonPath("%s_links.%s.method", this.prefix, this.relation)
          .value(IanaLinkRelations.isIanaRel(this.relation) ? "GET" : "POST")
          .match(mvcResult);

    };
  }

  private String toUrl(final String urlPath, final Object... urlVariables) {
    return UriComponentsBuilder.fromUriString("http://localhost" + urlPath)
        .buildAndExpand(unwrapOptionals(urlVariables))
        .encode()
        .toUriString();
  }

  private Object[] unwrapOptionals(final Object... urlVariables) {
    return Stream.of(urlVariables)
        .map(this::unwrapOptional)
        .toArray();
  }

  private Object unwrapOptional(final Object object) {
    if (object instanceof final Optional optional) {
      return optional.orElseThrow();
    }
    return object;
  }

}
