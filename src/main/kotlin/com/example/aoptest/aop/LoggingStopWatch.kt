package com.example.aoptest.aop

import com.example.aoptest.logger
import java.time.Duration
import java.time.LocalDateTime

class LoggingStopWatch {
    companion object {
        fun <T> loggingStopWatch(function: () -> T): T {
            val startAt = LocalDateTime.now()
            logger.info("Start At : $startAt")

            val result = function.invoke()

            val endAt = LocalDateTime.now()

            logger.info("End At : $endAt")
            logger.info("Logic Duration : ${Duration.between(startAt, endAt).toMillis()}ms")

            return result
        }
    }
}
