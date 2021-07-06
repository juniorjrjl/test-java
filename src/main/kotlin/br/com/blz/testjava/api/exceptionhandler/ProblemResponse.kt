package br.com.blz.testjava.api.exceptionhandler

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include
import java.time.OffsetDateTime

@JsonInclude(Include.NON_NULL)
data class ProblemResponse(var status: Int?,
                           var timestamp: OffsetDateTime?,
                           var title: String?,
                           var details: String?,
                           var fields: MutableList<FieldErrorResponse>?){

  class Builder{
    var status: Int? = null
    var timestamp: OffsetDateTime? = null
    var title: String? = null
    var details: String? = null
    var fields: MutableList<FieldErrorResponse>? = null

    fun withStatus(status: Int) = apply { this.status = status }
    fun withTimestamp(timestamp: OffsetDateTime) = apply { this.timestamp = timestamp }
    fun withTitle(title: String) = apply { this.title = title }
    fun withDetails(details: String) = apply { this.details = details }
    fun withFields(fields: MutableList<FieldErrorResponse>) = apply { this.fields = fields }
    fun build() = ProblemResponse(status, timestamp, title, details, fields)
  }

}
