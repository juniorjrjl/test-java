package br.com.blz.testjava.api.controller.product

import br.com.blz.testjava.api.controller.AbstractControllerTestWithBody
import br.com.blz.testjava.api.controller.ProductController
import br.com.blz.testjava.api.controller.request.update.ProductUpdateRequest
import br.com.blz.testjava.api.controller.response.search.ProductSearchResponse
import br.com.blz.testjava.api.mapper.ProductMapperImpl
import br.com.blz.testjava.config.factorybot.RandomData
import br.com.blz.testjava.config.factorybot.model.ProductModelFactoryBot
import br.com.blz.testjava.config.factorybot.request.ProductUpdateRequestFactoryBot
import br.com.blz.testjava.domain.exception.ProductNotFoundException
import br.com.blz.testjava.domain.model.ProductModel
import br.com.blz.testjava.domain.service.ProductService
import br.com.blz.testjava.domain.service.query.ProductQueryService
import com.github.javafaker.Faker
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ContextConfiguration
import reactor.core.publisher.Mono
import java.util.stream.Stream

@WebFluxTest(ProductController::class)
@ContextConfiguration(classes = [ProductMapperImpl::class])
class ProductControllerFindBySkuTest: AbstractControllerTestWithBody<ProductSearchResponse>("/products", ProductSearchResponse::class.java) {

  @MockBean
  private lateinit var productService: ProductService
  @MockBean
  private lateinit var productQueryService: ProductQueryService

  companion object{
    @JvmStatic private var faker: Faker = RandomData.getFaker()

    @JvmStatic fun NoInventoryTestData(): Stream<ProductModel> =
      Stream.of(
        ProductModelFactoryBot.withNullWarehouses().build(),
        ProductModelFactoryBot.withEmptyWarehouses().build(),
        ProductModelFactoryBot.withZeroWarehouseQuantity().build()
      )

  }

  @Test
  fun `find by sku test`(){
    val productModel = ProductModelFactoryBot.withRandomValues().build()
    whenever(productQueryService.findBySku(any())).thenReturn(Mono.just(productModel))
    val actual = doGet{ it.pathSegment("{sku}").build(productModel.sku) }
    assertHttpStatusIsOk()
    verify(productQueryService).findBySku(any())
    assertThat(actual).usingRecursiveComparison().ignoringFields("warehouses", "inventory", "isMarketable").isEqualTo(productModel)
    assertThat(actual.isMarketable).isTrue
    actual.inventory!!.warehouses!!.forEach { a ->
      assertThat(productModel.warehouses!!.firstOrNull{ it.locality == a.locality && it.quantity == a.quantity && it.type == a.type}).isNotNull
    }
    assertThat(actual.inventory!!.quantity).isEqualTo(productModel.warehouses!!.map { it.quantity }.reduce{ a, b -> a?.plus(b!!) })
  }

  @ParameterizedTest
  @MethodSource("NoInventoryTestData")
  fun `when has no warehouse quantity then is not marketable and inventory quantity is zero`(model: ProductModel){
    whenever(productQueryService.findBySku(any())).thenReturn(Mono.just(model))
    val actual = doGet{ it.pathSegment("{sku}").build(model.sku) }
    assertHttpStatusIsOk()
    assertThat(actual.isMarketable).isFalse
    assertThat(actual.inventory!!.quantity).isZero
  }

  @Test
  fun `when has no sku then throw a error`(){
    whenever(productQueryService.findBySku(any())).thenReturn(Mono.error(ProductNotFoundException("ProductNotFoundException.message")))
    val actual = doGetWithError{ it.pathSegment("{sku}").build(faker.number().randomDigitNotZero()) }
    assertHttpStatusIsNotFound()
    verify(productQueryService).findBySku(any())
  }

}
