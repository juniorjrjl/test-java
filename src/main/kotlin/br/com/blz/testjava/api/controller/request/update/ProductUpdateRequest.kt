package br.com.blz.testjava.api.controller.request.update

import javax.validation.Valid
import javax.validation.constraints.NotBlank

data class ProductUpdateRequest (
  @field:NotBlank
  var name: String?,
  @field:Valid
  var inventory: ProductInventoryUpdateRequest?)
