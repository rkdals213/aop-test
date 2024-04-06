package com.example.aoptest.main

import com.example.aoptest.http.feign.FeignMockClient
import com.example.aoptest.http.ktor.KtorMockClient
import com.example.aoptest.logger.LogTraceAspect
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
        val response = LogTraceAspect.trace("function1 controller : ${request.id}") {
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
    private val feignMockClient: FeignMockClient,
    private val ktorMockClient: KtorMockClient
) {

    fun function1(request: MainController.Request): MainController.Response {
        val response = LogTraceAspect.trace("function1 service : ${request.id}") {
            Thread.sleep(1000)
//            feignMockClient.mockA(MockAController.Request(request.id)).getOrThrow()
            ktorMockClient.mockA(MockAController.Request(request.id)).getOrThrow()
//            MainController.Response("aaa")
        }

        return MainController.Response(response.data)
    }

}
