package com.example.aoptest

import com.example.aoptest.main.MainController
import com.example.aoptest.response.ApiException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class AopTestApplicationTests @Autowired constructor(
    private val mainController: MainController
) {

    @Test
    fun success() {
        mainController.function1(MainController.Request(1L)).apply {
            println(this.body)
        }
    }

    @Test
    fun mockAFail() {
        val apiException = assertThrows<ApiException> {
            mainController.function1(MainController.Request(2L))
        }

        println(apiException.errorResponse)
    }

    @Test
    fun mockBFail() {
        val apiException = assertThrows<ApiException> {
            mainController.function1(MainController.Request(3L))
        }

        println(apiException.errorResponse)
    }

}
