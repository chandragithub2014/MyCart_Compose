package com.mycart.ui.orders

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.mycart.ui.cart.viewModel.CartViewModel
import org.koin.androidx.compose.get

@Composable
fun OrderComposable(userEmail: String?,
                    storeName: String,
                    navController: NavHostController,
                    cartViewModel: CartViewModel = get()
){

    Text(text = "Here is Order List In Progress")
}