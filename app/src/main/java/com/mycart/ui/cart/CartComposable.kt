package com.mycart.ui.cart


import android.text.TextUtils
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.FabPosition
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavHostController
import com.mycart.bottomnavigation.BottomNavigationBar
import com.mycart.bottomnavigation.Screen
import com.mycart.domain.model.Cart
import com.mycart.domain.model.Product
import com.mycart.navigator.navigateToCategoryList
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
    var cartCost by remember {
        mutableStateOf(0)
    }


    LaunchedEffect(key1 = Unit) {
        userEmail?.let { email ->
            cartViewModel.fetchProductListFromCart(email, storeName)
            cartViewModel.calculateCost(email, storeName)
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
            if(!TextUtils.isEmpty(categoryName) && categoryName != "NONE") {
                navigateToProductList(navController, categoryName, storeName, email)
            }else{
                //Navigate to Category List .....
                navigateToCategoryList(navController,storeName,email)
            }
        }
    }

    AppScaffold(
        title = "Cart",
        canShowCart = true,
        userEmail = userEmail?:"",
        store = storeName,
        navController = navController,
        selectedScreen = Screen.Cart,
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
        cartCost = cartViewModel.totalCost.value
        if (showProgress) {
            ProgressBar()
        }
        if (productList.isNotEmpty()) {
            userEmail?.let { email ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp)
                ) {


                    CartProductList(
                        loggedInUser = email,
                        storeName = storeName,
                        productList = productList,
                        cartCost = cartCost,
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
                        },
                        onCheckoutClick = {
                            cartViewModel.performCheckout(
                                loggedInUser = userEmail,
                                storeName = storeName,
                                totalCost = cartCost.toString()
                            )
                        }
                    )

                }
            }

        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(

                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "Cart is Empty!! Please add products",
                        modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
                    )
                }
            }

        }
    }
}

@Composable
fun CartProductList(
    loggedInUser: String,
    storeName: String,
    productList: List<Cart>,
    cartCost: Int,
    onPlusClick: (Product, Boolean) -> Unit,
    onMinusClick: (Product, Boolean) -> Unit,
    onCheckoutClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)

            ) {
                items(items = productList) { cart ->
                    CartListItem(loggedInUser, storeName, cart, onPlusClick, onMinusClick)
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 50.dp), // Adjust padding as needed
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Total Cost:",
                    modifier = Modifier
                        .padding(top = 10.dp)
                    // Takes up available space
                )

                Text(
                    text = "$cartCost (Rs)",
                    modifier = Modifier
                        .padding(top = 10.dp)
                    // Takes up available space
                )
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = {
                        onCheckoutClick()
                    },
                    modifier = Modifier
                        .padding(start = 16.dp)
                ) {
                    Text(text = "Check out")
                }
            }

        }

    }

}






