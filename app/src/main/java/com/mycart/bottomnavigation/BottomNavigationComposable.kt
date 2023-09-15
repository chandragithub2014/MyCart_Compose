package com.mycart.bottomnavigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import com.mycart.R
import com.mycart.navigator.navigateToCategoryList
import com.mycart.navigator.navigateToOrders
import com.mycart.navigator.navigateToStore

@Composable
fun BottomNavigationBar(
    selectedScreen: Screen,
    navController: NavHostController,
    userEmail:String = "",
    store:String  = ""
) {
    Column {
        Spacer(modifier = Modifier.weight(1f))
        BottomNavigation {
            BottomNavigationItem(
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_home_24),
                        contentDescription = "Home"
                    )
                },
                label = { Text("Home") },
                selected = selectedScreen == Screen.Home,
                onClick = {
                    if (selectedScreen != Screen.Home) {
                        navigateToCategoryList(navController,store,userEmail)
                }
                }
            )
           /* BottomNavigationItem(
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_store_24),
                        contentDescription = "Stores"
                    )
                },
                label = { Text("Stores") },
                selected = selectedScreen == Screen.StoreList,
                onClick = {
                    if (selectedScreen != Screen.StoreList) {
                        navigateToStore(navController,userEmail,canLaunchSingleTop = true)

                }
                }
            )*/
            BottomNavigationItem(
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_order_basket_24),
                        contentDescription = "Orders"
                    )
                },
                label = { Text("Orders") },
                selected = selectedScreen == Screen.Orders,
                onClick = {
                    if (selectedScreen != Screen.Orders) {
                       navigateToOrders(navController,userEmail,store)
                    }
                }
            )
        }
    }
}
