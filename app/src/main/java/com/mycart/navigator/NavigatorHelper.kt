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


fun navigateToCategoryList(
    navController: NavHostController,
    storeName: String,
    userEmail: String?
) {

    userEmail?.let { email ->
        navController.navigate("category/${email}/${storeName}")
    }

}

fun navigateToEditProduct(
    navController: NavHostController,
    categoryName: String,
    storeName: String,
    productName: String,
    userEmail: String?
) {
    navController.popBackStack()
    navController.navigate("editProduct/${categoryName}/${storeName}/${productName}/$userEmail")
}


fun navigateToCart(
    navController: NavHostController,
    categoryName: String,
    storeName: String,
    userEmail: String?
) {
    userEmail?.let { email ->
        navController.popBackStack()
        navController.navigate("cartList/${email}/${storeName}/${categoryName}")
    }

}


fun navigateToStore(
    navController: NavHostController,
    userEmail: String?,
    canLaunchSingleTop: Boolean = false
) {
    userEmail?.let { emailId ->
        navController.popBackStack()
        navController.navigate("store/${emailId}"){
            popUpTo(navController.graph.startDestinationId)
            // You can also use `inclusive = false` if you don't want to include the start destination in the cleared stack
            launchSingleTop = true
            restoreState = true
        }
    }
}

fun navigateToOrders(navController: NavHostController, userEmail: String?, store: String) {
    userEmail?.let { emailId ->
        navController.navigate("orderList/${emailId}/${store}")

    }
}

fun navigateToOrderDetails(navController: NavHostController, userEmail: String?, store: String,orderId:String) {
    userEmail?.let { emailId ->
        navController.navigate("orderDetailList/${emailId}/${store}/${orderId}")

    }
}

