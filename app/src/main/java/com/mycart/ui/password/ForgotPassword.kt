package com.mycart.ui.password

import android.text.TextUtils
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.mycart.R
import com.mycart.ui.common.AppScaffold
import com.mycart.ui.common.ProgressBar
import com.mycart.ui.common.Response
import com.mycart.ui.login.ImageItem
import com.mycart.ui.password.viewmodel.ResetPasswordViewModel
import com.mycart.ui.register.viewmodel.RegistrationViewModel
import com.mycart.ui.utils.DisplayLabel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get

@Composable
fun ForgotPassword(
    navController: NavHostController,
    resetPasswordViewModel: ResetPasswordViewModel = get()
) {
    var userEmail by rememberSaveable { mutableStateOf("") }
    var showProgress by rememberSaveable { mutableStateOf(false) }
    var showSnackBar by  rememberSaveable { mutableStateOf(false) }
    val coroutineScope: CoroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    var snackBarMessage by rememberSaveable{ mutableStateOf("") }

    LaunchedEffect(key1 = context) {
        resetPasswordViewModel.resetPasswordResponseEvent.collect { event ->
            when (event) {
                is Response.Success -> {
                    showProgress = false
                  //  Toast.makeText(context, event.data as String, Toast.LENGTH_LONG).show()
                    snackBarMessage = event.data as String
                    showSnackBar = true
                }

                is Response.Error -> {
                    showProgress = false
                    val errorMessage = event.errorMessage
                    Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
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
    AppScaffold(
        title = "Reset Password",
        canShowLogout = false,
        canShowBottomNavigation = false,
        onCartClick = {

        },
        onLogoutClick = {

        }
    ) {
        if(showProgress){
            ProgressBar()
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
            if(showSnackBar){
                if(!TextUtils.isEmpty(snackBarMessage)) {
                    coroutineScope.launch {
                        displaySnackBar(
                            snackBarHostState = snackbarHostState,
                            snackBarMessage = snackBarMessage
                        ) { status ->
                            showSnackBar = status
                        }
                    }
                }else{
                    showSnackBar = false
                }
            }
            Column(

                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                ImageItem(R.drawable.ic_baseline_shopping_cart_24)
                DisplayLabel(
                    "Enter your email to send password reset link", modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 10.dp, end = 16.dp),
                    textColor = Color.Blue
                )
                //     Text(text = "Enter your email to send password reset link")

                OutlinedTextField(value = userEmail, onValueChange = { userEmail = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 10.dp, end = 16.dp),
                    label = { Text(stringResource(R.string.username_label)) })

                TextButton(
                    onClick = {
                        if (!TextUtils.isEmpty(userEmail)) {
                                resetPasswordViewModel.resetPassword(userEmail)
                        } else {
                            coroutineScope.launch {
                                displaySnackBar(
                                    snackBarHostState = snackbarHostState,
                                    snackBarMessage = "Email cannot be empty"
                                ){ status ->
                                    showSnackBar = status
                                }
                            }
                        }
                    },
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
                    onClick = { navController.navigate("loginScreen") },
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
}


suspend fun displaySnackBar(
    snackBarHostState: SnackbarHostState,
    snackBarMessage: String,
    actionLabel: String = "OK",
    updateStatus: (Boolean)->Unit
) {
  val result =   snackBarHostState.showSnackbar(
        message = snackBarMessage,
        actionLabel = actionLabel,
        duration = SnackbarDuration.Indefinite
    )
    when (result) {
        SnackbarResult.ActionPerformed -> {
            updateStatus(false)
        }
        SnackbarResult.Dismissed -> {
            updateStatus(false)
        }
    }


}

@Preview
@Composable
fun ResetPasswordPreview() {
    //ForgotPassword()
}


//SnackBar without Scaffold

//https://codingwithrashid.com/show-snackbar-without-scaffold-in-android-jetpack-compose/#:~:text=First%2C%20we%20need%20to%20create,and%20observe%20the%20current%20Snackbar.&text=Next%2C%20we'll%20create%20a,show%20our%20Snackbar%20when%20requested.&text=In%20the%20onClick%20function%20of,showSnackbar%20function%20of%20our%20SnackbarHostState.