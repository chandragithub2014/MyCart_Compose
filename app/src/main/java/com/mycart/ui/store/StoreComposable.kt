package com.mycart.ui.store

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.mycart.domain.model.Category
import com.mycart.domain.model.Store
import com.mycart.domain.model.User
import com.mycart.navigator.navigateToCategoryList
import com.mycart.ui.common.*
import com.mycart.ui.store.viewmodel.StoreViewModel
import com.mycart.ui.utils.FetchImageFromDrawable
import org.koin.androidx.compose.get


@Composable
fun StoreList(
    userEmail: String?,
    navController: NavHostController,
    storeViewModel: StoreViewModel = get()
) {

    var storeList by rememberSaveable { mutableStateOf(listOf<Store>()) }
    var showProgress by rememberSaveable { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    LaunchedEffect(key1 = Unit) {
        userEmail?.let { email ->
            storeViewModel.checkForAdminFromFireStore(email)
        }
    }

    val currentState by storeViewModel.state.collectAsState()
    LaunchedEffect(key1 = currentState) {
        when(currentState){
            is Response.Loading -> {
                showProgress = true
            }
            is Response.Error -> {
                val errorMessage = (currentState as Response.Error).errorMessage
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                showProgress = false
            }
            is Response.SignOut -> {
                navController.navigate("loginScreen"){
                    popUpTo("loginScreen") {
                        inclusive = true
                    }
                }

            }

            is Response.Success -> {

                when((currentState as Response.Success).data){
                    is User -> {
                        val user = (currentState as Response.Success).data as User
                        if (user.admin) {
                            storeViewModel.fetchStoreByEmailFromFireStore(user.userEmail)
                        } else {
                            storeViewModel.fetchStoresFromFireStore()
                        }
                    }

                    is Store -> {
                        val store  =  (currentState as Response.Success).data as Store
                        storeList = listOf(store)
                        showProgress = false
                    }
                    else -> {
                        showProgress = false
                    }
                }
            }

            is Response.SuccessList -> {
                when((currentState as Response.SuccessList).dataType){
                    DataType.STORE -> {
                        storeList =   (currentState as Response.SuccessList).dataList.filterIsInstance<Store>()
                        showProgress = false
                    }
                    else -> {
                        showProgress = false
                    }
                }
            }

            else -> {

            }
        }
    }

  /* BackHandler(true) {

    }*/

    AppScaffold(
        title = "Stores",
        canShowBottomNavigation = false,
        onCartClick = {

        },
        onLogoutClick = {
            showDialog = true
        }
    ) {
        if (showProgress) {
            ProgressBar()
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),

                ) {
                items(items = storeList) { store ->
                    userEmail?.let { email ->
                        DisplayStore(store = store, email, navController)
                    }
                }
            }

            if (showDialog) {
                DisplaySimpleAlertDialog(
                    showDialog = showDialog,
                    title = "My Cart",
                    description = "Do you want to Logout ?",
                    positiveButtonTitle = "OK",
                    negativeButtonTitle = "Cancel",
                    onPositiveButtonClick = {
                       storeViewModel.signOut()

                    },
                    onNegativeButtonClick = {
                        showDialog = false

                    },
                    displayDialog = {
                        showDialog = it
                    }
                )
            }
        }
    }
}

@Composable
fun DisplayStore(store: Store, email: String, navController: NavHostController) {
    val name = store.storeName
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 0.dp, bottom = 5.dp, start = 16.dp, end = 16.dp)
            .border(
                BorderStroke(1.dp, Color.LightGray),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(8.dp)
            .clickable {
                navController.navigate("category/${email}/${name}")
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Display the image
        FetchImageFromDrawable(imageName = "ic_baseline_shopping_cart_24")


        // Display the store name
        Text(
            text = store.storeName,
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp)
        )

        FetchImageFromDrawable(imageName = "ic_detail")
    }

}