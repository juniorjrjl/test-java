package br.com.blz.testjava.api.exceptionhandler

import br.com.blz.testjava.domain.exception.ConflictException
import br.com.blz.testjava.domain.exception.ModelNotFoundException
import br.com.blz.testjava.domain.exception.ProductNotFoundException
import br.com.blz.testjava.domain.exception.TestJavaException
import org.hibernate.validator.internal.engine.path.PathImpl
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.http.HttpStatus
import org.springframework.validation.FieldError
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.bind.support.WebExchangeBindException
import java.time.OffsetDateTime
import javax.validation.ConstraintViolationException

@RestControllerAdvice
class ControllerExceptionHandler(var messageSource: MessageSource) {

  companion object {
    @JvmStatic private val log: Logger = LoggerFactory.getLogger(ControllerExceptionHandler::class.java)
  }

  private val errorMessageTitleCode = "ResponseError.message"

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(Exception::class)
  fun handleGenericException(ex: Exception): ProblemResponse{
    log.error("=== ConstraintViolationException", ex)
    return buildError(HttpStatus.INTERNAL_SERVER_ERROR.value(), getMessage(errorMessageTitleCode),
      getMessage("GenericException.message")).build()
  }

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(TestJavaException::class)
  fun handleTestJavaException(ex: TestJavaException): ProblemResponse{
    log.error("=== ConstraintViolationException", ex)
    return buildError(HttpStatus.INTERNAL_SERVER_ERROR.value(), getMessage(errorMessageTitleCode),
      getMessage("GenericException.message")).build()
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(ConstraintViolationException::class)
  fun handleConstraintViolationException(ex: ConstraintViolationException): ProblemResponse{
    log.error("=== ConstraintViolationException", ex)
    val errorFields = ex.constraintViolations
      .map { FieldErrorResponse.Builder()
        .withField((it.propertyPath as PathImpl).leafNode.toString())
        .withErrorMessage(it.message)
        .build()
      }.toMutableList()
    return buildError(HttpStatus.BAD_REQUEST.value(), getMessage(errorMessageTitleCode),
      getMessage("RequestWithErrors.message")).withFields(errorFields).build()
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(WebExchangeBindException::class)
  fun handleWebExchangeBindException(ex: WebExchangeBindException): ProblemResponse{
    log.error("=== WebExchangeBindException -> {}", ex.localizedMessage)
    val errorFields = ex.allErrors.map {
      var fieldError = it.objectName
      if (it is FieldError){
        fieldError = it.field
      }
      FieldErrorResponse.Builder()
        .withField(fieldError)
        .withErrorMessage(messageSource.getMessage(it, LocaleContextHolder.getLocale()))
        .build()
    }.toMutableList()
    return buildError(HttpStatus.BAD_REQUEST.value(), getMessage(errorMessageTitleCode),
      getMessage("RequestWithErrors.message")).withFields(errorFields).build()
  }

  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler(ProductNotFoundException::class)
  fun handleProductNotFoundException(ex: ProductNotFoundException): ProblemResponse{
    log.error("=== ProductNotFoundException", ex)
    return buildError(HttpStatus.NOT_FOUND.value(), getMessage(errorMessageTitleCode), ex.message!!).build()
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(ConflictException::class)
  fun handleConflictException(ex: ConflictException): ProblemResponse{
    log.error("=== ConflictException", ex)
    return buildError(HttpStatus.BAD_REQUEST.value(), getMessage(errorMessageTitleCode), ex.message!!).build()
  }

  private fun buildError(status: Int, title: String, details: String) = ProblemResponse.Builder()
    .withTimestamp(OffsetDateTime.now())
    .withStatus(status)
    .withTitle(title)
    .withDetails(details)

  private fun getMessage(code: String, args: Array<Any> = emptyArray()):String =
    messageSource.getMessage(code, args, LocaleContextHolder.getLocale())

}
