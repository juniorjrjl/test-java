package br.com.blz.testjava.config.factorybot

import com.github.javafaker.Faker
import java.util.*

class RandomData {

  companion object{
    @JvmStatic var actualUniqueValue: Long = 0

    fun getFaker():Faker = Faker(Locale("pt-BR"))

  }

}

fun Faker.nextUniqueValue():Long{
  ++RandomData.actualUniqueValue
  return RandomData.actualUniqueValue
}

fun <T : Enum<*>?> Faker.randomEnumValue(clazz: Class<T>): T {
  val x = Random().nextInt(clazz.enumConstants.size)
  return clazz.enumConstants[x]
}
