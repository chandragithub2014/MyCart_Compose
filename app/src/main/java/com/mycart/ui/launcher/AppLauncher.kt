package com.mycart.ui.launcher

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.mycart.domain.model.User
import com.mycart.ui.common.AppScaffold
import com.mycart.ui.common.ProgressBar
import com.mycart.ui.common.Response
import com.mycart.ui.launcher.viewmodel.AppLauncherViewModel
import kotlinx.coroutines.flow.onStart
import org.koin.androidx.compose.get

@Composable
fun LaunchApp(
    navController: NavHostController,
    appLauncherViewModel: AppLauncherViewModel = get()
) {
    var showProgress by rememberSaveable { mutableStateOf(false) }


    val context = LocalContext.current
    appLauncherViewModel.checkUser()
    val currentState by appLauncherViewModel.state.collectAsState()
    LaunchedEffect(key1 = context) {
        when (currentState) {
            is Response.Loading -> {
                showProgress = true
            }
            is Response.Error -> {
                val errorMessage = (currentState as Response.Error).errorMessage
                Toast.makeText(context, "Error is $errorMessage", Toast.LENGTH_LONG).show()
                showProgress = true
            }
            is Response.Success -> {
                val user = (currentState as Response.Success).data as User
                navigateToStore(navController, user.userEmail)
                showProgress = false
            }

            is Response.Login -> {
                navigateToLogin(navController)
                showProgress = false
            }
            else -> {

            }
        }
    }



    if (showProgress) {
        ProgressBar()
    }

    AppScaffold(
        title = "Home",
        onLogoutClick = {

        }
    ) {


    }


}

fun navigateToLogin(navController: NavHostController) {
    navController.popBackStack()
    navController.navigate("loginScreen")
}

fun navigateToStore(navController: NavHostController, email: String) {
    navController.popBackStack()
    navController.navigate("store/${email}")
}