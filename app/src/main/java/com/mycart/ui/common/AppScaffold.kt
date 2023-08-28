package com.mycart.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun AppScaffold(
    title: String,
    canShowLogout:Boolean = true,
    canShowCart:Boolean = false,
    cartItemCount:Int =0,
    onCartClick: () -> Unit,
    onLogoutClick: () -> Unit,
    floatingActionButton: @Composable (() -> Unit)? = null,
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    content: @Composable () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = title) },
                actions = {
                    if(canShowCart){
                       /* IconButton(onClick = { *//* Handle cart icon click *//* }) {
                            Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = null)

                            if (cartItemCount > 0) {
                                Badge(content = { Text(text = cartItemCount.toString()) })
                            }
                        }*/
                        Box(modifier = Modifier
                            .width(50.dp)
                            .height(50.dp),
                            //.background(Color.Magenta),
                            contentAlignment = Alignment.Center,
                        ){
                            if (cartItemCount > 0) {
                                Badge(content = { Text(text = cartItemCount.toString()) }, modifier = Modifier.align(
                                    Alignment.TopStart))
                            }
                            Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.align(Alignment.Center))
                        }
                    }
                    if(canShowLogout) {
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
        floatingActionButtonPosition = floatingActionButtonPosition
    ){
        content()
    }
}

/*
Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Create Category") }
            )
        }
    )
 */