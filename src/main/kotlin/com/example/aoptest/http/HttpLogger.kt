package com.example.aoptest.http

import com.example.aoptest.dateTimeFormatter
import com.example.aoptest.defaultObjectMapper
import com.example.aoptest.logger.LogTraceAspect
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class HttpLogger {
    fun <T, N> logging(function: (HttpLogInformation<T>) -> N): N {
        val logInformation = HttpLogInformation<T>()

        val startedDateTime = LocalDateTime.now().format(dateTimeFormatter)

        val result = function.invoke(logInformation)

        val finishedDateTime = LocalDateTime.now().format(dateTimeFormatter)

        val message = "Http Request started : {${startedDateTime}} / Request : {${defaultObjectMapper.writeValueAsString(logInformation)}} / finished : {${finishedDateTime}}"

        return LogTraceAspect.trace(message) {
            result
        }
    }


    data class HttpLogInformation<T>(
        var url: String = "",
        var data: T? = null
    )
}
