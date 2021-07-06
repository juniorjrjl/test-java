package br.com.blz.testjava.api.mapper

import br.com.blz.testjava.api.controller.request.insert.ProductInsertRequest
import br.com.blz.testjava.api.controller.request.update.ProductUpdateRequest
import br.com.blz.testjava.api.controller.response.inserted.ProductInsertedResponse
import br.com.blz.testjava.api.controller.response.inserted.ProductInventoryInsertedResponse
import br.com.blz.testjava.api.controller.response.inserted.ProductWarehouseInsertedResponse
import br.com.blz.testjava.api.controller.response.search.ProductInventorySearchResponse
import br.com.blz.testjava.api.controller.response.search.ProductSearchResponse
import br.com.blz.testjava.api.controller.response.search.ProductWarehouseSearchResponse
import br.com.blz.testjava.api.controller.response.updated.ProductInventoryUpdatedResponse
import br.com.blz.testjava.api.controller.response.updated.ProductUpdatedResponse
import br.com.blz.testjava.api.controller.response.updated.ProductWarehouseUpdatedResponse
import br.com.blz.testjava.domain.model.ProductModel
import br.com.blz.testjava.domain.model.WarehouseModel
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import kotlin.collections.ArrayList

@Mapper(componentModel = "spring")
abstract class ProductMapper {

  @Mapping(target = "warehouses", source = "inventory.warehouses")
  abstract fun toModel(request: ProductInsertRequest):ProductModel

  @Mappings(
    Mapping(target = "inventory", expression = "java(buildInsertedInventory(model.getWarehouses()))"),
    Mapping(target = "isMarketable", expression = "java(isMarketable(model.getWarehouses()))")
  )
  abstract fun toInsertedResponse(model: ProductModel): ProductInsertedResponse

  @Mapping(target = "warehouses", source = "request.inventory.warehouses")
  abstract fun toModel(request: ProductUpdateRequest, sku: Long):ProductModel

  @Mappings(
    Mapping(target = "inventory", expression = "java(buildUpdatedInventory(model.getWarehouses()))"),
    Mapping(target = "isMarketable", expression = "java(isMarketable(model.getWarehouses()))")
  )
  abstract fun toUpdatedResponse(model: ProductModel): ProductUpdatedResponse

  @Mappings(
    Mapping(target = "inventory", expression = "java(buildFoundedInventory(model.getWarehouses()))"),
    Mapping(target = "isMarketable", expression = "java(isMarketable(model.getWarehouses()))")
  )
  abstract fun toSearchResponse(model: ProductModel): ProductSearchResponse

  protected fun buildInsertedInventory(warehouses: MutableList<WarehouseModel>?): ProductInventoryInsertedResponse {
    val distinctElements = warehouses?.toSet() ?: emptySet()
    val response :MutableList<ProductWarehouseInsertedResponse> = ArrayList()
    distinctElements.forEach { response.add(ProductWarehouseInsertedResponse(it.locality, it.quantity, it.type)) }
    val quantity = response.map { it.quantity }.fold(0L){a, e -> a.plus(e?:0) }
    return ProductInventoryInsertedResponse(quantity, response)
  }

  protected fun buildUpdatedInventory(warehouses: MutableList<WarehouseModel>?): ProductInventoryUpdatedResponse {
    val distinctElements = warehouses?.toSet() ?: emptySet()
    val response :MutableList<ProductWarehouseUpdatedResponse> = ArrayList()
    distinctElements.forEach { response.add(ProductWarehouseUpdatedResponse(it.locality, it.quantity, it.type)) }
    val quantity = response.map { it.quantity }.fold(0L){a, e -> a.plus(e?:0) }
    return ProductInventoryUpdatedResponse(quantity, response)
  }

  protected fun buildFoundedInventory(warehouses: MutableList<WarehouseModel>?): ProductInventorySearchResponse {
    val distinctElements = warehouses?.toSet() ?: emptySet()
    val response :MutableList<ProductWarehouseSearchResponse> = ArrayList()
    distinctElements.forEach { response.add(ProductWarehouseSearchResponse(it.locality, it.quantity, it.type)) }
    val quantity = response.map { it.quantity }.fold(0L){a, e -> a.plus(e?:0) }
    return ProductInventorySearchResponse(quantity, response)
  }

  fun isMarketable(warehouses: MutableList<WarehouseModel>?):Boolean =
    warehouses?.map { it.quantity }?.fold(0L) { acc, l -> acc.plus(l?:0) }?:0 > 0L


}
