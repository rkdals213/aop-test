package com.example.aoptest.mock

import com.example.aoptest.feign.MockClient
import com.example.aoptest.logger
import com.example.aoptest.response.ApiException
import com.example.aoptest.response.ErrorResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/mock-a")
class MockAController(
    private val mockClient: MockClient
) {

    @PostMapping
    fun function1(@RequestBody request: Request): ResponseEntity<Response> {
        logger.info("mock a start : id = ${request.id}")

        if (request.id == 2L) {
            throw ApiException(
                ErrorResponse(
                    message = "mock-a exception",
                    code = "AAAA",
                    status = 500
                )
            )
        }

        val responseResult = mockClient.mockB(MockBController.Request(request.id))
            .getOrThrow()

        return ResponseEntity.ok().body(Response(responseResult.data))
    }

    data class Request(
        val id: Long
    )

    data class Response(
        val data: String
    )
}
