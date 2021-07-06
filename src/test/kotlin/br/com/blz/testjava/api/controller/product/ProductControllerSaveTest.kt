package br.com.blz.testjava.api.controller.product

import br.com.blz.testjava.api.controller.AbstractControllerTestWithBody
import br.com.blz.testjava.api.controller.ProductController
import br.com.blz.testjava.api.controller.request.insert.ProductInsertRequest
import br.com.blz.testjava.api.controller.response.inserted.ProductInsertedResponse
import br.com.blz.testjava.api.mapper.ProductMapper
import br.com.blz.testjava.api.mapper.ProductMapperImpl
import br.com.blz.testjava.config.factorybot.RandomData
import br.com.blz.testjava.config.factorybot.request.ProductInsertRequestFactoryBot
import br.com.blz.testjava.domain.exception.ConflictException
import br.com.blz.testjava.domain.service.ProductService
import br.com.blz.testjava.domain.service.query.ProductQueryService
import com.github.javafaker.Faker
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ContextConfiguration
import reactor.core.publisher.Mono
import java.util.stream.Stream

@WebFluxTest(ProductController::class)
@ContextConfiguration(classes = [ProductMapperImpl::class])
class ProductControllerSaveTest: AbstractControllerTestWithBody<ProductInsertedResponse>("/products", ProductInsertedResponse::class.java) {

  @MockBean
  private lateinit var productService: ProductService
  @MockBean
  private lateinit var productQueryService: ProductQueryService

  companion object{
    @JvmStatic private var faker: Faker = RandomData.getFaker()

    @JvmStatic fun constraintViolationTestData():Stream<Arguments> =
      Stream.of(
        Arguments.of(ProductInsertRequestFactoryBot.withNullSku().build(), "sku"),
        Arguments.of(ProductInsertRequestFactoryBot.withNegativeSku().build(), "sku"),
        Arguments.of(ProductInsertRequestFactoryBot.withBlankName().build(), "name"),
        Arguments.of(ProductInsertRequestFactoryBot.withBlankInventoryLocality().build(), "inventory.warehouses[0].locality"),
        Arguments.of(ProductInsertRequestFactoryBot.withNegativeOrZeroInventoryQuantity().build(), "inventory.warehouses[0].quantity"),
        Arguments.of(ProductInsertRequestFactoryBot.withNullInventoryQuantity().build(), "inventory.warehouses[0].quantity"),
        Arguments.of(ProductInsertRequestFactoryBot.withNullInventoryType().build(), "inventory.warehouses[0].type"),
      )

    @JvmStatic fun NoInventoryTestData(): Stream<ProductInsertRequest> =
      Stream.of(
        ProductInsertRequestFactoryBot.withNullInventory().build(),
        ProductInsertRequestFactoryBot.withNullWarehouses().build(),
        ProductInsertRequestFactoryBot.withWarehousesZeroQuantity().build()
      )
  }

  @Autowired
  private lateinit var productMapper: ProductMapper

  @Test
  fun `save test`(){
    val request = ProductInsertRequestFactoryBot.withRandomValues().build()
    val model = productMapper.toModel(request)
    whenever(productService.save(any())).thenReturn(Mono.just(model))
    val actual = doPost(request){ it.build() }
    assertHttpStatusIsCreated()
    assertThat(request).usingRecursiveComparison().isEqualTo(actual)
    verify(productService).save(any())
  }

  @ParameterizedTest
  @MethodSource("NoInventoryTestData")
  fun `when has no warehouse quantity then is not marketable and inventory quantity is zero`(request: ProductInsertRequest){
    val model = productMapper.toModel(request)
    whenever(productService.save(any())).thenReturn(Mono.just(model))
    val actual = doPost(request){ it.build() }
    assertHttpStatusIsCreated()
    assertThat(actual.isMarketable).isFalse
    assertThat(actual.inventory!!.quantity).isZero
  }

  @ParameterizedTest
  @MethodSource("constraintViolationTestData")
  fun `When request has constraint violation Then return bad request`(request: ProductInsertRequest, fieldWithError: String){
    val responseError = doPostWithError(request){ it.build() }
    assertHttpStatusIsBadRequest()
    assertThat(responseError).isNotNull
    assertThat(responseError.fields).isNotNull
    assertThat(responseError.fields?.size).isOne
    val responseFields = responseError.fields!!
    assertThat(responseFields.map { it.field }).contains(fieldWithError)
    verify(productService, times(0)).save(any())
  }

  @Test
  fun `When sku in Use throw a error`(){
    val request = ProductInsertRequestFactoryBot.withRandomValues().build()
    whenever(productService.save(any())).thenReturn(Mono.error(ConflictException("")))
    val responseError = doPostWithError(request){ it.build() }
    assertHttpStatusIsBadRequest()
    assertThat(responseError).isNotNull
  }

}
