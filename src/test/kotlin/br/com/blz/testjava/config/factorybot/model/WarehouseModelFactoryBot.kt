package br.com.blz.testjava.config.factorybot.model

import br.com.blz.testjava.config.factorybot.RandomData
import br.com.blz.testjava.config.factorybot.randomEnumValue
import br.com.blz.testjava.config.factorybot.request.ProductWarehouseUpdateRequestFactoryBot
import br.com.blz.testjava.domain.model.WarehouseModel
import br.com.blz.testjava.domain.model.type.WarehousesType
import com.github.javafaker.Faker

class WarehouseModelFactoryBot {

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

    fun withZeroQuantity() = apply {
      withRandomValues()
      quantity = 0
    }

    fun build():WarehouseModel = WarehouseModel(locality, quantity, type)

  }

}
