package com.mycart.data.mock

import com.mycart.domain.model.Category
import com.mycart.domain.model.Deal

class MockAPI {

    fun getCategoryDetails(): List<Category> {
        return listOf(
            Category(
                categoryName = "Vegetables",
                categoryImage = "https://firebasestorage.googleapis.com/v0/b/mycart-45ee2.appspot.com/o/Categories%2Fic_vegs.png?alt=media"
            ),
            Category(
                categoryName = "Fruits",
                categoryImage = "https://firebasestorage.googleapis.com/v0/b/mycart-45ee2.appspot.com/o/Categories%2Fic_friut.png?alt=media"
            ),
            Category(
                categoryName = "Groceries",
                categoryImage = "https://firebasestorage.googleapis.com/v0/b/mycart-45ee2.appspot.com/o/Categories%2Fic_grocery.png?alt=media"
            ),
            Category(
                categoryName = "Snacks",
                categoryImage = "https://firebasestorage.googleapis.com/v0/b/mycart-45ee2.appspot.com/o/Categories%2Fic_snacks.png?alt=media"
            ),
            Category(
                categoryName = "Bakery",
                categoryImage = "https://firebasestorage.googleapis.com/v0/b/mycart-45ee2.appspot.com/o/Categories%2Fic_bakery.png?alt=media"
            ),
            Category(
                categoryName = "Cosmetics",
                categoryImage = "https://firebasestorage.googleapis.com/v0/b/mycart-45ee2.appspot.com/o/Categories%2Fic_cosmetics.png?alt=media"
            ),
            Category(
                categoryName = "seaFood",
                categoryImage = "https://firebasestorage.googleapis.com/v0/b/mycart-45ee2.appspot.com/o/Categories%2Fic_fish.png?alt=media"
            ),
            Category(
                categoryName = "meat",
                categoryImage = "https://firebasestorage.googleapis.com/v0/b/mycart-45ee2.appspot.com/o/Categories%2Fic_meat.png?alt=media"
            )
        )
    }

    fun getSeasonalCategoryDetails(): List<Category> {
        return listOf(
            Category(
                categoryName = "Vegetables",
                categoryImage = "https://firebasestorage.googleapis.com/v0/b/mycart-45ee2.appspot.com/o/Categories%2Fic_vegs.png?alt=media"
            ),
            Category(
                categoryName = "Fruits",
                categoryImage = "https://firebasestorage.googleapis.com/v0/b/mycart-45ee2.appspot.com/o/Categories%2Fic_friut.png?alt=media"
            ),
            Category(
                categoryName = "Groceries",
                categoryImage = "https://firebasestorage.googleapis.com/v0/b/mycart-45ee2.appspot.com/o/Categories%2Fic_grocery.png?alt=media"
            ),
            Category(
                categoryName = "Snacks",
                categoryImage = "https://firebasestorage.googleapis.com/v0/b/mycart-45ee2.appspot.com/o/Categories%2Fic_snacks.png?alt=media"
            ),
            Category(
                categoryName = "Bakery",
                categoryImage = "https://firebasestorage.googleapis.com/v0/b/mycart-45ee2.appspot.com/o/Categories%2Fic_bakery.png?alt=media"
            ),
        )
    }

    fun getDeals():List<Deal>{
        return listOf(
            Deal(Category(
                categoryName = "Vegetables",
                categoryImage = "https://firebasestorage.googleapis.com/v0/b/mycart-45ee2.appspot.com/o/Categories%2Fic_vegs.png?alt=media"
            ),"15% off on Vegetables"),

            Deal( Category(
                categoryName = "Fruits",
                categoryImage = "https://firebasestorage.googleapis.com/v0/b/mycart-45ee2.appspot.com/o/Categories%2Fic_friut.png?alt=media"
            ),"20 % off on Fruits"),

           Deal( Category(
                categoryName = "Bakery",
                categoryImage = "https://firebasestorage.googleapis.com/v0/b/mycart-45ee2.appspot.com/o/Categories%2Fic_bakery.png?alt=media"
            ),"5% off on Bakery Items"),

           Deal( Category(
               categoryName = "Snacks",
               categoryImage = "https://firebasestorage.googleapis.com/v0/b/mycart-45ee2.appspot.com/o/Categories%2Fic_snacks.png?alt=media"
           ),"12% off on Snacks")
        )
    }
}