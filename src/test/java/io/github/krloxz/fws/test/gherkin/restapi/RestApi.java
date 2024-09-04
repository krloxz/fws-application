package io.github.krloxz.fws.test.gherkin.restapi;

import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Entry point to interact with the REST API of the system using the {@link MockMvc} framework.
 * <p>
 * This class is a thin wrapper around the {@link MockMvc} API, providing a more concise way to
 * perform REST API requests and simplifying exception handling.
 *
 * @author Carlos Gomez
 */
public class RestApi {

  private final MockMvc mvc;
  private final ObjectMapper objectMapper;

  /**
   * Constructs a {@code RestApi} instance with the given {@link MockMvc} and {@link ObjectMapper}.
   *
   * @param mvc
   *        the {@link MockMvc} instance used to perform HTTP requests
   * @param objectMapper
   *        the {@link ObjectMapper} used to serialize request bodies to JSON
   */
  public RestApi(final MockMvc mvc, final ObjectMapper objectMapper) {
    this.mvc = mvc;
    this.objectMapper = objectMapper;
  }

  /**
   * Performs a GET request.
   *
   * @param uri
   *        the target URI, which may include URI template variables
   * @param uriVars
   *        zero or more variables to expand the URI if it contains a template
   * @return an {@link MvcResult} containing the result of the executed request
   */
  public MvcResult get(final String uri, final Object... uriVars) {
    return perform(HttpMethod.GET, uri, uriVars);
  }

  /**
   * Performs a POST request.
   *
   * @param uri
   *        the target URI
   * @param body
   *        the body of the request, which will be serialized to JSON
   * @return an {@link MvcResult} containing the result of the executed request
   */
  public MvcResult post(final String uri, final Object body) {
    return perform(HttpMethod.POST, uri, body);
  }

  /**
   * Performs a PUT request.
   *
   * @param uri
   *        the target URI
   * @param body
   *        the body of the request, which will be serialized to JSON
   * @return an {@link MvcResult} containing the result of the executed request
   */
  public MvcResult put(final String uri, final Object body) {
    return perform(HttpMethod.PUT, uri, body);
  }

  /**
   * Performs a PATCH request.
   *
   * @param uri
   *        the target URI
   * @param body
   *        the body of the request, which will be serialized to JSON
   * @return an {@link MvcResult} containing the result of the executed request
   */
  public MvcResult patch(final String uri, final Object body) {
    return perform(HttpMethod.PATCH, uri, body);
  }

  /**
   * Performs a DELETE request.
   *
   * @param uriTemplate
   *        the target URI, which may include URI template variables
   * @param uriVars
   *        zero or more variables to expand the URI if it contains a template
   * @return an {@link MvcResult} containing the result of the executed request
   */
  public MvcResult delete(final String uriTemplate, final Object... uriVars) {
    return perform(HttpMethod.DELETE, uriTemplate, uriVars);
  }

  private MvcResult perform(final HttpMethod method, final String uriTemplate, final Object... uriVars) {
    try {
      return this.mvc.perform(MockMvcRequestBuilders.request(method, uriTemplate, uriVars)).andReturn();
    } catch (final Exception e) {
      throw new IllegalStateException(
          "An unexpected exception occurred while performing '%s %s'".formatted(method, uriTemplate), e);
    }
  }

  private MvcResult perform(final HttpMethod method, final String uri, final Object body) {
    try {
      return this.mvc.perform(
          MockMvcRequestBuilders.request(method, uri)
              .contentType(MediaType.APPLICATION_JSON)
              .content(this.objectMapper.writeValueAsString(body)))
          .andReturn();
    } catch (final Exception e) {
      throw new IllegalStateException(
          "An unexpected exception occurred while performing '%s %s'".formatted(method, uri), e);
    }
  }

}
