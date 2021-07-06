package br.com.blz.testjava.domain.repository

import br.com.blz.testjava.domain.model.ProductModel
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import javax.management.InvalidAttributeValueException

@Repository
class ProductRepository {

  companion object {
    private val log: Logger = LoggerFactory.getLogger(ProductRepository::class.java)
  }

  private var products: MutableList<ProductModel> = arrayListOf()

  fun save(model: ProductModel): Mono<ProductModel> = Mono.just(model)
    .map {
      products.add(it)
      it
    }.doFirst { log.info(" ==== inserting a follow product [{}]", model) }

  fun delete(sku: Long):Mono<Void> = Mono.fromCallable {
    products = products.filterNot { it.sku?.equals(sku) ?: throw InvalidAttributeValueException() }.toMutableList()
    products
  }
    .doFirst { log.info(" ==== deleting a product with follow sku [{}]", sku) }
    .then()

  fun update(model: ProductModel):Mono<ProductModel> = Mono.fromCallable {
    val indexToUpdate = products.indexOf(products.singleOrNull { it.sku?.equals(model.sku) ?: throw InvalidAttributeValueException() })
    products = products.mapIndexed { i, p -> if (i == indexToUpdate)  model else p }.toMutableList() }
    .doFirst { log.info(" ==== updating a follow product [{}]", model) }
    .thenReturn(model)

  fun findBySku(sku: Long):Mono<ProductModel> = Mono.justOrEmpty(products.singleOrNull { it.sku?.equals(sku) ?: throw InvalidAttributeValueException() })
    .doFirst { log.info(" ==== getting a product with follow sku [{}]", sku) }

  fun hasWithSku(sku: Long):Mono<Boolean> = Mono.just(products.firstOrNull { it.sku?.equals(sku) ?: false } != null)
    .doFirst { log.info(" ==== checking if has product with sku [{}]", sku) }

}
