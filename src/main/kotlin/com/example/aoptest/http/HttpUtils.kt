package com.example.aoptest.http

import com.example.aoptest.defaultObjectMapper

/**
 * 표준 Error Response {"message": "xxx", "code": "C002", "status": 400}의 역직렬화 가능 여부를 확인합니다.
 */
fun isErrorResponseDeserializeAble(responseBody: String): Boolean {
    return when (val rootNode = defaultObjectMapper.readTree(responseBody)) {
        null -> false
        else -> rootNode.path("message").isTextual && rootNode.path("status").isNumber && rootNode.path("code").isTextual
    }
}


fun defaultError() = ErrorResponse(
    message = "Unknown Error",
    code = "CCCC",
    status = 500
)
