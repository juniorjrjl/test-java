package br.com.blz.testjava.api.controller.request.insert

import br.com.blz.testjava.domain.model.type.WarehousesType
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.PositiveOrZero

data class ProductWarehouseInsertRequest (
  @field:NotBlank
  var locality: String?,
  @field:PositiveOrZero
  @field:NotNull
  var quantity: Long?,
  @field:NotNull
  var type: WarehousesType?)
