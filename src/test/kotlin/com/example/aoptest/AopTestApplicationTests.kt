package com.example.aoptest

import com.example.aoptest.main.MainController
import com.example.aoptest.mock.MockAController
import com.example.aoptest.http.ApiException
import com.example.aoptest.main.Request
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
        mainController.function1(Request(1L)).apply {
            println(this.body)
        }
    }

    @Test
    fun mockAFail() {
        assertThrows<ApiException> {
            mainController.function1(Request(2L))
                .apply { println(this) }
        }
    }

    @Test
    fun mockBFail() {
        assertThrows<ApiException> {
            mainController.function1(Request(3L))
                .apply { println(this) }
        }
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
                runBlocking {
                    httpClient.request {
                        contentType(type = ContentType.Application.Json)
                        method = HttpMethod.Post
                        host = "localhost:8080/main/function1"
                        setBody(MockAController.Request(id = 1L))
                    }.apply {
                        println(bodyAsText())
                    }
                }
            }
            async {
                runBlocking {
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

    @Test
    fun cache() {
        mainController.function2(Request(1L)).apply {
            println(this.body)
        }

        mainController.function2(Request(1L)).apply {
            println(this.body)
        }

        mainController.function2(Request(2L)).apply {
            println(this.body)
        }

        mainController.function3(Request(1L)).apply {
            println(this.body)
        }

        mainController.function2(Request(1L)).apply {
            println(this.body)
        }
    }

}
