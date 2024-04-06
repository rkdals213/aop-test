package com.example.aoptest

import com.example.aoptest.http.ApiException
import com.example.aoptest.http.ErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
class ExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(ApiException::class)
    protected fun handleApiException(ex: ApiException): ResponseEntity<ErrorResponse> {
        println("exception handler : ${ex.errorResponse}")
        return ResponseEntity.status(HttpStatus.valueOf(ex.errorResponse.status))
            .body(ex.errorResponse)
    }
}
