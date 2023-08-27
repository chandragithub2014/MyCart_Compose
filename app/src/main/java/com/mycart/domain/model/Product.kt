package com.mycart.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*


data class Product(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val productId: String = UUID.randomUUID().toString(),
    val categoryName: String = "",
    val storeName: String = "",
    val userEmail: String = "",
    val productName: String = "",
    val productQty: Int = 0,
    val productDiscountedPrice: String = "",
    val productImage: String = "",
    val productOriginalPrice: String = "",
    val productQtyUnits: String = "",//Kgs,gms,units
    val userSelectedProductQty: String = ""
)
