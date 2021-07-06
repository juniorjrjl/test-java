package br.com.blz.testjava.config.factorybot.request

import br.com.blz.testjava.api.controller.request.insert.ProductInsertRequest
import br.com.blz.testjava.api.controller.request.insert.ProductInventoryInsertRequest
import br.com.blz.testjava.api.controller.request.insert.ProductWarehouseInsertRequest
import br.com.blz.testjava.config.factorybot.RandomData
import br.com.blz.testjava.config.factorybot.nextUniqueValue
import com.github.javafaker.Faker
import java.util.stream.Collectors
import java.util.stream.Stream

class ProductInsertRequestFactoryBot {

  companion object {
    var faker: Faker = RandomData.getFaker()
    var sku: Long? = null
    var name: String? = null
    var inventory: ProductInventoryInsertRequest? = null

    fun withRandomValues() = apply {
      sku = faker.nextUniqueValue()
      name = faker.food().ingredient()
      val warehouses = Stream.generate { ProductWarehouseInsertRequestFactoryBot.withRandomValues().build() }
        .limit(faker.number().numberBetween(1L, 10L))
        .collect(Collectors.toList())
      inventory = ProductInventoryInsertRequest(warehouses)
    }

    fun withNullSku() = apply{
      withRandomValues()
      sku = null
    }

    fun withNegativeSku() = apply{
      withRandomValues()
      sku = faker.number().numberBetween(Long.MIN_VALUE, 0)
    }

    fun withBlankName() = apply {
      withRandomValues()
      name = if (faker.bool().bool()) null else "       "
    }


    fun withBlankInventoryLocality() = apply {
      withRandomValues()
      val warehouses = Stream.generate { ProductWarehouseInsertRequestFactoryBot.withBlankLocality().build() }
        .limit(1L)
        .collect(Collectors.toList())
      inventory = ProductInventoryInsertRequest(warehouses)
    }

    fun withNegativeOrZeroInventoryQuantity() = apply {
      withRandomValues()
      val warehouses = Stream.generate { ProductWarehouseInsertRequestFactoryBot.withNegativeOrZeroQuantity().build() }
        .limit(1L)
        .collect(Collectors.toList())
      inventory = ProductInventoryInsertRequest(warehouses)
    }

    fun withNullInventoryQuantity() = apply {
      withRandomValues()
      val warehouses = Stream.generate { ProductWarehouseInsertRequestFactoryBot.withNullQuantity().build() }
        .limit(1L)
        .collect(Collectors.toList())
      inventory = ProductInventoryInsertRequest(warehouses)
    }

    fun withNullInventoryType() = apply{
      withRandomValues()
      val warehouses = Stream.generate { ProductWarehouseInsertRequestFactoryBot.withNullType().build() }
        .limit(1L)
        .collect(Collectors.toList())
      inventory = ProductInventoryInsertRequest(warehouses)
    }

    fun withNullInventory() = apply {
      withRandomValues()
      inventory = null
    }

    fun withNullWarehouses() = apply {
      withRandomValues()
      inventory = ProductInventoryInsertRequest(null)
    }

    fun withWarehousesZeroQuantity() = apply {
      withRandomValues()
      val warehouses = Stream.generate { ProductWarehouseInsertRequestFactoryBot.withZeroQuantity().build() }
        .limit(2L)
        .collect(Collectors.toList())
      inventory = ProductInventoryInsertRequest(warehouses)
    }

    fun build(): ProductInsertRequest = ProductInsertRequest(sku, name, inventory)

  }


}
