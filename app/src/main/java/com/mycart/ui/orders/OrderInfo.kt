package com.mycart.ui.orders

import com.mycart.domain.model.Order

sealed class OrderInfo {
    data class OrderList(val orderList:Order, val orderType:String):OrderInfo()
    data class Header(val headerType:String):OrderInfo()
}
