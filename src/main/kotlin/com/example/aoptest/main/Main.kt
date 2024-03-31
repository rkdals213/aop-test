package com.example.aoptest.main

import com.example.aoptest.aop.LoggingStopWatch
import com.example.aoptest.feign.MockClient
import com.example.aoptest.mock.MockAController
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/main")
class MainController(
    private val mainService: MainService
) {

    @PostMapping("/function1")
    fun function1(@RequestBody request: Request): ResponseEntity<Response> {
        val response = LoggingStopWatch.loggingStopWatch {
            mainService.function1(request)
        }

        return ResponseEntity.ok().body(response)
    }

    data class Request(
        val id: Long
    )

    data class Response(
        val data: String
    )
}

@Service
class MainService(
    private val mockClient: MockClient
) {

    fun function1(request: MainController.Request): MainController.Response {
        val response = mockClient.mockA(MockAController.Request(request.id))
            .getOrThrow()

        return MainController.Response(response.data)
    }

}
