package com.example.aoptest

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jsonMapper
import com.fasterxml.jackson.module.kotlin.kotlinModule
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cloud.openfeign.EnableFeignClients
import java.time.format.DateTimeFormatter

@SpringBootApplication
@EnableFeignClients
@EnableCaching
class AopTestApplication

fun main(args: Array<String>) {
    runApplication<AopTestApplication>(*args)
}

val logger: Logger = LoggerFactory.getLogger(AopTestApplication::class.java)
val defaultObjectMapper: ObjectMapper = jsonMapper { addModule(kotlinModule()) }
val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
