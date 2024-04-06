package com.example.aoptest.logger

import com.example.aoptest.logger


interface LogTrace {
    fun begin(message: String): TraceStatus
    fun end(traceStatus: TraceStatus)
    fun exception(traceStatus: TraceStatus, exception: Exception)
}

class ThreadLocalLogTrace : LogTrace {
    private val traceIdHolder: ThreadLocal<TraceId> = ThreadLocal()

    override fun begin(message: String): TraceStatus {
        syncTraceId()
        val traceId = traceIdHolder.get()
        val startTimeMs = System.currentTimeMillis()
        logger.info("[{}] {} {}", traceId.id, addSpace(START_PREFIX, traceId.level), message)

        return TraceStatus(traceId, startTimeMs, message)
    }

    override fun end(traceStatus: TraceStatus) {
        complete(traceStatus, null)
    }

    override fun exception(traceStatus: TraceStatus, exception: Exception) {
        complete(traceStatus, exception)
    }

    private fun complete(traceStatus: TraceStatus, exception: Exception?) {
        val stopTimeMs = System.currentTimeMillis()
        val resultTimeMs = stopTimeMs - traceStatus.startTimeMs
        val traceId = traceStatus.traceId

        if (exception == null) {
            logger.info("[{}] {} {} time={}ms", traceId.id, addSpace(COMPLETE_PREFIX, traceId.level), traceStatus.message, resultTimeMs)
        } else {
            logger.info("[{}] {} {} time={}ms ex={}", traceId.id, addSpace(EX_PREFIX, traceId.level), traceStatus.message, resultTimeMs, exception.toString())
        }

        releaseTraceId()
    }

    private fun syncTraceId() {
        val traceId = traceIdHolder.get() ?: TraceId()

        traceIdHolder.set(traceId.createNextId())
    }

    private fun releaseTraceId() {
        val traceId = traceIdHolder.get() ?: TraceId()

        if (traceId.isFirstLevel) {
            traceIdHolder.remove()
        } else {
            traceIdHolder.set(traceId.createPreviousId())
        }
    }

    private fun addSpace(prefix: String, level: Int): String {
        val sb = StringBuilder()
        for (i in 0 until level) {
            sb.append(
                if ((i == level - 1)) "|$prefix"
                else "|   "
            )
        }
        return sb.toString()
    }

    companion object {
        private const val START_PREFIX = "-->"
        private const val COMPLETE_PREFIX = "<--"
        private const val EX_PREFIX = "<X-"
    }
}
