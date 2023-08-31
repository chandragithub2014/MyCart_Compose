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
import com.mycart.ui.cart.CartComposable
import com.mycart.ui.category.Category
import com.mycart.ui.category.CategoryScreen
import com.mycart.ui.category.CreateCategory
import com.mycart.ui.category.EditCategory
import com.mycart.ui.launcher.LaunchApp
import com.mycart.ui.login.LoginScreen
import com.mycart.ui.password.ForgotPassword
import com.mycart.ui.product.CreateProduct
import com.mycart.ui.product.DisplayProductList
import com.mycart.ui.product.EditProduct
import com.mycart.ui.register.Register
import com.mycart.ui.store.StoreList
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
    NavHost(navController = navHostController, startDestination = "appLauncherScreen") {
        composable("appLauncherScreen") { LaunchApp(navController = navHostController) }
        composable("loginScreen") { LoginScreen(navController = navHostController) }
        composable("registrationScreen") { Register(navController = navHostController) }
        composable("forgotPasswordScreen") { ForgotPassword(navHostController) }
        composable(
            "store/{emailId}",
            arguments = listOf(navArgument("emailId") { type = NavType.StringType })
        ) { backStackEntry ->
            backStackEntry.arguments?.getString("emailId")
                ?.let { email -> StoreList(email, navController = navHostController) }
        }

        composable(
            "createCategory/{emailId}",
            arguments = listOf(navArgument("emailId") { type = NavType.StringType })
        ) { backStackEntry ->
            backStackEntry.arguments?.getString("emailId")
                ?.let { email -> CreateCategory(email, navController = navHostController) }
        }

        composable(
            "category/{emailId}/{store}",
            arguments = listOf(navArgument("emailId") { type = NavType.StringType },
                navArgument("store") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("emailId")
            val storeName = backStackEntry.arguments?.getString("store")
            email?.let { userEmail ->
                storeName?.let { store ->
                    Category(userEmail, store, navController = navHostController)
                }
            }
        }



        composable(
            "edit/{categoryName}/{storeName}",
            arguments = listOf(navArgument("categoryName") { type = NavType.StringType },
                navArgument("storeName") { type = NavType.StringType })
        ) { backStackEntry ->
            val category = backStackEntry.arguments?.getString("categoryName")
            val store = backStackEntry.arguments?.getString("storeName")
            category?.let { categoryName ->
                store?.let { storeName ->
                    EditCategory(categoryName, storeName, navController = navHostController)
                }
            }
        }


        composable(
            "productList/{emailId}/{store}/{category}",
            arguments = listOf(navArgument("emailId") { type = NavType.StringType },
                navArgument("store") { type = NavType.StringType },
                navArgument("category") { type = NavType.StringType }
                )
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("emailId")
            val storeName = backStackEntry.arguments?.getString("store")
            val categoryName = backStackEntry.arguments?.getString("category")
            email?.let { userEmail ->
                storeName?.let { store ->
                    categoryName?.let { category->
                            DisplayProductList(
                                userEmail,
                                store,
                                category,
                                navController = navHostController
                            )
                    }

                }
            }
        }

        composable(
            "createProduct/{emailId}/{store}/{category}",
            arguments = listOf(navArgument("emailId") { type = NavType.StringType },
                navArgument("store") { type = NavType.StringType },
                navArgument("category") { type = NavType.StringType })

        ) {
                backStackEntry ->
            val email = backStackEntry.arguments?.getString("emailId")
            val storeName = backStackEntry.arguments?.getString("store")
            val categoryName = backStackEntry.arguments?.getString("category")
            email?.let { userEmail ->
                storeName?.let { store ->
                    categoryName?.let { category->
                            CreateProduct(
                                userEmail,
                                store,
                                category,
                                navController = navHostController
                            )
                    }

                }
            }
        }

        composable(
            "editProduct/{categoryName}/{storeName}/{productName}/{email}",
            arguments = listOf(navArgument("categoryName") { type = NavType.StringType },
                navArgument("storeName") { type = NavType.StringType },
                navArgument("productName") { type = NavType.StringType },
                navArgument("email") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val category = backStackEntry.arguments?.getString("categoryName")
            val store = backStackEntry.arguments?.getString("storeName")
            val product =  backStackEntry.arguments?.getString("productName")
            val userEmail = backStackEntry.arguments?.getString("email")
            category?.let { categoryName ->
                store?.let { storeName ->
                    product?.let { productName ->
                        userEmail?.let { email ->
                            EditProduct(
                                categoryName,
                                storeName,
                                productName,
                                email,
                                navController = navHostController
                            )
                        }
                    }
                }
            }
        }

        composable(
            "cartList/{emailId}/{store}/{category}",
            arguments = listOf(navArgument("emailId") { type = NavType.StringType },
                navArgument("store") { type = NavType.StringType },
                navArgument("category") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("emailId")
            val storeName = backStackEntry.arguments?.getString("store")
            val categoryName = backStackEntry.arguments?.getString("category")
            email?.let { userEmail ->
                storeName?.let { store ->
                    categoryName?.let { category->
                        CartComposable(
                            userEmail,
                            store,
                            category,
                            navController = navHostController
                        )
                    }

                }
            }
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