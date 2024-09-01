package io.github.krloxz.fws.test.dsl;

import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Entry point to test the REST API of the FwsApplication using the {@link MockMvc} framework.
 *
 * @author Carlos Gomez
 */
public class RestApi {

  private final MockMvc mvc;
  private final ObjectMapper objectMapper;

  RestApi(final MockMvc mvc, final ObjectMapper objectMapper) {
    this.mvc = mvc;
    this.objectMapper = objectMapper;
  }

  /**
   * Performs a GET request.
   *
   * @param uri
   *        the target URI, which may be a URI template
   * @param uriVars
   *        zero or more variables to expand the URI when it is a template
   * @return a {@link ResultAssertions} instance to validate the result of this request
   */
  public ResultAssertions get(final String uri, final Object... uriVars) {
    return perform(HttpMethod.GET, uri, uriVars);
  }

  /**
   * Performs a POST request.
   *
   * @param uri
   *        the target URI
   * @param body
   *        the body of the request
   * @return a {@link ResultAssertions} instance to validate the result of this request
   */
  public ResultAssertions post(final String uri, final Object body) {
    return perform(HttpMethod.POST, uri, body);
  }

  /**
   * Performs a PUT request.
   *
   * @param uri
   *        the target URI
   * @param body
   *        the body of the request
   * @return a {@link ResultAssertions} instance to validate the result of this request
   */
  public ResultAssertions put(final String uri, final Object body) {
    return perform(HttpMethod.PUT, uri, body);
  }

  /**
   * Performs a PATCH request.
   *
   * @param uri
   *        the target URI
   * @param body
   *        the body of the request
   * @return a {@link ResultAssertions} instance to validate the result of this request
   */
  public ResultAssertions patch(final String uri, final Object body) {
    return perform(HttpMethod.PATCH, uri, body);
  }

  /**
   * Performs a DELETE request.
   *
   * @param uriTemplate
   *        the target URI, which may be a URI template
   * @param uriVars
   *        zero or more variables to expand the URI when it is a template
   * @return a {@link ResultAssertions} instance to validate the result of this request
   */
  public ResultAssertions delete(final String uriTemplate, final Object... uriVars) {
    return perform(HttpMethod.DELETE, uriTemplate, uriVars);
  }

  private ResultAssertions perform(final HttpMethod method, final String uriTemplate, final Object... uriVars) {
    try {
      final var result = this.mvc.perform(MockMvcRequestBuilders.request(method, uriTemplate, uriVars));
      return new ResultAssertions(result);
    } catch (final Exception e) {
      throw new IllegalStateException(
          "An unexpected exception occurred while performing %s %s".formatted(method, uriTemplate), e);
    }
  }

  private ResultAssertions perform(final HttpMethod method, final String uri, final Object body) {
    try {
      final var result = this.mvc.perform(
          MockMvcRequestBuilders.request(method, uri)
              .contentType(MediaType.APPLICATION_JSON)
              .content(this.objectMapper.writeValueAsString(body)));
      return new ResultAssertions(result);
    } catch (final Exception e) {
      throw new IllegalStateException(
          "An unexpected exception occurred while performing %s %s".formatted(method, uri), e);
    }
  }

}
