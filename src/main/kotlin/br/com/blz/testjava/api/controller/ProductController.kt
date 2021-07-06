package br.com.blz.testjava.api.controller

import br.com.blz.testjava.api.controller.request.insert.ProductInsertRequest
import br.com.blz.testjava.api.controller.request.update.ProductUpdateRequest
import br.com.blz.testjava.api.controller.response.inserted.ProductInsertedResponse
import br.com.blz.testjava.api.controller.response.search.ProductSearchResponse
import br.com.blz.testjava.api.controller.response.updated.ProductUpdatedResponse
import br.com.blz.testjava.api.mapper.ProductMapper
import br.com.blz.testjava.domain.service.ProductService
import br.com.blz.testjava.domain.service.query.ProductQueryService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import javax.validation.Valid

@Validated
@RestController
@RequestMapping("/products")
class ProductController(
  private val productService: ProductService,
  private val productQueryService: ProductQueryService,
  private val productMapper: ProductMapper) {

  companion object {
    private val log: Logger = LoggerFactory.getLogger(ProductController::class.java)
  }

  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping(produces = [MediaType.APPLICATION_JSON_VALUE], consumes = [MediaType.APPLICATION_JSON_VALUE])
  fun save(@RequestBody @Valid request: ProductInsertRequest):Mono<ProductInsertedResponse> =
    productService.save(productMapper.toModel(request))
      .doFirst { log.info("==== insert a new product [{}]", request) }
      .map { productMapper.toInsertedResponse(it) }

  @PutMapping(produces = [MediaType.APPLICATION_JSON_VALUE], consumes = [MediaType.APPLICATION_JSON_VALUE], value = ["{sku}"])
  fun update(@PathVariable sku: Long, @RequestBody @Valid request: ProductUpdateRequest):Mono<ProductUpdatedResponse> =
    productService.update(productMapper.toModel(request, sku))
      .doFirst { log.info("==== update a product with id [{}] using a follow request [{}]", sku, request) }
      .map { productMapper.toUpdatedResponse(it) }

  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping(produces = [MediaType.APPLICATION_JSON_VALUE], value = ["{sku}"])
  fun delete(@PathVariable sku: Long):Mono<Void> = productService.delete(sku)
    .doFirst { log.info("==== delete a product with sku [{}]", sku) }

  @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE], value = ["{sku}"])
  fun findById(@PathVariable sku: Long):Mono<ProductSearchResponse> = productQueryService.findBySku(sku)
      .doFirst { log.info("==== get a product with sku [{}]", sku) }
      .map { productMapper.toSearchResponse(it) }
}
