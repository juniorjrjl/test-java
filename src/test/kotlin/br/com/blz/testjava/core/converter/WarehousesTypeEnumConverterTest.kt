package br.com.blz.testjava.core.converter

import br.com.blz.testjava.config.factorybot.RandomData
import br.com.blz.testjava.domain.model.type.WarehousesType
import com.github.javafaker.Faker
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.streams.asStream

class WarehousesTypeEnumConverterTest {

  private var converter: WarehousesTypeEnumConverter? = null
  private val faker: Faker = RandomData.getFaker()

  companion object{
    @JvmStatic fun prepareEnum():Stream<String> = Stream.concat(
      WarehousesType.values().iterator().asSequence().asStream().map { it.name },
      WarehousesType.values().iterator().asSequence().asStream().map { it.name.lowercase() }
    )
  }

  @MethodSource("prepareEnum")
  @ParameterizedTest
  fun testConvert(value: String?) {
    Assertions.assertNotNull(converter!!.convert(value!!))
  }

  @Test
  fun testConvertInvalid() {
    Assertions.assertThrows(IllegalArgumentException::class.java) { converter!!.convert(faker.yoda().quote()) }
  }


  @BeforeEach
  fun setup(){
    converter = WarehousesTypeEnumConverter()
  }

}
