package br.com.blz.testjava.api.controller.response.inserted


class ProductInsertedResponse (var sku: Long?,
                               var name: String?,
                               var inventory: ProductInventoryInsertedResponse?,
                               var isMarketable: Boolean?)
