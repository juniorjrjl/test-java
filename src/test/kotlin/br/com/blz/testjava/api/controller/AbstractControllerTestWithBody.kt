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
abstract class AbstractControllerTestWithBody<R>(private var baseUri: String, var responseType: Class<R>) {

  @Autowired
  protected lateinit var applicationContext: ApplicationContext

  protected var webTestClient: WebTestClient? = null

  protected var httpStatus: HttpStatus? = null

  protected var result: EntityExchangeResult<R>? = null

  protected var resultList: EntityExchangeResult<List<R>>? = null

  protected var resultError: EntityExchangeResult<ProblemResponse>? = null

  @BeforeEach
  fun setup(){
    webTestClient = WebTestClient.bindToApplicationContext(applicationContext)
      .configureClient()
      .baseUrl(baseUri)
      .build()
  }

  protected open fun doPut(body: Any, uriFunction: Function<UriBuilder, URI?>): R {
    result = webTestClient!!.put()
      .uri(uriFunction)
      .bodyValue(body)
      .accept(MediaType.APPLICATION_JSON)
      .exchange()
      .expectBody(responseType)
      .returnResult()
    httpStatus = result!!.status
    return result!!.responseBody!!
  }

  protected open fun doPutWithError(body: Any, uriFunction: Function<UriBuilder, URI>): ProblemResponse {
    resultError = webTestClient!!.put()
      .uri(uriFunction)
      .bodyValue(body)
      .accept(MediaType.APPLICATION_JSON)
      .exchange()
      .expectBody(ProblemResponse::class.java)
      .returnResult()
    httpStatus = resultError!!.status
    return resultError!!.responseBody!!
  }

  protected open fun doPost(body: Any, uriFunction: Function<UriBuilder, URI>): R {
    result = webTestClient!!.post()
      .uri(uriFunction)
      .bodyValue(body)
      .accept(MediaType.APPLICATION_JSON)
      .exchange()
      .expectBody(responseType)
      .returnResult()
    httpStatus = result!!.status
    return result!!.responseBody!!
  }

  protected open fun doPostList(body: Any, uriFunction: Function<UriBuilder, URI>): List<R> {
    resultList = webTestClient!!.post()
      .uri(uriFunction)
      .bodyValue(body)
      .accept(MediaType.APPLICATION_JSON)
      .exchange()
      .expectBodyList(responseType)
      .returnResult()
    httpStatus = resultList!!.status
    return resultList!!.responseBody!!
  }

  protected open fun doPostWithError(body: Any, uriFunction: Function<UriBuilder, URI>): ProblemResponse {
    resultError = webTestClient!!.post()
      .uri(uriFunction)
      .bodyValue(body)
      .accept(MediaType.APPLICATION_JSON)
      .exchange()
      .expectBody(ProblemResponse::class.java)
      .returnResult()
    httpStatus = resultError!!.status
    return resultError!!.responseBody!!
  }

  protected open fun doGet(uriFunction: Function<UriBuilder, URI>): R {
    result = webTestClient!!.get()
      .uri(uriFunction)
      .accept(MediaType.APPLICATION_JSON)
      .exchange()
      .expectBody(responseType)
      .returnResult()
    httpStatus = result!!.status
    return result!!.responseBody!!
  }

  protected open fun doGetWithError(uriFunction: Function<UriBuilder, URI>): ProblemResponse {
    resultError = webTestClient!!.get()
      .uri(uriFunction)
      .accept(MediaType.APPLICATION_JSON)
      .exchange()
      .expectBody(ProblemResponse::class.java)
      .returnResult()
    httpStatus = resultError!!.status
    return resultError!!.responseBody!!
  }

  protected open fun assertHttpStatusIsCreated() = Assertions.assertThat(httpStatus).isEqualTo(HttpStatus.CREATED)

  protected open fun assertHttpStatusIsOk() = Assertions.assertThat(httpStatus).isEqualTo(HttpStatus.OK)

  protected open fun assertHttpStatusIsNotFound() = Assertions.assertThat(httpStatus).isEqualTo(HttpStatus.NOT_FOUND)

  protected open fun assertHttpStatusIsAccepted() = Assertions.assertThat(httpStatus).isEqualTo(HttpStatus.ACCEPTED)

  protected open fun assertHttpStatusIsNoContent() = Assertions.assertThat(httpStatus).isEqualTo(HttpStatus.NO_CONTENT)

  protected open fun assertHttpStatusIsBadRequest() = Assertions.assertThat(httpStatus).isEqualTo(HttpStatus.BAD_REQUEST)

  protected open fun assertHttpStatusIsConflict() = Assertions.assertThat(httpStatus).isEqualTo(HttpStatus.CONFLICT)

}
