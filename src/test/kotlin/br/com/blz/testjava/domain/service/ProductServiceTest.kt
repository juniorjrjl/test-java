package br.com.blz.testjava.domain.service

import br.com.blz.testjava.config.factorybot.RandomData
import br.com.blz.testjava.config.factorybot.model.ProductModelFactoryBot
import br.com.blz.testjava.domain.exception.ConflictException
import br.com.blz.testjava.domain.exception.ProductNotFoundException
import br.com.blz.testjava.domain.model.ProductModel
import br.com.blz.testjava.domain.repository.ProductRepository
import br.com.blz.testjava.domain.service.query.ProductQueryService
import com.github.javafaker.Faker
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor

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
class ProductServiceTest {

  @Autowired
  private lateinit var messageSource: MessageSource

  private var faker: Faker = RandomData.getFaker()

  private lateinit var productService: ProductService

  @Mock
  private lateinit var productRepository: ProductRepository
  @Mock
  private lateinit var productQueryService: ProductQueryService

  @BeforeEach
  fun setup(){
    productService = ProductService(productRepository, productQueryService, messageSource)
  }

  @Test
  fun `save test`(){
    val product = ProductModelFactoryBot.withRandomValues().build()
    whenever(productRepository.save(any())).thenReturn(Mono.just(product))
    whenever(productRepository.hasWithSku(any())).thenReturn(Mono.just(false))
    StepVerifier.create(productService.save(product))
      .assertNext{ assertThat(it).usingRecursiveComparison().isEqualTo(product) }
      .verifyComplete()
    verify(productRepository).save(any())
    verify(productRepository).hasWithSku(any())
  }

  @Test
  fun `when has product with same sku Then throw error`(){
    val product = ProductModelFactoryBot.withRandomValues().build()
    whenever(productRepository.hasWithSku(any())).thenReturn(Mono.just(true))
    StepVerifier.create(productService.save(product))
      .verifyError(ConflictException::class.java)
    verify(productRepository, times(0)).save(any())
    verify(productRepository).hasWithSku(any())
  }

  @Test
  fun `update test`(){
    val product = ProductModelFactoryBot.withRandomValues().build()
    val storedProduct = ProductModelFactoryBot.withRandomValues().build()
    whenever(productQueryService.findBySku(any())).thenReturn(Mono.just(storedProduct))
    whenever(productRepository.update(any())).thenAnswer { Mono.just(it.getArgument(0, ProductModel::class.java)) }
    StepVerifier.create(productService.update(product))
      .assertNext{ assertThat(it).usingRecursiveComparison().isEqualTo(product) }
      .verifyComplete()
    verify(productQueryService).findBySku(any())
    verify(productRepository).update(any())
  }

  @Test
  fun `when try update non-existent product then throw a error`(){
    val product = ProductModelFactoryBot.withRandomValues().build()
    whenever(productQueryService.findBySku(any())).thenReturn(Mono.error(ProductNotFoundException("Produto não encontrado")))
    StepVerifier.create(productService.update(product))
      .verifyError(ProductNotFoundException::class.java)
    verify(productQueryService).findBySku(any())
    verify(productRepository, times(0)).update(any())
  }

  @Test
  fun `delete test`(){
    val product = ProductModelFactoryBot.withRandomValues().build()
    val sku = product.sku?: throw InvalidAttributeValueException()
    whenever(productQueryService.findBySku(any())).thenReturn(Mono.just(product))
    whenever(productRepository.delete(any())).thenReturn(Mono.empty())
    StepVerifier.create(productService.delete(sku))
      .verifyComplete()
    verify(productQueryService).findBySku(any())
    verify(productRepository).delete(any())
  }

  @Test
  fun `when try delete non-existent product then throw a error`(){
    whenever(productQueryService.findBySku(any())).thenReturn(Mono.error(ProductNotFoundException("Produto não encontrado")))
    StepVerifier.create(productService.delete(faker.number().randomNumber()))
      .verifyError(ProductNotFoundException::class.java)
    verify(productQueryService).findBySku(any())
    verify(productRepository, times(0)).delete(any())
  }

}
