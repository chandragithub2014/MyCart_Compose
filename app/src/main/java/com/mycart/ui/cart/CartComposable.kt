package com.mycart.ui.cart


import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.FabPosition
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.mycart.domain.model.Cart
import com.mycart.domain.model.Product
import com.mycart.navigator.navigateToProductList
import com.mycart.ui.cart.viewModel.CartViewModel
import com.mycart.ui.common.*
import org.koin.androidx.compose.get

@Composable
fun CartComposable(
    userEmail: String?,
    storeName: String,
    categoryName: String,
    navController: NavHostController,
    cartViewModel: CartViewModel = get()
) {

    val currentState by cartViewModel.state.collectAsState()
    var productList by rememberSaveable { mutableStateOf(listOf<Cart>()) }
    var showProgress by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    var isLogOut by remember { mutableStateOf(false) }



    LaunchedEffect(key1 = Unit) {
        userEmail?.let { email ->
            cartViewModel.fetchProductListFromCart(email, storeName)
        }
    }

    LaunchedEffect(key1 = currentState) {
        when (currentState) {
            is Response.Loading -> {
                showProgress = true
            }

            is Response.Error -> {
                val errorMessage = (currentState as Response.Error).errorMessage
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                showProgress = false
            }

            is Response.SignOut -> {
                navController.navigate("loginScreen") {
                    popUpTo("loginScreen") {
                        inclusive = true
                    }
                }

            }

            is Response.Success -> {
                showProgress = false
            }

            is Response.SuccessList -> {
                when ((currentState as Response.SuccessList).dataType) {
                    DataType.CART -> {
                        productList =
                            (currentState as Response.SuccessList).dataList.filterIsInstance<Cart>()
                        showProgress = false

                    }
                    else -> {

                    }
                }
            }
            is Response.SuccessConfirmation -> {
                showProgress = false
            }
            is Response.Refresh -> {
                showProgress = false
                userEmail?.let { email ->
                    cartViewModel.fetchProductListFromCart(email, storeName)
                }
            }
            else -> {
                showProgress = false
            }
        }
    }

    BackHandler(true) {
        userEmail?.let { email ->
            navController.popBackStack()
            navigateToProductList(navController, categoryName, storeName, email)
        }
    }

    AppScaffold(
        title = "Cart",
        canShowCart = true,
        cartItemCount = cartViewModel.cartCount.value,
        onCartClick = {

        },
        onLogoutClick = {
            // Handle logout action
            isLogOut = true
        },
        floatingActionButton = {

        },
        floatingActionButtonPosition = FabPosition.End
    ) {

        if (showProgress) {
            ProgressBar()
        }
        if (productList.isNotEmpty()) {
            userEmail?.let { email ->
                CartProductList(
                    loggedInUser = email,
                    storeName = storeName,
                    productList = productList,
                    onPlusClick = { productIncrement: Product, canIncrement: Boolean ->
                        userEmail.let { email ->
                            cartViewModel.updateProductQuantity(
                                email,
                                productIncrement,
                                true,
                                productIncrement.productImage
                            )
                        }
                    },
                    onMinusClick = { productDecrement: Product, canIncrement: Boolean ->
                        userEmail.let { email ->
                            cartViewModel.updateProductQuantity(
                                email,
                                productDecrement,
                                false,
                                productDecrement.productImage
                            )
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun CartProductList(
    loggedInUser: String,
    storeName: String,
    productList: List<Cart>,
    onPlusClick: (Product, Boolean) -> Unit,
    onMinusClick: (Product, Boolean) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),

            ) {
            items(items = productList) { cart ->
                CartListItem(loggedInUser, storeName, cart, onPlusClick, onMinusClick)
            }
        }
    }
}
