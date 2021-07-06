package br.com.blz.testjava.api.controller.product

import br.com.blz.testjava.api.controller.AbstractControllerTestWithoutBody
import br.com.blz.testjava.api.controller.ProductController
import br.com.blz.testjava.api.mapper.ProductMapperImpl
import br.com.blz.testjava.config.factorybot.RandomData
import br.com.blz.testjava.domain.exception.ProductNotFoundException
import br.com.blz.testjava.domain.service.ProductService
import br.com.blz.testjava.domain.service.query.ProductQueryService
import com.github.javafaker.Faker
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ContextConfiguration
import reactor.core.publisher.Mono

@WebFluxTest(ProductController::class)
@ContextConfiguration(classes = [ProductMapperImpl::class])
class ProductControllerDeleteTest: AbstractControllerTestWithoutBody("/products") {

  @MockBean
  private lateinit var productService: ProductService
  @MockBean
  private lateinit var productQueryService: ProductQueryService

  companion object{
    @JvmStatic private var faker: Faker = RandomData.getFaker()
  }

  @Test
  fun `delete product by sku test`(){
    whenever(productService.delete(any())).thenReturn(Mono.empty())
    doDelete{ it.pathSegment("{sku}").build(faker.number().randomDigitNotZero()) }
    assertHttpStatusIsNoContent()
    verify(productService).delete(any())
  }

  @Test
  fun `when has no sku then throw a error`(){
    whenever(productService.delete(any())).thenReturn(Mono.error(ProductNotFoundException("ProductNotFoundException.message")))
    doDeleteWithError{ it.pathSegment("{sku}").build(faker.number().randomDigitNotZero()) }
    assertHttpStatusIsNotFound()
    verify(productService).delete(any())
  }

}
