package com.mycart.ui.common

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.runtime.Composable

@Composable
fun AppScaffold(
    title: String,
    canShowLogout:Boolean = true,
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