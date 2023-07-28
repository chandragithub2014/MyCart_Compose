package com.mycart.ui.password

import androidx.compose.foundation.layout.*
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.mycart.R
import com.mycart.ui.login.ImageItem

@Composable
fun ForgotPassword(navController: NavHostController){
    var userEmail by rememberSaveable { mutableStateOf("") }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(

            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ImageItem(R.drawable.ic_baseline_shopping_cart_24)
            Text(text = "Enter your email to send password reset link")

            OutlinedTextField(value = userEmail, onValueChange = { userEmail = it },
                label = { Text(stringResource(R.string.username_label)) })

            TextButton(
                onClick = { },
                modifier = Modifier
                    .padding(end = 40.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = stringResource(R.string.reset_password),
                    textAlign = TextAlign.End,
                )
            }
            TextButton(
                onClick = { navController.navigate("loginScreen")},
                modifier = Modifier
                    .padding(end = 40.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = stringResource(R.string.back_to_login),
                    textAlign = TextAlign.End,
                )
            }
            /* Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center) {
            Column{
                TextButton(onClick = { *//*TODO*//* }) {
                    Text(stringResource(R.string.reset_password))
                }

                TextButton(onClick = { *//*TODO*//* }) {
                    Text(stringResource(R.string.back_to_login))
                }

            }
        }*/

        }
    }
}


@Preview
@Composable
fun ResetPasswordPreview(){
    //ForgotPassword()
}