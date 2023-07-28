package com.mycart.domain.model

import java.util.*

data class Category(val id: UUID = UUID.randomUUID(), val categoryName: String,val categoryImage:String)
