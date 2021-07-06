package br.com.blz.testjava.domain.exception

import java.lang.RuntimeException

open class TestJavaException : RuntimeException{

  constructor(message: String):super(message)

  constructor(message: String, cause: Throwable): super(message, cause)

}
