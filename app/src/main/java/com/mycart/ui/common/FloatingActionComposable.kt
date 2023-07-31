package com.mycart.ui.common

import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun FloatingActionComposable(isAdmin:Boolean = false){
    if(isAdmin) {
        FloatingActionButton(
            shape = MaterialTheme.shapes.large.copy(CornerSize(percent = 50)),
            backgroundColor = Color.Blue,
            contentColor = Color.White,
            onClick = { }) {
            Icon(Icons.Default.Add, contentDescription = null)
        }
    }
}