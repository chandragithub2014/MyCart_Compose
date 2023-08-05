package com.mycart.ui.category.utils

object CategoryUtils {
    val categoryImageMap = mapOf(
        "Vegetables" to "https://firebasestorage.googleapis.com/v0/b/mycart-45ee2.appspot.com/o/Categories%2Fic_vegs.png?alt=media",
        "Fruits" to "https://firebasestorage.googleapis.com/v0/b/mycart-45ee2.appspot.com/o/Categories%2Fic_friut.png?alt=media",
        "Grocery" to "https://firebasestorage.googleapis.com/v0/b/mycart-45ee2.appspot.com/o/Categories%2Fic_grocery.png?alt=media",
        "Snacks" to "https://firebasestorage.googleapis.com/v0/b/mycart-45ee2.appspot.com/o/Categories%2Fic_snacks.png?alt=media",
        "Bakery" to "https://firebasestorage.googleapis.com/v0/b/mycart-45ee2.appspot.com/o/Categories%2Fic_bakery.png?alt=media",
        "Cosmetics" to "https://firebasestorage.googleapis.com/v0/b/mycart-45ee2.appspot.com/o/Categories%2Fic_cosmetics.png?alt=media",
        "SeaFood" to "https://firebasestorage.googleapis.com/v0/b/mycart-45ee2.appspot.com/o/Categories%2Fic_fish.png?alt=media",
        "Meat" to "https://firebasestorage.googleapis.com/v0/b/mycart-45ee2.appspot.com/o/Categories%2Fic_meat.png?alt=media"
    )

    private val categoryList = listOf(
        "Vegetables",
        "Fruits",
        "Grocery",
        "Bakery",
        "Snacks",
        "Cosmetics",
        "SeaFood",
        "Meat"
    )


    fun fetchCategoryImageUrlByCategory(selectedCategory: String) = categoryImageMap[selectedCategory]

    fun fetchCategoryList() = categoryList
}