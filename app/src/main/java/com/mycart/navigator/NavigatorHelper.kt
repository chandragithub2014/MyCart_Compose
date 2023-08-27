package com.mycart.navigator

import androidx.navigation.NavHostController

fun navigateToProductList(
    navController: NavHostController,
    categoryName: String,
    storeName: String,
    userEmail: String?
) {
    userEmail?.let { email ->
        navController.popBackStack()
        navController.navigate("productList/${email}/${storeName}/${categoryName}")
    }

}


fun navigateToCategoryList(navController: NavHostController,storeName: String,userEmail: String?){

    userEmail?.let{email ->
        navController.navigate("category/${email}/${storeName}")
    }

}

fun navigateToEditProduct(
    navController: NavHostController,
    categoryName: String,
    storeName: String,
    productName:String,
    userEmail: String?
) {
    navController.popBackStack()
    navController.navigate("editProduct/${categoryName}/${storeName}/${productName}/$userEmail")
}