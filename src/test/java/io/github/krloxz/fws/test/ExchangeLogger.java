package io.github.krloxz.fws.test;

import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * {@link Consumer} that logs an {@link ExchangeResult}.
 *
 * @author Carlos Gomez
 */
class ExchangeLogger implements Consumer<ExchangeResult> {

  private static final Log LOGGER = LogFactory.getLog(ExchangeLogger.class);

  @Override
  public void accept(final ExchangeResult result) {
    LOGGER.info("""


        ClientRequest:
          HTTP Method = %s
          Request URL = %s
           Attributes = %s
              Headers = %s
                 Body = %s

        ClientResponse:
               Status = %s
              Headers = %s
                 Body = %s
        """
        .formatted(
            result.request().method(),
            result.request().url(),
            result.request().attributes(),
            result.request().headers(),
            StringUtils.defaultIfBlank(result.requestBody(), "<empty>"),
            result.response().statusCode(),
            result.response().headers().asHttpHeaders(),
            StringUtils.defaultIfBlank(result.responseBody(), "<empty>")));
  }

}
