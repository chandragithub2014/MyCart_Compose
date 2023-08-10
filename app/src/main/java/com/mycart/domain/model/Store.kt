package com.mycart.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "store")
data class Store(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val storeName: String = "",
    val storeLoc: String = "",
    val ownerEmail: String = "",
    val pinCode: String = ""

)


