package br.com.blz.testjava.api.controller.response.updated

import br.com.blz.testjava.api.controller.response.inserted.ProductWarehouseInsertedResponse

class ProductInventoryUpdatedResponse(var quantity: Long?,
                                      var warehouses: MutableList<ProductWarehouseUpdatedResponse>?, )
