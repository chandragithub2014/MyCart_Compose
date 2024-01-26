package com.mycart.ui.emailverify

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.mycart.R
import com.mycart.ui.login.ImageItem


@Composable
fun EmailVerify(navController: NavHostController) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(

            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ImageItem(R.drawable.ic_baseline_shopping_cart_24)
            // Display the store name
            Text(
                text = "An email verification link has been sent to registered email for verification. Please click on link verify email and then Login",
                fontSize = 15.sp,
                modifier = Modifier
                    .padding(start = 16.dp),
                color = Color.Blue

            )

            TextButton(
                onClick = { navigateToLogin(navController)},
                modifier = Modifier
                    .padding(end = 40.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = stringResource(R.string.login_label),
                    textAlign = TextAlign.End,
                )
            }
        }
    }
}

private fun navigateToLogin(navController: NavHostController){
    navController.popBackStack()
    navController.navigate("loginScreen")
}