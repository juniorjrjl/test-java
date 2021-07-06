package br.com.blz.testjava.api.exceptionhandler

import br.com.blz.testjava.domain.exception.ModelNotFoundException
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.core.annotation.Order
import org.springframework.core.io.buffer.DataBufferFactory
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.server.MethodNotAllowedException
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebExceptionHandler
import reactor.core.publisher.Mono
import java.time.OffsetDateTime

//@Component
//@Order(-2)
class UnhandledExceptionHandler(var mapper: ObjectMapper, var messageSource: MessageSource) : WebExceptionHandler {

  companion object {
    @JvmStatic private val log: Logger = LoggerFactory.getLogger(UnhandledExceptionHandler::class.java)
  }

  private val dataBufferFactory: DataBufferFactory = DefaultDataBufferFactory()

  private val errorMessageTitleCode = "ResponseError.message"

  override fun handle(exchange: ServerWebExchange, throwable: Throwable): Mono<Void> = Mono.error<Void>(throwable)
    .onErrorResume(MethodNotAllowedException::class.java) { handleMethodNotAllowedException(exchange) }
    .doFirst{ log.error("==== Method [{}] is not allowed at [{}]. Message: {}", exchange.request.methodValue, exchange.request.path.value(), throwable.localizedMessage) }
    .onErrorResume({ it is ResponseStatusException || it is ModelNotFoundException }, { handleResourceNotFoundException(exchange, it) })
    .doFirst{ log.error("==== Resource [{}] was not found. Message: {}", exchange.request.path.value(), throwable.localizedMessage) }
    .onErrorResume{ handleGenericException(exchange) }
    .doFirst{ log.error("==== Generic exception at [{}] [{}].", exchange.request.methodValue, exchange.request.path.value(), throwable) }
    .onErrorResume(JsonProcessingException::class.java) {
      exchange.response.statusCode = HttpStatus.INTERNAL_SERVER_ERROR
      exchange.response.setComplete()
    }
    .doFirst { log.error("==== Failed to map exception for the request [{}]", exchange.request.path.value(), throwable) }
    .then()

  private fun handleMethodNotAllowedException(exchange: ServerWebExchange): Mono<Void> =
    Mono.fromCallable {
      exchange.response.statusCode = HttpStatus.METHOD_NOT_ALLOWED
      exchange.response.headers.contentType = MediaType.APPLICATION_JSON
      getMessage("MethodNotAllowedException.message", arrayOf(exchange.request.path, exchange.request.methodValue))
    }.map { buildError(HttpStatus.METHOD_NOT_ALLOWED.value(), getMessage(errorMessageTitleCode), it) }
      .flatMap { writeResponse(exchange, it) }

  private fun handleResourceNotFoundException(exchange: ServerWebExchange, ex: Throwable): Mono<Void> =
    Mono.fromCallable {
      exchange.response.statusCode = HttpStatus.NOT_FOUND
      exchange.response.headers.contentType = MediaType.APPLICATION_JSON
      getMessage("ResourceNotFoundException.message")
    }.map { buildError(HttpStatus.NOT_FOUND.value(), getMessage(errorMessageTitleCode), it) }
      .flatMap { writeResponse(exchange, it) }

  private fun handleGenericException(exchange: ServerWebExchange): Mono<Void> =
    Mono.fromCallable {
      exchange.response.statusCode = HttpStatus.INTERNAL_SERVER_ERROR
      exchange.response.headers.contentType = MediaType.APPLICATION_JSON
      getMessage("GenericException.message")
    }.map { buildError(HttpStatus.INTERNAL_SERVER_ERROR.value(), getMessage(errorMessageTitleCode), it) }
      .flatMap { writeResponse(exchange, it) }

  private fun getMessage(code: String, args: Array<Any> = emptyArray()):String = messageSource.getMessage(code, args, LocaleContextHolder.getLocale())

  private fun buildError(status: Int, title: String, details: String):ProblemResponse = ProblemResponse.Builder()
    .withTimestamp(OffsetDateTime.now())
    .withStatus(status)
    .withTitle(title)
    .withDetails(details)
    .build()

  private fun writeResponse(exchange: ServerWebExchange, problemResponse: ProblemResponse):Mono<Void> {
    return exchange.response
      .writeWith { Mono.fromCallable { dataBufferFactory.wrap(mapper.writeValueAsBytes(problemResponse)) } }
  }

}
