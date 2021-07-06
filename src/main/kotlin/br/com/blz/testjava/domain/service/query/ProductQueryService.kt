package br.com.blz.testjava.domain.service.query

import br.com.blz.testjava.domain.exception.ProductNotFoundException
import br.com.blz.testjava.domain.model.ProductModel
import br.com.blz.testjava.domain.repository.ProductRepository
import br.com.blz.testjava.domain.service.ProductService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class ProductQueryService(private val productRepository: ProductRepository, private val messageSource: MessageSource) {

  companion object {
    private val log: Logger = LoggerFactory.getLogger(ProductQueryService::class.java)
  }

  fun findBySku(sku: Long): Mono<ProductModel> = productRepository.findBySku(sku)
    .doFirst { log.info(" ==== try to get a product with sku [{}]", sku) }
    .switchIfEmpty(Mono.error(ProductNotFoundException(messageSource.getMessage("ProductNotFoundException.message",
      arrayOf(sku), LocaleContextHolder.getLocale()))))

}
