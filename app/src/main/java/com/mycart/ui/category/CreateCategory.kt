package com.mycart.ui.category

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.mycart.R
import com.mycart.domain.model.User
import com.mycart.ui.category.viewmodel.CategoryViewModel
import com.mycart.ui.common.*
import com.mycart.ui.login.ImageItem
import org.koin.androidx.compose.get
import com.mycart.ui.category.utils.*
import com.mycart.ui.category.utils.CategoryUtils.fetchCategoryImageUrlByCategory
import com.mycart.ui.utils.FetchImageFromURLWithPlaceHolder
import java.util.*

@Composable
fun CreateCategory(
    userEmail: String?,
    navController: NavHostController,
    categoryViewModel: CategoryViewModel = get()
) {
    var storeLocation by rememberSaveable { mutableStateOf("") }
    var storeName by rememberSaveable { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val defaultCategoryImageURL =
        fetchCategoryImageUrlByCategory(CategoryUtils.fetchCategoryList()[0])
    var selectedCategory by rememberSaveable {
        mutableStateOf(CategoryUtils.fetchCategoryList()[0])
    }
    var selectedCategoryUrl by rememberSaveable {
        mutableStateOf(defaultCategoryImageURL)
    }
    var isDeal by rememberSaveable { mutableStateOf(false) }
    var dealInfo by rememberSaveable {
        mutableStateOf("")
    }
    var isSeasonal by rememberSaveable {
        mutableStateOf(false)
    }

    var showProgress by rememberSaveable { mutableStateOf(false) }
    val currentState by categoryViewModel.state.collectAsState()
    LaunchedEffect(key1 = Unit) {
        userEmail?.let { email ->
            categoryViewModel.checkForAdmin(email)
        }
    }
    LaunchedEffect(key1 = currentState) {
        when (currentState) {
            is Response.Loading -> {
                showProgress = true
            }
            is Response.Error -> {
                val errorMessage = (currentState as Response.Error).errorMessage
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                showProgress = false
            }

            is Response.SuccessConfirmation -> {
                val successMessage = (currentState as Response.SuccessConfirmation).successMessage
                Toast.makeText(context, successMessage, Toast.LENGTH_LONG).show()
                userEmail?.let { email ->
                    navigateToCategory(navController, email, storeName)
                }


            }
            is Response.Success -> {
                when ((currentState as Response.Success).data) {
                    is User -> {
                        val user = (currentState as Response.Success).data as User
                        storeLocation = user.userStoreLocation
                        storeName = user.userStore
                    }

                    else -> {

                    }
                }
                showProgress = false
            }

            else -> {
                showProgress = false
            }
        }
    }



    BackHandler(true) {
        userEmail?.let { email ->
            navigateToCategory(navController, email, storeName)
        }
    }
    AppScaffold(
        title = "Create Category",
        onCartClick = {

        },
        canShowLogout = false,
        onLogoutClick = {
            // Handle logout action
        },

        )

    {
        if (showProgress) {
            ProgressBar()
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(

                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                ImageItem(R.drawable.ic_baseline_shopping_cart_24)
                Text(
                    text = "StoreInfo:$storeName _ $storeLocation",
                    color = Color.Blue,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(start = 50.dp, end = 50.dp, bottom = 20.dp),
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Blue,
                    )

                )

                ExposedDropDownMenu(options = CategoryUtils.fetchCategoryList()) {
                    println("Selected Items is $it")
                    selectedCategory = it
                }

                Text(
                    text = "Category Image",
                    color = Color.Blue,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(start = 50.dp, end = 50.dp, top = 20.dp),
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Blue,
                    )
                )
                selectedCategoryUrl = fetchCategoryImageUrlByCategory(selectedCategory)
                Row(
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(start = 50.dp, end = 50.dp)
                ) {
                    selectedCategoryUrl?.let { it1 -> FetchImageFromURLWithPlaceHolder(imageUrl = it1) }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .padding(end = 50.dp)
                        .align(Alignment.End)
                ) {
                    Text(text = "Is Seasonal ?")
                    Switch(
                        checked = isSeasonal, onCheckedChange = {
                            isSeasonal = it
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.Blue)
                    )


                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .padding(end = 50.dp)
                        .align(Alignment.End)
                ) {
                    Text(text = "Is deal for category ?")
                    Switch(
                        checked = isDeal, onCheckedChange = {
                            isDeal = it
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.Blue)
                    )


                }
                if (isDeal) {
                    InputTextField(
                        onValueChanged = {
                            dealInfo = it
                        },
                        label = stringResource(R.string.deal_info_title)
                    )
                }
                OutlinedButton(
                    onClick = {
                        showDialog = true
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Blue),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 50.dp, top = 10.dp, end = 50.dp),
                ) {
                    Text(stringResource(R.string.create_title), color = Color.White)
                }

                if (showDialog) {
                    DisplaySimpleAlertDialog(
                        showDialog = showDialog,
                        title = "Create Category",
                        description = "Do you want to Create selected Category ?",
                        positiveButtonTitle = "OK",
                        negativeButtonTitle = "Cancel",
                        onPositiveButtonClick = {
                            // Action to perform when "OK" button is clicked
                            userEmail?.let { email ->
                                selectedCategoryUrl?.let { imageUrl ->

                                    val category = com.mycart.domain.model.Category(
                                        categoryName = selectedCategory,
                                        categoryImage = imageUrl,
                                        userEmail = email,
                                        storeLoc = storeLocation,
                                        storeName = storeName,
                                        deal = isDeal,
                                        dealInfo = dealInfo,
                                        seasonal = isSeasonal
                                    )
                                    categoryViewModel.createCategory(category)
                                }
                            }
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
}


fun navigateToCategory(navController: NavHostController, userEmail: String, storeName: String) {
    println("Store Name........$storeName")
    navController.popBackStack()
    userEmail.let { email ->
        storeName?.let { store ->
            navController.navigate("category/${email}/${store}")
        }
    }

}
