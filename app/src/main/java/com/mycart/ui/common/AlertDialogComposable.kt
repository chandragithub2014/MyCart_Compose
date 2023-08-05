package com.mycart.ui.common

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun DisplaySimpleAlertDialog(
    showDialog: Boolean = false,
    displayDialog:(Boolean)->Unit,
    title: String,
    description: String,
    positiveButtonTitle: String,
    negativeButtonTitle: String,
    onPositiveButtonClick: () -> Unit,
    onNegativeButtonClick: () -> Unit
) {
    val openDialog = remember { mutableStateOf(true) }

   // if (openDialog.value) {
        AlertDialog(
            onDismissRequest = { openDialog.value = false
                displayDialog(openDialog.value)},
            buttons = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = {
                            openDialog.value = false
                            onPositiveButtonClick()
                            displayDialog(openDialog.value)
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Blue),
                        modifier = Modifier.weight(1f).padding(2.dp)
                    ) {
                        Text(text = positiveButtonTitle, color = Color.White)
                    }
                    Button(
                        onClick = {
                            openDialog.value = false
                            onNegativeButtonClick()
                            displayDialog(openDialog.value)
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Blue),
                        modifier = Modifier.weight(1f).padding(2.dp)
                    ) {
                        Text(text = negativeButtonTitle, color = Color.White)
                    }
                }

            },
            title = {
                Text(text = title)
            },
            text = {
                Text(description)
            },

            )
            //  }
}