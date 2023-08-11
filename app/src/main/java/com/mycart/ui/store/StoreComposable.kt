package com.mycart.ui.store

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.FabPosition
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.runtime.R
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
import com.mycart.ui.category.DealsComposable
import com.mycart.ui.common.DataType
import com.mycart.ui.common.FloatingActionComposable
import com.mycart.ui.common.ValidationState
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
    userEmail?.let { email ->
        storeViewModel.checkForAdmin(email)
    }

    val context = LocalContext.current
    LaunchedEffect(key1 = context) {
        storeViewModel.validationEvent.collect { event ->
            when (event) {
                is ValidationState.Success -> {
                    // Toast.makeText(context, "User Detail successful", Toast.LENGTH_LONG).show()
                    when (val data: Any = event.data) {
                        is User -> {
                            val user: User = data
                            if (user.isAdmin) {
                                storeViewModel.fetchStoreByEmail(user.userEmail)
                            } else {
                                storeViewModel.fetchStores()
                            }


                        }
                        is Store -> {
                            val store: Store = data
                            storeList = listOf(store)
                        }

                        else -> {
                        }
                    }
                }

                is ValidationState.SuccessList -> {
                    when (event.dataType) {
                        DataType.STORE -> {
                            storeList = event.dataList.filterIsInstance<Store>()
                        }
                        else -> {

                        }
                        // Add more cases as needed
                    }

                }
                is ValidationState.Error -> {
                    val errorMessage = event.errorMessage
                    Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                }
                else -> {}
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Stores") }
            )
        },

        ) {
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),

                ) {
                items(items = storeList) { store ->
                    userEmail?.let { email ->
                        DisplayStore(store = store,email,navController)
                    }
                }
            }
        }
    }
}

@Composable
fun DisplayStore(store: Store,email:String,navController: NavHostController) {

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
                navController.navigate("category/${email}/${store.storeName}")
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