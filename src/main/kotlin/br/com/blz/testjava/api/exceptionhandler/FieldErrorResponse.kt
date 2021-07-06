package br.com.blz.testjava.api.exceptionhandler

data class FieldErrorResponse(var field: String?,
                              var errorMessage: String?){

  class Builder{
    var field: String? = null
    var errorMessage: String? = null

    fun withField(field: String) = apply { this.field = field }
    fun withErrorMessage(errorMessage: String) = apply { this.errorMessage = errorMessage }
    fun build() = FieldErrorResponse(field, errorMessage)
  }

}
