package com.mycart.ui.product.utils


object ProductUtils {
    private val productQtyList = listOf(
        "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"
    )
    private val productQtyUnits = listOf(
        "unit","kgs","gram","litre"
    )
    fun fetchProductQty() = productQtyList
    fun fetchProductQtyInUnits() = productQtyUnits
}
