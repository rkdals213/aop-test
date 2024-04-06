package com.example.aoptest

import com.example.aoptest.main.MainController
import com.example.aoptest.mock.MockAController
import com.example.aoptest.http.ApiException
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class AopTestApplicationTests @Autowired constructor(
    private val mainController: MainController
) {

    @Test
    fun success() {
        mainController.function1(MainController.Request(1L)).apply {
            println(this.body)
        }
    }

    @Test
    fun mockAFail() {
        val apiException = assertThrows<ApiException> {
            mainController.function1(MainController.Request(2L))
        }

        println(apiException.errorResponse)
    }

    @Test
    fun mockBFail() {
        val apiException = assertThrows<ApiException> {
            mainController.function1(MainController.Request(3L))
        }

        println("result : " + apiException.errorResponse)
    }

    @Test
    fun test() {
        val httpClient = HttpClient {
            install(ContentNegotiation) {
                jackson()
            }
        }

        runBlocking {
            async {
                httpClient.request {
                    contentType(type = ContentType.Application.Json)
                    method = HttpMethod.Post
                    host = "localhost:8080/main/function1"
                    setBody(MockAController.Request(id = 1L))
                }.apply {
                    println(bodyAsText())
                }
            }
            async {
                httpClient.request {
                    contentType(type = ContentType.Application.Json)
                    method = HttpMethod.Post
                    host = "localhost:8080/main/function1"
                    setBody(MockAController.Request(id = 2L))
                }.apply {
                    println(bodyAsText())
                }
            }

        }

    }

}
