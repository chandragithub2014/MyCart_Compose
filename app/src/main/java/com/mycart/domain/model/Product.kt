package com.mycart.domain.model


import java.util.*


data class Product(
    val id: Long = 0,
    val productId: String = UUID.randomUUID().toString(),
    val categoryName: String = "",
    val categoryImage: String="",
    val storeName: String = "",
    val userEmail: String = "",
    val productName: String = "",
    val productQty: Int = 10,
    val productDiscountedPrice: String = "",
    val productImage: String = "",
    val productOriginalPrice: String = "",
    val productQtyUnits: String = "",//Kgs,gms,units
    val userSelectedProductQty: Int = 0,
    val productPerUnit:String = "",
    val keywords: List<String> = mutableListOf()
)
