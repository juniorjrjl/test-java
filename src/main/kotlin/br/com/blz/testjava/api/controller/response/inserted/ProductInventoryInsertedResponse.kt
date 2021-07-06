package br.com.blz.testjava.api.controller.response.inserted

data class ProductInventoryInsertedResponse(var quantity: Long?,
                                            var warehouses: MutableList<ProductWarehouseInsertedResponse>?)
