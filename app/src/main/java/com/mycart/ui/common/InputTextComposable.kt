package com.mycart.ui.common

import android.text.TextUtils
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.mycart.R

@Composable
fun InputTextField(
    input: String = "",
    onValueChanged: (String) -> Unit,
    label: String,
    error: String="",
    responseError:String = "",
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
    var text by remember {
        mutableStateOf("")
    }
    Column() {
        OutlinedTextField(
            value = text,
            onValueChange = {
                text = it
                onValueChanged(it)},
            label = { Text(label) },
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            modifier = Modifier.fillMaxWidth().padding(start = 50.dp, end = 50.dp)

        )
        if(!TextUtils.isEmpty(error) && isError) {
                ShowErrorText(error)
        }

    }

}

@Composable
fun ShowErrorText(text:String){
    Text(
        text = text,
        color = Color.Red,
        modifier = Modifier.fillMaxWidth().height(50.dp).padding(start = 50.dp, end = 50.dp)

    )
}