package br.com.blz.testjava.core

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder

@Configuration
class JacksonConfig {

  @Bean
  fun objectMapper(): ObjectMapper = ObjectMapper()
    .registerKotlinModule()
    .registerModule(JavaTimeModule())
    .setSerializationInclusion(JsonInclude.Include.NON_NULL)
    .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)

  @Bean
  fun jackson2JsonEncoder(mapper: ObjectMapper): Jackson2JsonEncoder = Jackson2JsonEncoder(mapper)

  @Bean
  fun jackson2JsonDecoder(mapper: ObjectMapper): Jackson2JsonDecoder = Jackson2JsonDecoder(mapper)

}
