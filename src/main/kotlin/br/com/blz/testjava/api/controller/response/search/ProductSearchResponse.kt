package br.com.blz.testjava.api.controller.response.search


class ProductSearchResponse (var sku: Long?,
                             var name: String?,
                             var inventory: ProductInventorySearchResponse?,
                             var isMarketable: Boolean?)
