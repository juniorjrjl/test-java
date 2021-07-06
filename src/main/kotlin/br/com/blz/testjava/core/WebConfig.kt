package br.com.blz.testjava.core

import br.com.blz.testjava.core.converter.WarehousesTypeEnumConverter
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.format.FormatterRegistry
import org.springframework.http.codec.ServerCodecConfigurer
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.validation.Validator
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean
import org.springframework.web.reactive.config.WebFluxConfigurer

@Configuration
@Profile("!test")
class WebConfig(private var encoder: Jackson2JsonEncoder,
                private var decoder: Jackson2JsonDecoder,
                private var localValidatorFactoryBean: LocalValidatorFactoryBean): WebFluxConfigurer {


  override fun configureHttpMessageCodecs(configurer: ServerCodecConfigurer) {
    configurer.defaultCodecs().jackson2JsonEncoder(encoder)
    configurer.defaultCodecs().jackson2JsonDecoder(decoder)
  }

  override fun addFormatters(registry: FormatterRegistry) {
    registry.addConverter(WarehousesTypeEnumConverter())
    super.addFormatters(registry)
  }

  override fun getValidator(): Validator? {
    return localValidatorFactoryBean
  }

}
