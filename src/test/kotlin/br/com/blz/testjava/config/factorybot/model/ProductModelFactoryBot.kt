package br.com.blz.testjava.config.factorybot.model

import br.com.blz.testjava.config.factorybot.RandomData
import br.com.blz.testjava.config.factorybot.nextUniqueValue
import br.com.blz.testjava.domain.model.WarehouseModel
import br.com.blz.testjava.domain.model.ProductModel
import com.github.javafaker.Faker
import java.util.stream.Collectors
import java.util.stream.Stream

class ProductModelFactoryBot {

  companion object {
    var faker: Faker = RandomData.getFaker()
    var sku: Long? = null
    var name: String? = null
    var warehouses: MutableList<WarehouseModel>? = null

    fun withRandomValues() = apply {
      sku = faker.nextUniqueValue()
      name = faker.food().ingredient()
      warehouses = Stream.generate { WarehouseModelFactoryBot.withRandomValues().build() }
        .limit(faker.number().numberBetween(0L, 10L))
        .collect(Collectors.toList())
    }

    fun withNullWarehouses() = apply {
      withRandomValues()
      warehouses = null
    }

    fun withEmptyWarehouses() = apply {
      withRandomValues()
      warehouses = ArrayList()
    }

    fun withZeroWarehouseQuantity() = apply {
      withRandomValues()
      warehouses = Stream.generate { WarehouseModelFactoryBot.withZeroQuantity().build() }
        .limit(2)
        .collect(Collectors.toList())
    }

    fun build(): ProductModel = ProductModel(sku, name, warehouses)

  }


}
