package br.com.blz.testjava.api.controller.request.update

import javax.validation.Valid

class ProductInventoryUpdateRequest(
  @field:Valid
  var warehouses: MutableList<ProductWarehouseUpdateRequest>?)
