package io.github.krloxz.fws.springframework;

import org.springframework.core.annotation.Order;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.reactive.ReactiveRepresentationModelAssembler;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.HandlerResult;
import org.springframework.web.reactive.HandlerResultHandler;
import org.springframework.web.reactive.result.method.annotation.ResponseBodyResultHandler;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

/**
 * {@link HandlerResultHandler} that applies {@link ResponseBodyResultHandler} when values returned
 * by Spring Controllers have been intercepted by the {@link RepresentationModelBeanPostProcessor}.
 * <p>
 * Without this bean in the Spring container JSON serialization of {@link RepresentationModel}
 * instances won't work properly since all the JSON customizations implemented by Spring HATEOAS
 * will be ignored.
 *
 * @author Carlos Gomez
 */
@Component
@Order(200)
class RepresentationModelResultHandler implements HandlerResultHandler {

  private final ResponseBodyResultHandler responseBodyResultHandler;

  RepresentationModelResultHandler(final ResponseBodyResultHandler responseBodyResultHandler) {
    this.responseBodyResultHandler = responseBodyResultHandler;
  }

  @Override
  public boolean supports(final HandlerResult result) {
    final var generic = result.getReturnTypeSource().getDeclaringClass();
    return ReactiveRepresentationModelAssembler.class.isAssignableFrom(generic);
  }

  @Override
  public Mono<Void> handleResult(final ServerWebExchange exchange, final HandlerResult result) {
    return this.responseBodyResultHandler.handleResult(exchange, result);
  }

}
