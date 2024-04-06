package com.example.aoptest.http.ktor

import com.example.aoptest.defaultObjectMapper
import com.example.aoptest.http.*
import com.example.aoptest.mock.MockAController
import com.example.aoptest.mock.MockBController
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Component

@Component
class KtorMockClient(
    private val httpLogger: HttpLogger
) : KtorMockClientAbs() {

    fun mockA(request: MockAController.Request): ResponseResult<MockAController.Response> {
        return httpLogger.logging<MockAController.Request, ResponseResult<MockAController.Response>> { logInformation ->
            logInformation.url = "localhost:8080/mock-a"
            logInformation.data = request

            request(
                httpMethod = HttpMethod.Post,
                url = "localhost:8080/mock-a",
                body = request
            )
        }
    }

    fun mockB(request: MockBController.Request): ResponseResult<MockBController.Response> {
        return httpLogger.logging<MockBController.Request, ResponseResult<MockBController.Response>> { logInformation ->
            logInformation.url = "localhost:8080/mock-a"
            logInformation.data = request

            request(
                httpMethod = HttpMethod.Post,
                url = "localhost:8080/mock-b",
                body = request
            )
        }
    }
}

abstract class KtorMockClientAbs {
    protected val httpClient = HttpClient {
        install(ContentNegotiation) {
            jackson()
        }
    }

    protected inline fun <reified T> request(
        httpMethod: HttpMethod,
        url: String,
        body: Any,
        contentType: ContentType = ContentType.Application.Json
    ): ResponseResult<T> {
        return runBlocking {
            httpClient.request {
                contentType(type = contentType)
                method = httpMethod
                host = url
                setBody(body)
            }.responseResult<T>()
        }
    }

    suspend inline fun <reified T> HttpResponse.responseResult(): ResponseResult<T> {
        return when {
            status.isSuccess() -> ResponseResult.Success(body())
            else -> {
                val responseBody = bodyAsText()

                println("responseResult : $responseBody")

                ResponseResult.Failure(
                    when {
                        isErrorResponseDeserializeAble(responseBody) -> defaultObjectMapper.readValue(responseBody, ErrorResponse::class.java)
                        else -> defaultError()
                    }
                )
            }
        }
    }
}


