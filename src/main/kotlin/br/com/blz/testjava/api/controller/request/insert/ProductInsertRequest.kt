package br.com.blz.testjava.api.controller.request.insert

import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Positive

data class ProductInsertRequest (
  @field:Positive
  @field:NotNull
  var sku: Long?,
  @field:NotBlank
  var name: String?,
  @field:Valid
  var inventory: ProductInventoryInsertRequest?)
