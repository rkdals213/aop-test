package com.example.aoptest.mock

import com.example.aoptest.logger
import com.example.aoptest.response.ApiException
import com.example.aoptest.response.ErrorResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/mock-b")
class MockBController {

    @PostMapping
    fun function1(@RequestBody request: Request): ResponseEntity<Response> {
        logger.info("mock b start : id = ${request.id}")

        if (request.id == 3L) {
            throw ApiException(
                ErrorResponse(
                    message = "mock-b exception",
                    code = "BBBB",
                    status = 500
                )
            )
        }

        return ResponseEntity.ok().body(Response("Mock B"))
    }

    data class Request(
        val id: Long
    )

    data class Response(
        val data: String
    )
}
