package com.mycart.ui.login


import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.mycart.R
import com.mycart.domain.model.User
import com.mycart.ui.common.ProgressBar
import com.mycart.ui.common.Response
import com.mycart.ui.login.viewmodel.LoginViewModel
import org.koin.androidx.compose.get


@Composable
fun LoginScreen(navController: NavHostController, loginViewModel: LoginViewModel = get()) {
    var userEmail by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    val context = LocalContext.current
    var showProgress by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(key1 = context) {
        loginViewModel.responseEvent.collect { event ->
            when (event) {
                is Response.Success -> {
                    val user = event.data as? User
                    println("Login User is $user")
                    Toast.makeText(context, "Login Successful", Toast.LENGTH_LONG).show()
                    userEmail = ""
                    password = ""
                    navController.navigate("store/${user?.userEmail}")
                    showProgress = false

                }
                is Response.Error -> {
                    val errorMessage = event.errorMessage
                    Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                    showProgress = false
                }
                is  Response.Loading ->{
                    showProgress = true
                }
                else -> {
                    showProgress = false
                }
            }
        }
    }
// Getting your activity in a composable function
    val activity = (LocalContext.current as? Activity)
   BackHandler(enabled = true) {
        userEmail = ""
        password = ""
       activity?.finish()
   }
    if(showProgress){
        ProgressBar()
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(

            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ImageItem(R.drawable.ic_baseline_shopping_cart_24)

            OutlinedTextField(value = userEmail, onValueChange = { userEmail = it },
                label = { Text(stringResource(R.string.username_label)) })

            OutlinedTextField(
                value = password, onValueChange = { password = it },
                label = { Text(stringResource(R.string.password_label)) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )
            TextButton(
                onClick = { navController.navigate("forgotPasswordScreen") },
                modifier = Modifier
                    .padding(end = 40.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = stringResource(R.string.forgot_password_label),
                    textAlign = TextAlign.End,
                )
            }

            OutlinedButton(
                onClick = {
                    loginViewModel.fetchLoggedInUserInfo(
                        email = userEmail,
                        password = password
                    )

                },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Blue),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(start = 50.dp, end = 50.dp)
            ) {
                Text(stringResource(R.string.login_btn_label), color = Color.White)
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                TextButton(onClick = {
                    navController.navigate("registrationScreen")
                    }) {
                    Text(stringResource(R.string.register_label))
                }


            }
        }
    }

}

@Composable
fun ImageItem(imageResId: Int) {
    val image = painterResource(imageResId)

    Image(
        painter = image,
        contentDescription = null, // Provide a descriptive content description if needed
        modifier = Modifier
            .size(100.dp)
            .clip(CircleShape)// Adjust the size as needed
    )
}


@Preview
@Composable
fun LoginPreview() {
    //LoginScreen()
}