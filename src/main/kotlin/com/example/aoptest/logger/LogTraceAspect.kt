package com.example.aoptest.logger

class LogTraceAspect {
    companion object {
        private val logTrace: LogTrace = ThreadLocalLogTrace()
        fun <T> trace(message: String, function: (TraceId) -> T): T {
            val traceStatus: TraceStatus = logTrace.begin(message)
            try {

                val result = function.invoke(traceStatus.traceId)

                logTrace.end(traceStatus)

                return result
            } catch (e: Exception) {
                logTrace.exception(traceStatus, e)
                throw e
            }
        }
    }
}
