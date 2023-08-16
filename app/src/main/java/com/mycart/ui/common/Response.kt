package com.mycart.ui.common



sealed class Response<out T> {
    object Loading:Response<Nothing>()
    data class Success<out T>(val data: T) : Response<T>()
    data class SuccessList<out T>(val dataList: List<T>,val dataType: DataType) : Response<T>()
    data class Error(val errorMessage:String):Response<Nothing>()
    data class SuccessConfirmation(val successMessage:String):Response<Nothing>()
    object SignOut : Response<Nothing>()
    object Login: Response<Nothing>()

}

enum class DataType {
    CATEGORY,
    DEALS,
    SEASONALDEALS,
    STORE
    // Add more types as needed
}
