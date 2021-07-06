package br.com.blz.testjava.api.controller.request.insert

import javax.validation.Valid

class ProductInventoryInsertRequest(
  @field:Valid
  var warehouses: MutableList<ProductWarehouseInsertRequest>?)
