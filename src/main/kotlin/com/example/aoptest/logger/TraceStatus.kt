package com.example.aoptest.logger

data class TraceStatus(
    val traceId: TraceId,
    val startTimeMs: Long,
    val message: String
)
