package com.mycart.domain.model

import java.util.*

data class Order( val orderId: String = UUID.randomUUID().toString(),
val loggedInUserEmail:String = "",
val store:String = "",
val orderedDateTime:String ="",
val totalCost:String = "" ,
val orderStatus:String = "In Progress")
