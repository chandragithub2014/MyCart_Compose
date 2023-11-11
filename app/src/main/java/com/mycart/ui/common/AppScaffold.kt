package com.mycart.ui.common

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.mycart.bottomnavigation.BottomNavigatorComposable
import com.mycart.bottomnavigation.Screen


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun AppScaffold(
    title: String,
    canShowLogout: Boolean = true,
    canShowCart: Boolean = false,
    canShowBottomNavigation: Boolean = true,
    navController: NavHostController = NavHostController(LocalContext.current),
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    userEmail: String = "",
    store: String = "",
    selectedScreen: Screen = Screen.Home,
    cartItemCount: Int = 0,
    onCartClick: () -> Unit,
    onLogoutClick: () -> Unit,
    floatingActionButton: @Composable (() -> Unit)? = null,
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    content: @Composable () -> Unit
) {

    Scaffold(

        topBar = {

            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Text(text = title)

                    }


                },

                modifier = Modifier.wrapContentHeight(),
                actions = {

                    if (canShowCart) {
                        Box(
                            modifier = Modifier
                                .width(50.dp)
                                .height(50.dp)
                                .clickable { onCartClick() },
                            //.background(Color.Magenta),
                            contentAlignment = Alignment.Center,
                        ) {
                            if (cartItemCount > 0) {
                                Badge(
                                    content = { Text(text = cartItemCount.toString()) },
                                    modifier = Modifier.align(
                                        Alignment.TopStart
                                    )
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = null,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                    if (canShowLogout) {
                        IconButton(onClick = onLogoutClick) {
                            Icon(Icons.Default.ExitToApp, contentDescription = null)
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            floatingActionButton?.invoke()
        },
        floatingActionButtonPosition = floatingActionButtonPosition,
        scaffoldState = scaffoldState,
        bottomBar = {
            if (canShowBottomNavigation) {
                BottomAppBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    backgroundColor = Color.White, // Set your desired background color
                    cutoutShape = CircleShape // You can customize the shape as needed
                ) {
                    BottomNavigatorComposable(
                        selectedScreen = selectedScreen,
                        navController = navController,
                        userEmail,
                        store
                    )
                }
            }
        }
    ) {
        content()

    }
}


