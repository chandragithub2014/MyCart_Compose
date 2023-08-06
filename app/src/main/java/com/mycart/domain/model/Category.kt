package com.mycart.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

//data class Category(val id: UUID = UUID.randomUUID(), val categoryName: String,val categoryImage:String)
@Entity(tableName = "category")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val categoryName: String,
    val categoryImage: String,
    val userEmail: String = "",
    val storeLoc: String = "",
    val storeName:String = "",
    val isSeasonal:Boolean = false,
    val isDeal:Boolean = false,
    val dealInfo:String = ""
)
