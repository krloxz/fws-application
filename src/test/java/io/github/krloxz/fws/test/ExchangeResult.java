package io.github.krloxz.fws.test;

import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;

/**
 * A record of the request, response and their bodies captured after an exchange operation
 * completes.
 *
 * @author Carlos Gomez
 */
record ExchangeResult(
    ClientRequest request,
    String requestBody,
    ClientResponse response,
    String responseBody) {

}
