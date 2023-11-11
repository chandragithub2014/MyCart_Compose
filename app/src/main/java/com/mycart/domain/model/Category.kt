package com.mycart.domain.model


import java.util.*

//data class Category(val id: UUID = UUID.randomUUID(), val categoryName: String,val categoryImage:String)
//@Entity(tableName = "category")
data class Category(
   // @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val categoryId:String = UUID.randomUUID().toString(),
    val categoryName: String="",
    val categoryImage: String="",
    val userEmail: String = "",
    val storeLoc: String = "",
    val storeName:String = "",
    val seasonal:Boolean = false,
    val deal:Boolean = false,
    val dealInfo:String = ""
)
