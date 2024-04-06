package com.example.aoptest.mock

import com.example.aoptest.http.feign.FeignMockClient
import com.example.aoptest.http.ktor.KtorMockClient
import com.example.aoptest.logger
import com.example.aoptest.http.ErrorResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/mock-a")
class MockAController(
    private val feignMockClient: FeignMockClient,
    private val ktorMockClient: KtorMockClient
) {

    @PostMapping
    fun function1(@RequestBody request: Request): ResponseEntity<Any> {
        logger.info("mock a start : id = ${request.id}")

        if (request.id == 2L) {
            return ResponseEntity.status(400)
                .body(
                    ErrorResponse(
                        message = "mock-a exception",
                        code = "AAAA",
                        status = 500
                    )
                )
        }

//        val responseResult = feignMockClient.mockB(MockBController.Request(request.id)).getOrThrow()
        val responseResult = ktorMockClient.mockB(MockBController.Request(request.id)).getOrThrow()

        return ResponseEntity.ok().body(Response(responseResult.data))
    }

    data class Request(
        val id: Long
    )

    data class Response(
        val data: String
    )
}
