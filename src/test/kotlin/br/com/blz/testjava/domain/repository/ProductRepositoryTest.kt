package br.com.blz.testjava.domain.repository

import br.com.blz.testjava.config.factorybot.RandomData
import br.com.blz.testjava.config.factorybot.model.ProductModelFactoryBot
import com.github.javafaker.Faker
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import reactor.test.StepVerifier
import javax.management.InvalidAttributeValueException

@WebFluxTest
@ActiveProfiles("test")
@ContextConfiguration(classes = [ProductRepository::class])
class ProductRepositoryTest {

  @Autowired
  private lateinit var productRepository: ProductRepository

  private var faker: Faker = RandomData.getFaker()

  @Test
  fun `verify save success`(){
    val product = ProductModelFactoryBot.withRandomValues().build()
    StepVerifier.create(productRepository.save(product))
      .assertNext{ assertThat(it).usingRecursiveComparison().isEqualTo(product)}
      .verifyComplete()
  }

  @Test
  fun `verify delete success`(){
    val product = ProductModelFactoryBot.withRandomValues().build()
    val sku = product.sku?: throw InvalidAttributeValueException()
    productRepository.save(product).block()
    StepVerifier.create(productRepository.findBySku(sku))
      .assertNext{ assertThat(it).usingRecursiveComparison().isEqualTo(product)}
      .verifyComplete()
    StepVerifier.create(productRepository.delete(sku))
      .verifyComplete()
    StepVerifier.create(productRepository.findBySku(sku))
      .assertNext{ assertThat(it).isNull()}
  }

  @Test
  fun `verify update success`(){
    val product = ProductModelFactoryBot.withRandomValues().build()
    val sku = product.sku?: throw InvalidAttributeValueException()
    productRepository.save(product).block()
    val productToUpdate = ProductModelFactoryBot.withRandomValues().build()
    productToUpdate.sku = sku
    productRepository.update(productToUpdate).block()
    StepVerifier.create(productRepository.findBySku(sku))
      .assertNext{
        assertThat(it).usingRecursiveComparison().ignoringFields("sku").isNotEqualTo(product)
        assertThat(it).usingRecursiveComparison().isEqualTo(productToUpdate)
      }
      .verifyComplete()
  }

  @Test
  fun `Whe sku is stored Then return true`(){
    val product = ProductModelFactoryBot.withRandomValues().build()
    val sku = product.sku?: throw InvalidAttributeValueException()
    productRepository.save(product).block()
    StepVerifier.create(productRepository.hasWithSku(sku))
      .assertNext{ assertThat(it).isTrue() }
      .verifyComplete()
  }

  @Test
  fun `Whe sku is not stored Then return false`(){
    val product = ProductModelFactoryBot.withRandomValues().build()
    productRepository.save(product).block()
    StepVerifier.create(productRepository.hasWithSku(faker.number().numberBetween(0L, Long.MAX_VALUE)))
      .assertNext{ assertThat(it).isFalse() }
      .verifyComplete()
  }

}

