package com.mycart.domain.model

import java.util.*

data class SeasonalCategory(val id: UUID = UUID.randomUUID(), val categoryName: String, val categoryImage:String)
