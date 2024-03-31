package com.example.aoptest.feign

import com.example.aoptest.defaultObjectMapper
import com.example.aoptest.mock.MockAController
import com.example.aoptest.mock.MockBController
import com.example.aoptest.response.*
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.PostMapping

@FeignClient(
    name = "mockserver",
    url = "http://localhost:8080"
)
interface MockClientFeign {

    @PostMapping("/mock-a")
    fun mockA(request: MockAController.Request): ResponseEntity<String>

    @PostMapping("/mock-b")
    fun mockB(request: MockBController.Request): ResponseEntity<String>

}

@Component
class MockClient(
    private val mockClientFeign: MockClientFeign
) {
    fun mockA(request: MockAController.Request): ResponseResult<MockAController.Response> {
        return mockClientFeign.mockA(request).responseResult<MockAController.Response>()
    }

    fun mockB(request: MockBController.Request): ResponseResult<MockBController.Response> {
        return mockClientFeign.mockB(request).responseResult<MockBController.Response>()
    }
}

inline fun <reified T> ResponseEntity<String>.responseResult(): ResponseResult<T> {
    return when (this.statusCode.is2xxSuccessful) {
        true -> ResponseResult.Success(defaultObjectMapper.readValue(body!!))
        else -> {
            val responseBody = this.body.toString()

            ResponseResult.Failure(
                when {
                    isErrorResponseDeserializeAble(responseBody) -> defaultObjectMapper.readValue(responseBody, ErrorResponse::class.java)
                    else -> ErrorResponse(
                        message = "Unknown Error",
                        code = "CCCC",
                        status = 500
                    )
                }
            )
        }
    }
}

/**
 * 표준 Error Response {"message": "xxx", "code": "C002", "status": 400}의 역직렬화 가능 여부를 확인합니다.
 */
fun isErrorResponseDeserializeAble(responseBody: String): Boolean {
    return when (val rootNode = defaultObjectMapper.readTree(responseBody)) {
        null -> false
        else -> rootNode.path("message").isTextual && rootNode.path("status").isNumber && rootNode.path("code").isTextual
    }
}
