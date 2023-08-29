package com.mycart.domain.model

import java.util.*

data class Cart(
    val cartId:String  = UUID.randomUUID().toString(),
    var loggedInUserEmail:String  = "",
    val product: Product = Product()
   /* val productId: String = "",
    val categoryName: String = "",
    val storeName: String = "",
    val userEmail: String = "",
    val productName: String = "",
    val productQty: Int = 0,
    val productDiscountedPrice: String = "",
    val productImage: String = "",
    val productOriginalPrice: String = "",
    val productQtyUnits: String = "",//Kgs,gms,units
    val userSelectedProductQty: Int = 0*/
)
