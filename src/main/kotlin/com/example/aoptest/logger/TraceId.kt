package com.example.aoptest.logger

import java.util.UUID

data class TraceId(
    val id: String = UUID.randomUUID().toString().substring(0, 8),
    val level: Int = 0
) {
    val isFirstLevel: Boolean
        get() = level == 0

    fun createNextId(): TraceId {
        return TraceId(id, level + 1)
    }

    fun createPreviousId(): TraceId {
        return TraceId(id, level - 1)
    }
}
