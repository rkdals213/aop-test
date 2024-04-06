package com.example.aoptest.http.feign

import com.example.aoptest.defaultObjectMapper
import com.example.aoptest.http.ApiException
import com.example.aoptest.http.ErrorResponse
import feign.Response
import feign.codec.ErrorDecoder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FeignClientConfiguration {

    @Bean
    fun errorDecoder(): ErrorDecoder {
        return FeignClientErrorDecoder()
    }
}

class FeignClientErrorDecoder : ErrorDecoder {
    override fun decode(methodKey: String, response: Response): Exception {
        val failure = parse(response)

        failure?.let {
            println("error decoder : $failure")
            return ApiException(it)
        }

        return ErrorDecoder.Default().decode(methodKey, response)
    }

    private fun parse(response: Response): ErrorResponse? {
        return runCatching {
            defaultObjectMapper.readValue(response.body().asInputStream(), ErrorResponse::class.java)
        }.getOrNull()
    }
}
