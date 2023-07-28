package com.mycart.domain.model

data class User(val userEmail:String="",
                val userPassword:String="",
                val confirmPassWord:String="",
                val isAdmin:Boolean=false,
                val userStore:String="",
                val userMobile:String="",
                val userStoreLocation:String="",
                val userPinCode:String="")
