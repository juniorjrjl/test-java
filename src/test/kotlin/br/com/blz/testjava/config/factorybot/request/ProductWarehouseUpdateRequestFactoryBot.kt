package br.com.blz.testjava.config.factorybot.request

import br.com.blz.testjava.api.controller.request.update.ProductWarehouseUpdateRequest
import br.com.blz.testjava.config.factorybot.RandomData
import br.com.blz.testjava.config.factorybot.randomEnumValue
import br.com.blz.testjava.domain.model.type.WarehousesType
import com.github.javafaker.Faker

class ProductWarehouseUpdateRequestFactoryBot {

  companion object {
    var faker: Faker = RandomData.getFaker()
    var locality: String? = null
    var quantity: Long? = null
    var type: WarehousesType? = null

    fun withRandomValues() = apply {
      locality = faker.address().city()
      quantity = faker.number().numberBetween(0L, 20L)
      type = faker.randomEnumValue(WarehousesType::class.java)
    }

    fun withBlankLocality() = apply {
      withRandomValues()
      locality = if (ProductInsertRequestFactoryBot.faker.bool().bool()) null else "       "
    }

    fun withNegativeOrZeroQuantity() = apply {
      withRandomValues()
      quantity = faker.number().numberBetween(Long.MIN_VALUE, 0L)
    }

    fun withNullQuantity() = apply {
      withRandomValues()
      quantity = null
    }

    fun withNullType() = apply{
      withRandomValues()
      type = null
    }

    fun withZeroQuantity() = apply {
      withRandomValues()
      quantity = 0
    }

    fun build(): ProductWarehouseUpdateRequest = ProductWarehouseUpdateRequest(locality, quantity, type)

  }

}
