package com.mycart.ui.product

import android.widget.Toast
import androidx.compose.material.FabPosition
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.mycart.domain.model.User
import com.mycart.ui.category.viewmodel.CategoryViewModel
import com.mycart.ui.common.AppScaffold
import com.mycart.ui.common.FloatingActionComposable
import com.mycart.ui.common.Response
import com.mycart.ui.product.viewModel.ProductViewModel
import org.koin.androidx.compose.get

@Composable
fun DisplayProductList(
    userEmail: String?,
    storeName: String,
    category:String,
    navController: NavHostController,
    productViewModel: ProductViewModel = get()
) {

    var showProgress by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    val currentState by productViewModel.state.collectAsState()
    var isAdmin by rememberSaveable {
        mutableStateOf(false)
    }
    LaunchedEffect(key1 = Unit) {
        userEmail?.let { email ->
            productViewModel.checkForAdminFromFireStore(email)
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
                navController.navigate("loginScreen"){
                    popUpTo("loginScreen") {
                        inclusive = true
                    }
                }

            }

            is Response.Success -> {
                when ((currentState as Response.Success).data) {
                    is User -> {
                        val user = (currentState as Response.Success).data as User
                        if (user.admin) {
                           // categoryViewModel.fetchCategoryByStoreFromFireStore(user.userStore)
                        } else {
                           // categoryViewModel.fetchCategoryByStoreFromFireStore(storeName)
                        }
                    }

                }
            }

            else -> {

            }
        }
    }

    AppScaffold(
        title = category,
        onLogoutClick = {
            // Handle logout action

        },
        floatingActionButton = {
            isAdmin = productViewModel.isAdminState.value
            FloatingActionComposable(productViewModel.isAdminState.value) {
                navController.popBackStack()
           //     navController.navigate("createCategory/${userEmail}")
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ){

    }
}