package br.com.blz.testjava.api.controller

import br.com.blz.testjava.TestjavaApplication
import br.com.blz.testjava.api.exceptionhandler.ProblemResponse
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.reactive.server.EntityExchangeResult
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.util.UriBuilder
import java.net.URI
import java.util.function.Function

@ActiveProfiles("test")
@ContextConfiguration(classes = [TestjavaApplication::class])
abstract class AbstractControllerTestWithoutBody(protected var baseUri: String) {

  @Autowired
  protected var applicationContext: ApplicationContext? = null

  protected var webTestClient: WebTestClient? = null

  protected var httpStatus: HttpStatus? = null

  protected var result: EntityExchangeResult<Void>? = null

  protected var resultError: EntityExchangeResult<ProblemResponse>? = null

  @BeforeEach
  open fun setup() {
    webTestClient = WebTestClient
      .bindToApplicationContext(applicationContext!!)
      .configureClient()
      .baseUrl(baseUri)
      .build()
  }

  protected open fun doPost(body: Any, uriFunction: Function<UriBuilder, URI>) {
    result = webTestClient!!.post()
      .uri(uriFunction)
      .bodyValue(body)
      .accept(MediaType.APPLICATION_JSON)
      .exchange()
      .expectBody()
      .isEmpty
    httpStatus = result!!.status
  }

  protected open fun doPostWithError(body: Any, uriFunction: Function<UriBuilder, URI>): ProblemResponse? {
    resultError = webTestClient!!.post()
      .uri(uriFunction)
      .bodyValue(body)
      .accept(MediaType.APPLICATION_JSON)
      .exchange()
      .expectBody(ProblemResponse::class.java)
      .returnResult()
    httpStatus = resultError!!.status
    return resultError!!.responseBody
  }

  protected open fun doPut(uriFunction: Function<UriBuilder, URI>) {
    result = webTestClient!!.put()
      .uri(uriFunction)
      .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
      .accept(MediaType.APPLICATION_JSON)
      .exchange()
      .expectBody()
      .isEmpty
    httpStatus = result!!.status
  }

  protected open fun doPut(body: Any, uriFunction: Function<UriBuilder, URI>) {
    result = webTestClient!!.put()
      .uri(uriFunction)
      .bodyValue(body)
      .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
      .accept(MediaType.APPLICATION_JSON)
      .exchange()
      .expectBody()
      .isEmpty
    httpStatus = result!!.status
  }


  protected open fun doPutWithError(body: Any, uriFunction: Function<UriBuilder, URI>): ProblemResponse? {
    resultError = webTestClient!!.put()
      .uri(uriFunction)
      .bodyValue(body)
      .accept(MediaType.APPLICATION_JSON)
      .exchange()
      .expectBody(ProblemResponse::class.java)
      .returnResult()
    httpStatus = resultError!!.status
    return resultError!!.responseBody
  }

  protected open fun doPutWithError(uriFunction: Function<UriBuilder, URI>): ProblemResponse? {
    resultError = webTestClient!!.put()
      .uri(uriFunction)
      .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
      .accept(MediaType.APPLICATION_JSON)
      .exchange()
      .expectBody(ProblemResponse::class.java)
      .returnResult()
    httpStatus = resultError!!.status
    return resultError!!.responseBody
  }

  protected open fun doDelete(uriFunction: Function<UriBuilder, URI>) {
    result = webTestClient!!.delete()
      .uri(uriFunction!!)
      .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
      .accept(MediaType.APPLICATION_JSON)
      .exchange()
      .expectBody()
      .isEmpty
    httpStatus = result!!.status
  }

  protected open fun doDeleteWithError(uriFunction: Function<UriBuilder, URI>): ProblemResponse? {
    resultError = webTestClient!!.delete()
      .uri(uriFunction)
      .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
      .accept(MediaType.APPLICATION_JSON)
      .exchange()
      .expectBody(ProblemResponse::class.java)
      .returnResult()
    httpStatus = resultError!!.status
    return resultError!!.responseBody
  }

  protected open fun assertHttpStatusIsCreated() = Assertions.assertThat(httpStatus).isEqualTo(HttpStatus.CREATED)

  protected open fun assertHttpStatusIsOk() = Assertions.assertThat(httpStatus).isEqualTo(HttpStatus.OK)

  protected open fun assertHttpStatusIsNotFound() = Assertions.assertThat(httpStatus).isEqualTo(HttpStatus.NOT_FOUND)

  protected open fun assertHttpStatusIsAccepted() = Assertions.assertThat(httpStatus).isEqualTo(HttpStatus.ACCEPTED)

  protected open fun assertHttpStatusIsNoContent() = Assertions.assertThat(httpStatus).isEqualTo(HttpStatus.NO_CONTENT)

  protected open fun assertHttpStatusIsBadRequest() = Assertions.assertThat(httpStatus).isEqualTo(HttpStatus.BAD_REQUEST)

  protected open fun assertHttpStatusIsConflict() = Assertions.assertThat(httpStatus).isEqualTo(HttpStatus.CONFLICT)

}
