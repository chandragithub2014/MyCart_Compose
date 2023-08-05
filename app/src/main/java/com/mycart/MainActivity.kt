package com.mycart

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mycart.ui.category.Category
import com.mycart.ui.category.CategoryScreen
import com.mycart.ui.category.CreateCategory
import com.mycart.ui.login.LoginScreen
import com.mycart.ui.password.ForgotPassword
import com.mycart.ui.register.Register
import com.mycart.ui.theme.MyCartTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyCartTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    // Greeting("Android")
                    //   LoginScreen()
                    // Register()
                    // ForgotPassword()
                    val navController = rememberNavController()
                    Navigator(navController)
                }
            }
        }
    }
}

@Composable
fun Navigator(navHostController: NavHostController) {
    NavHost(navController = navHostController, startDestination = "loginScreen") {
        composable("loginScreen") { LoginScreen(navController = navHostController) }
        composable("registrationScreen") { Register(navController = navHostController) }
        composable("forgotPasswordScreen") { ForgotPassword(navHostController) }
        //composable("category"){ Category() }
        composable(
            "category/{emailId}",
            arguments = listOf(navArgument("emailId") { type = NavType.StringType })
        ) { backStackEntry ->
            backStackEntry.arguments?.getString("emailId")
                ?.let { email -> Category(email, navController = navHostController) }
        }

        composable(
            "createCategory/{emailId}",
            arguments = listOf(navArgument("emailId") { type = NavType.StringType })
        ) { backStackEntry ->
            backStackEntry.arguments?.getString("emailId")?.let { email -> CreateCategory(email) }
        }
    }

}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyCartTheme {
        Greeting("Android")
    }
}