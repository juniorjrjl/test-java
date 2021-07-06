package br.com.blz.testjava.api.controller.product

import br.com.blz.testjava.api.controller.AbstractControllerTestWithBody
import br.com.blz.testjava.api.controller.ProductController
import br.com.blz.testjava.api.controller.request.insert.ProductInsertRequest
import br.com.blz.testjava.api.controller.request.update.ProductUpdateRequest
import br.com.blz.testjava.api.controller.response.updated.ProductUpdatedResponse
import br.com.blz.testjava.api.mapper.ProductMapper
import br.com.blz.testjava.api.mapper.ProductMapperImpl
import br.com.blz.testjava.config.factorybot.RandomData
import br.com.blz.testjava.config.factorybot.request.ProductInsertRequestFactoryBot
import br.com.blz.testjava.config.factorybot.request.ProductUpdateRequestFactoryBot
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
class ProductControllerUpdateTest: AbstractControllerTestWithBody<ProductUpdatedResponse>("/products", ProductUpdatedResponse::class.java) {

  @MockBean
  private lateinit var productService: ProductService
  @MockBean
  private lateinit var productQueryService: ProductQueryService

  companion object{
    @JvmStatic private var faker: Faker = RandomData.getFaker()

    @JvmStatic fun constraintViolationTestData(): Stream<Arguments> =
      Stream.of(
        Arguments.of(ProductUpdateRequestFactoryBot.withBlankName().build(), "name"),
        Arguments.of(ProductUpdateRequestFactoryBot.withBlankInventoryLocality().build(), "inventory.warehouses[0].locality"),
        Arguments.of(ProductUpdateRequestFactoryBot.withNegativeOrZeroInventoryQuantity().build(), "inventory.warehouses[0].quantity"),
        Arguments.of(ProductUpdateRequestFactoryBot.withNullInventoryQuantity().build(), "inventory.warehouses[0].quantity"),
        Arguments.of(ProductUpdateRequestFactoryBot.withNullInventoryType().build(), "inventory.warehouses[0].type"),
      )

    @JvmStatic fun NoInventoryTestData(): Stream<ProductUpdateRequest> =
      Stream.of(
        ProductUpdateRequestFactoryBot.withNullInventory().build(),
        ProductUpdateRequestFactoryBot.withNullWarehouses().build(),
        ProductUpdateRequestFactoryBot.withWarehousesZeroQuantity().build()
      )

  }

  @Autowired
  private lateinit var productMapper: ProductMapper

  @Test
  fun `update test`(){
    val request = ProductUpdateRequestFactoryBot.withRandomValues().build()
    val model = productMapper.toModel(request, faker.number().numberBetween(1L, Long.MAX_VALUE))
    whenever(productService.update(any())).thenReturn(Mono.just(model))
    val actual = doPut(request){ it.pathSegment("{sku}").build(model.sku) }
    assertHttpStatusIsOk()
    assertThat(request).usingRecursiveComparison().ignoringFields("sku").isEqualTo(actual)
    assertThat(model.sku).isEqualTo(actual.sku)
  }

  @ParameterizedTest
  @MethodSource("NoInventoryTestData")
  fun `when has no warehouse quantity then is not marketable and inventory quantity is zero`(request: ProductUpdateRequest){
    val sku = faker.number().numberBetween(1L, Long.MAX_VALUE)
    val model = productMapper.toModel(request, sku)
    whenever(productService.update(any())).thenReturn(Mono.just(model))
    val actual = doPut(request){ it.pathSegment("{sku}").build(sku) }
    assertHttpStatusIsOk()
    assertThat(actual.isMarketable).isFalse
    assertThat(actual.inventory!!.quantity).isZero
  }

  @ParameterizedTest
  @MethodSource("constraintViolationTestData")
  fun `When request has constraint violation Then return bad request`(request: ProductUpdateRequest, fieldWithError: String){
    val responseError = doPutWithError(request){ it.pathSegment("{sku}").build(faker.number().randomDigitNotZero()) }
    assertHttpStatusIsBadRequest()
    assertThat(responseError).isNotNull
    assertThat(responseError.fields).isNotNull
    assertThat(responseError.fields?.size).isOne
    val responseFields = responseError.fields!!
    assertThat(responseFields.map { it.field }).contains(fieldWithError)
    verify(productService, times(0)).save(any())
  }

}
