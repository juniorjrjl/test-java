package br.com.blz.testjava.config.factorybot.request

import br.com.blz.testjava.api.controller.request.insert.ProductInventoryInsertRequest
import br.com.blz.testjava.api.controller.request.update.ProductInventoryUpdateRequest
import br.com.blz.testjava.api.controller.request.update.ProductUpdateRequest
import br.com.blz.testjava.api.controller.request.update.ProductWarehouseUpdateRequest
import br.com.blz.testjava.config.factorybot.RandomData
import com.github.javafaker.Faker
import java.util.stream.Collectors
import java.util.stream.Stream

class ProductUpdateRequestFactoryBot {

  companion object {
    var faker: Faker = RandomData.getFaker()
    var name: String? = null
    var inventory: ProductInventoryUpdateRequest? = null

    fun withRandomValues() = apply {
      name = faker.food().ingredient()
      val warehouses = Stream.generate { ProductWarehouseUpdateRequestFactoryBot.withRandomValues().build() }
        .limit(faker.number().numberBetween(1L, 10L))
        .collect(Collectors.toList())
      inventory = ProductInventoryUpdateRequest(warehouses)
    }

    fun withBlankName() = apply {
      withRandomValues()
      name = if (faker.bool().bool()) null else "       "
    }


    fun withBlankInventoryLocality() = apply {
      withRandomValues()
      val warehouses = Stream.generate { ProductWarehouseUpdateRequestFactoryBot.withBlankLocality().build() }
        .limit(1L)
        .collect(Collectors.toList())
      inventory = ProductInventoryUpdateRequest(warehouses)
    }

    fun withNegativeOrZeroInventoryQuantity() = apply {
      withRandomValues()
      val warehouses = Stream.generate { ProductWarehouseUpdateRequestFactoryBot.withNegativeOrZeroQuantity().build() }
        .limit(1L)
        .collect(Collectors.toList())
      inventory = ProductInventoryUpdateRequest(warehouses)
    }

    fun withNullInventoryQuantity() = apply {
      withRandomValues()
      val warehouses = Stream.generate { ProductWarehouseUpdateRequestFactoryBot.withNullQuantity().build() }
        .limit(1L)
        .collect(Collectors.toList())
      inventory = ProductInventoryUpdateRequest(warehouses)
    }

    fun withNullInventoryType() = apply{
      withRandomValues()
      val warehouses = Stream.generate { ProductWarehouseUpdateRequestFactoryBot.withNullType().build() }
        .limit(1L)
        .collect(Collectors.toList())
      inventory = ProductInventoryUpdateRequest(warehouses)
    }

    fun withNullInventory() = apply {
      withRandomValues()
      inventory = null
    }

    fun withNullWarehouses() = apply {
      withRandomValues()
      inventory = ProductInventoryUpdateRequest(null)
    }

    fun withWarehousesZeroQuantity() = apply {
      withRandomValues()
      val warehouses = Stream.generate { ProductWarehouseUpdateRequestFactoryBot.withZeroQuantity().build() }
        .limit(2L)
        .collect(Collectors.toList())
      inventory = ProductInventoryUpdateRequest(warehouses)
    }


    fun build(): ProductUpdateRequest = ProductUpdateRequest(name, inventory)

  }


}
