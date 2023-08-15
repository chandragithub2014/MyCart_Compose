package com.mycart.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userEmail: String = "",
    val userPassword: String = "",
    val confirmPassWord: String = "",
    val admin: Boolean = false,
    val userStore: String = "",
    val userMobile: String = "",
    val userStoreLocation: String = "",
    val userPinCode: String = ""
)
