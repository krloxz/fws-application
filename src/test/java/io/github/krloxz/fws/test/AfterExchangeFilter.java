package io.github.krloxz.fws.test;

import java.nio.charset.Charset;
import java.util.function.Consumer;

import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.client.reactive.ClientHttpRequestDecorator;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * {@link ExchangeFilterFunction} that captures the {@link ExchangeResult} and executes an action
 * when, and only if, the response body is consumed after the exchange completes.
 * <p>
 * Inspired by this response from Stack Overflow: https://stackoverflow.com/a/70277701
 *
 * @author Carlos Gomez
 */
class AfterExchangeFilter implements ExchangeFilterFunction {

  private final Consumer<ExchangeResult> afterExchangeAction;

  /**
   * Creates a new instance.
   *
   * @param afterExchangeAction
   *        the action to execute after the exchange completes
   */
  AfterExchangeFilter(final Consumer<ExchangeResult> afterExchangeAction) {
    this.afterExchangeAction = afterExchangeAction;
  }

  @Override
  public Mono<ClientResponse> filter(final ClientRequest request, final ExchangeFunction next) {
    final var capturedResponseBody = new StringBuilder();
    final var capturedRequestBody = new StringBuilder();

    return next.exchange(
        ClientRequest.from(request)
            .body(
                (httpRequest, context) -> request.body().insert(
                    new ClientHttpRequestDecorator(httpRequest) {

                      @Override
                      public Mono<Void> writeWith(final Publisher<? extends DataBuffer> body) {
                        return super.writeWith(
                            Flux.from(body).doOnNext(data -> capturedRequestBody.append(readData(data))));
                      }

                    }, context))
            .build())
        .map(
            response -> response.mutate().body(
                data -> data
                    .doOnNext(body -> capturedResponseBody.append(readData(body)))
                    .doOnTerminate(() -> {
                      this.afterExchangeAction.accept(
                          new ExchangeResult(
                              request,
                              capturedRequestBody.toString(),
                              response,
                              capturedResponseBody.toString()));
                    }))
                .build());
  }

  private static String readData(final DataBuffer buffer) {
    final var currentReadPosition = buffer.readPosition();
    final var data = buffer.toString(Charset.defaultCharset());
    buffer.readPosition(currentReadPosition);
    return data;
  }

}
