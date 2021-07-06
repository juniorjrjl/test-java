package br.com.blz.testjava.domain.service

import br.com.blz.testjava.domain.exception.ConflictException
import br.com.blz.testjava.domain.model.ProductModel
import br.com.blz.testjava.domain.repository.ProductRepository
import br.com.blz.testjava.domain.service.query.ProductQueryService
import org.apache.commons.lang3.BooleanUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.lang.Exception
import javax.management.InvalidAttributeValueException

@Service
class ProductService(
  private val productRepository: ProductRepository,
  private val productQueryService: ProductQueryService,
  var messageSource: MessageSource) {

  companion object {
    private val log: Logger = LoggerFactory.getLogger(ProductService::class.java)
  }

  fun save(model: ProductModel): Mono<ProductModel> = productRepository.hasWithSku(model.sku?: throw InvalidAttributeValueException() )
    .filter { BooleanUtils.isFalse(it) }
    .switchIfEmpty(Mono.error(ConflictException(messageSource.getMessage("ProductModel.skuInUse", arrayOf(model.sku), LocaleContextHolder.getLocale()))))
    .flatMap { productRepository.save(model) }
    .doFirst { log.info(" ==== try to insert a follow product [{}]", model) }

  fun update(model: ProductModel): Mono<ProductModel> = productQueryService.findBySku(model.sku?:throw InvalidAttributeValueException() )
    .flatMap {  productRepository.update(model)}
    .doFirst { log.info(" ==== try to update a follow product [{}]", model) }

  fun delete(sku: Long): Mono<Void> = productQueryService.findBySku(sku)
    .flatMap { productRepository.delete(it.sku?:throw InvalidAttributeValueException()) }
    .doFirst { log.info(" ==== try to delete a product with sku [{}]", sku) }

}
