package br.com.blz.testjava.core.converter

import br.com.blz.testjava.domain.model.type.WarehousesType
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component

@Component
class WarehousesTypeEnumConverter : Converter<String, WarehousesType>{

  companion object {
    private val log: Logger = LoggerFactory.getLogger(WarehousesTypeEnumConverter::class.java)
  }

  override fun convert(value: String): WarehousesType =
    try {
      log.info("try to convert {} to {}", value, WarehousesType::class.java)
      WarehousesType.valueOf(value.uppercase())
    } catch (e: IllegalArgumentException) {
      log.error("error to convert {} to {}", value, WarehousesType::class.java)
      throw e
    }

}
