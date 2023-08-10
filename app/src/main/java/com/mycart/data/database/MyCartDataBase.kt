package com.mycart.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mycart.domain.model.Category
import com.mycart.domain.model.User
import com.mycart.domain.model.Store

@Database(entities = [User::class,Category::class,Store::class], version = 5)
abstract class MyCartDataBase : RoomDatabase() {
    abstract fun myCartDao(): MyCartDAO
}