package com.mycart.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mycart.domain.model.Category
import com.mycart.domain.model.User

@Database(entities = [User::class,Category::class], version = 4)
abstract class MyCartDataBase : RoomDatabase() {
    abstract fun myCartDao(): MyCartDAO
}