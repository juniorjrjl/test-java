package br.com.blz.testjava.domain.service.query

import br.com.blz.testjava.config.factorybot.RandomData
import br.com.blz.testjava.config.factorybot.model.ProductModelFactoryBot
import br.com.blz.testjava.domain.exception.ProductNotFoundException
import br.com.blz.testjava.domain.repository.ProductRepository
import com.github.javafaker.Faker
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration
import org.springframework.context.MessageSource
import org.springframework.test.context.junit.jupiter.SpringExtension
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import javax.management.InvalidAttributeValueException

@ExtendWith(value = [MockitoExtension::class, SpringExtension::class])
@ImportAutoConfiguration(classes = [MessageSourceAutoConfiguration::class])
class ProductQueryServiceTest {

  @Autowired
  private lateinit var messageSource: MessageSource

  private lateinit var productQueryService: ProductQueryService

  private var faker: Faker = RandomData.getFaker()

  @Mock
  private lateinit var productRepository: ProductRepository

  @BeforeEach
  fun setup() {
    productQueryService = ProductQueryService(productRepository, messageSource)
  }

  @Test
  fun `find by sku test`(){
    val product = ProductModelFactoryBot.withRandomValues().build()
    val sku = product.sku?: throw InvalidAttributeValueException()
    whenever(productRepository.findBySku(any())).thenReturn(Mono.just(product))
    StepVerifier.create(productQueryService.findBySku(sku))
      .assertNext{ assertThat(it).usingRecursiveComparison().isEqualTo(product) }
      .verifyComplete()
    verify(productRepository).findBySku(any())
  }

  @Test
  fun `when has no product with sku then throw a error`(){
    whenever(productRepository.findBySku(any())).thenReturn(Mono.empty())
    StepVerifier.create(productQueryService.findBySku(faker.number().randomNumber()))
      .verifyError(ProductNotFoundException::class.java)
    verify(productRepository).findBySku(any())
  }

}
