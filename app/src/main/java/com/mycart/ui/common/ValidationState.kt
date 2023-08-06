package com.mycart.ui.common

import com.mycart.domain.model.User

sealed class ValidationState<out T> {
    object Loading:ValidationState<Nothing>()
    data class Success<out T>(val data: T) : ValidationState<T>()
    data class SuccessList<out T>(val dataList: List<T>,val dataType: DataType) : ValidationState<T>()
    data class Error(val errorMessage:String):ValidationState<Nothing>()
    data class SuccessConfirmation(val successMessage:String):ValidationState<Nothing>()

}

enum class DataType {
    CATEGORY,
    OTHER_TYPE,
    // Add more types as needed
}
