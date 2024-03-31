package com.example.aoptest.response

sealed class ResponseResult<out T> {

    /**
     * HTTP 2xx 정상 응답을 나타내는 데이터 클래스.
     *
     * @param body 응답 본문.
     */
    data class Success<out T>(val body: T) : ResponseResult<T>()

    /**
     * HTTP 4xx, 5xx 오류 응답을 나타내는 데이터 클래스.
     *
     * @param errorResponse 오류 응답 정보.
     */
    data class Failure(val errorResponse: ErrorResponse) : ResponseResult<Nothing>()

    // 성공 여부를 확인하는 속성.
    val isSuccess: Boolean
        get() = this is Success

    // 실패 여부를 확인하는 속성.
    val isFailure: Boolean
        get() = this is Failure

    /**
     * Success 상태일 때 실행될 콜백 함수.
     *
     * @param action 성공 시 실행할 액션.
     */
    inline fun onSuccess(action: (T) -> Unit): ResponseResult<T> {
        if (this is Success) {
            action(body)
        }
        return this
    }

    /**
     * Failure 상태일 때 실행될 콜백 함수.
     *
     * @param action 실패 시 실행할 액션.
     */
    inline fun onFailure(action: (ErrorResponse) -> Unit): ResponseResult<T> {
        if (this is Failure) {
            action(errorResponse)
        }
        return this
    }

    /**
     * Failure 상태일 경우 null을 반환하며, 그 외의 경우 주어진 액션을 수행한다.
     */
    fun getOrNull(): T? = if (this is Success) body else null


    /**
     * * [Success] 경우 [Success.body] 응답
     * * [Failure] 경우 [ApiException] 발생
     *
     */
    fun getOrThrow(): T {
        return when (this) {
            is Success -> body
            is Failure -> throw ApiException(errorResponse = errorResponse)
        }
    }

    /**
     * [Failure] 상태인 경우 [default] 기반으로 반환하고, [Success] 경우 반환 진행
     */
    inline fun <R> getOrDefault(default: R, transform: (T) -> R): R {
        return when (this) {
            is Success -> transform(body)
            is Failure -> default
        }
    }
}

/**
 * 내부 서비스에서 공통으로 사용하는 오류 응답 객체
 */
data class ErrorResponse(
    val message: String,
    val code: String,
    val status: Int
)

class ApiException(
    val errorResponse: ErrorResponse
) : RuntimeException()
