package com.example.aoptest.main

import com.example.aoptest.cache.CacheUser
import com.example.aoptest.http.feign.FeignMockClient
import com.example.aoptest.http.ktor.KtorMockClient
import com.example.aoptest.logger.LogTraceAspect
import com.example.aoptest.mock.MockAController
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Repository
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

    @PostMapping("/function2")
    fun function2(@RequestBody request: Request): ResponseEntity<Response> {
        val response = LogTraceAspect.trace("function2 controller : ${request.id}") {
            mainService.function2(request)
        }

        return ResponseEntity.ok().body(response)
    }

    @PostMapping("/function3")
    fun function3(@RequestBody request: Request): ResponseEntity<Response> {
        val response = LogTraceAspect.trace("function3 controller : ${request.id}") {
            CacheUser.evict("USER", "id:${request.id}") {
                Response("user${request.id} cache remove")
            }
        }

        return ResponseEntity.ok().body(response)
    }
}

@Service
class MainService(
    private val feignMockClient: FeignMockClient,
    private val ktorMockClient: KtorMockClient,
    private val mainRepository: MainRepository
) {

    fun function1(request: Request): Response {
        val response = LogTraceAspect.trace("function1 service : ${request.id}") {
            Thread.sleep(1000)
//            feignMockClient.mockA(MockAController.Request(request.id)).getOrThrow()
            ktorMockClient.mockA(MockAController.Request(request.id)).getOrThrow()
//            MainController.Response("aaa")
        }

        return Response(response.data)
    }

    fun function2(request: Request): Response {
        val response = LogTraceAspect.trace("function2 service : ${request.id}") {
            mainRepository.loadUser(request)
        }

        return Response(response)
    }
}

@Repository
class MainRepository {

    fun loadUser(request: Request): String {
        return CacheUser.cache("USER", "id:${request.id}") {
            "user${request.id}"
        }
    }
}

data class Request(
    val id: Long
)

data class Response(
    val data: String
)
