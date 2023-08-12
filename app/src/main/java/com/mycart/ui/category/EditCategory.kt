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
import com.mycart.domain.model.Category
import com.mycart.domain.model.User
import com.mycart.ui.category.utils.CategoryUtils
import com.mycart.ui.category.viewmodel.CategoryViewModel
import com.mycart.ui.common.InputTextField
import com.mycart.ui.common.ValidationState
import com.mycart.ui.login.ImageItem
import com.mycart.ui.utils.FetchImageFromURLWithPlaceHolder
import org.koin.androidx.compose.get

@Composable
fun EditCategory(
    selectedCategory: String,
    store: String,
    navController: NavHostController,
    categoryViewModel: CategoryViewModel = get()
) {


    var isDeal by rememberSaveable { mutableStateOf(false) }
    var dealInfo by rememberSaveable {
        mutableStateOf("")
    }

    var isSeasonal by rememberSaveable {
        mutableStateOf(false)
    }
    var category by remember { mutableStateOf(Category()) }

    categoryViewModel.fetchCategoryInfoByCategoryNameAndStoreNumber(selectedCategory, store)

    BackHandler(true) {
        navigateToCategory(navController,category.userEmail,category.storeName)
    }
    val context = LocalContext.current
    LaunchedEffect(key1 = context) {
        categoryViewModel.validationEvent.collect { event ->
            when (event) {
                is ValidationState.Success -> {
                    when (val data: Any = event.data) {
                        is Category -> {
                            category = data

                            isSeasonal = category.isSeasonal
                            isDeal = category.isDeal
                            dealInfo = category.dealInfo

                        }
                        else -> {

                        }
                    }
                }
                is ValidationState.SuccessConfirmation -> {
                    val successMessage = event.successMessage
                    Toast.makeText(context, successMessage, Toast.LENGTH_LONG).show()
                    navigateToCategory(navController,category.userEmail,category.storeName)
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
                title = { Text(text = "Edit Category") }
            )
        }
    ) {
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
                    text = "StoreInfo:${category.storeName} _ ${category.storeLoc}",
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
                Text(
                    text = "Category:${category.categoryName} ",
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
                Row(
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(start = 50.dp, end = 50.dp)
                ) {
                    CategoryUtils.fetchCategoryImageUrlByCategory(
                        category.categoryName
                    )?.let { url ->
                        FetchImageFromURLWithPlaceHolder(
                            imageUrl = url
                        )
                    }
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
                        label = stringResource(R.string.deal_info_title),
                        textValue = dealInfo
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(start = 50.dp, end = 50.dp)
                ) {
                    Button(
                        onClick = {
                            categoryViewModel.updateCategory(
                                category.categoryName,
                                category.storeName,
                                isDeal,
                                isSeasonal,
                                dealInfo
                            )
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Blue),
                        modifier = Modifier
                            .weight(1f)
                            .padding(2.dp)
                    ) {
                        Text(text = "Save", color = Color.White)
                    }

                    OutlinedButton(
                        onClick = {
                            navigateToCategory(navController,category.userEmail,category.storeName)
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Blue),
                        modifier = Modifier
                            .weight(1f)
                            .padding(2.dp)
                    ) {
                        Text(text = "Cancel", color = Color.White)
                    }
                }
            }
        }
    }

    fun navigateToCategory(navController: NavHostController, userEmail: String, storeName: String) {
        navController.popBackStack()
        navController.navigate("category/${userEmail}/${storeName}")
    }
}