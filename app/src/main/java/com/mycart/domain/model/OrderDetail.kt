package com.mycart.domain.model

import java.util.*

data class OrderDetail(val orderId:String = "",
                       val orderDetailId:String = UUID.randomUUID().toString(),
                       var loggedInUserEmail:String  = "",
                       val product: Product = Product(),
                       val status:String = "In Progress",
                       val additionalMessage:String = ""
)
