package com.mycart.ui.category

import android.text.TextUtils
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
import com.mycart.domain.model.CategoryInfo
import com.mycart.domain.model.User
import com.mycart.ui.category.viewmodel.CategoryViewModel
import com.mycart.ui.common.*
import com.mycart.ui.login.ImageItem
import org.koin.androidx.compose.get
import com.mycart.ui.category.utils.*
import com.mycart.ui.utils.FetchImageFromURLWithPlaceHolder


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

    var selectedCategory by rememberSaveable {
        mutableStateOf("")
    }
    var selectedCategoryUrl by rememberSaveable {
        mutableStateOf("")
    }
    var newCategory by rememberSaveable {
        mutableStateOf("")
    }
    var isDeal by rememberSaveable { mutableStateOf(false) }
    var dealInfo by rememberSaveable {
        mutableStateOf("")
    }
    var isSeasonal by rememberSaveable {
        mutableStateOf(false)
    }

    var showProgress by rememberSaveable { mutableStateOf(false) }

    var categoryInfoList by rememberSaveable { mutableStateOf(listOf<CategoryInfo>()) }
    var categoryInfoMap by rememberSaveable {
        mutableStateOf(mapOf<String,String>())
    }
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
                        categoryViewModel.fetchCategoryInfoList()
                    }

                    else -> {

                    }
                }
                showProgress = false
            }
            is Response.SuccessList -> {
                when ((currentState as Response.SuccessList).dataType) {
                    DataType.CATEGORY_INFO_LIST -> {
                         categoryInfoList =
                            (currentState as Response.SuccessList).dataList.filterIsInstance<CategoryInfo>()
                        if(categoryInfoList.isNotEmpty()){
                            categoryInfoMap =
                                categoryInfoList.associate { it.categoryName to it.categoryImage }
                        }
                        println("Received CategoryInfo map is $categoryInfoMap")
                        showProgress = false
                    }
                    else -> {

                    }
                }
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
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 60.dp),
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
                Text(
                    text = "Select category from below list ",
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
               if(categoryInfoList.isNotEmpty()) {
                 //  selectedCategory = categoryInfoList[0].categoryName
                   ExposedDropDownMenu(options = categoryInfoList.map { it.categoryName }) {
                       println("Selected Items is $it")
                       selectedCategory = it
                   }
               }

                Text(
                    text = "Category Not in above List?",
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
                OutlinedTextField(value = newCategory, onValueChange = { newCategory = it },modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 50.dp, end = 50.dp),
                    label = { Text("Create New Category ") }, singleLine = true)
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
                selectedCategoryUrl = if(!TextUtils.isEmpty(newCategory)){
                    CategoryUtils.DEFAULT_CATEGORY_IMAGE_URL
                } else{

                    if(categoryInfoMap.isNotEmpty()) {
                        if(selectedCategory.isEmpty()){
                            categoryInfoMap[categoryInfoList[0].categoryName]?:CategoryUtils.DEFAULT_CATEGORY_IMAGE_URL
                        }else {
                            categoryInfoMap[selectedCategory]?:CategoryUtils.DEFAULT_CATEGORY_IMAGE_URL
                        }
                    }
                    else{
                        CategoryUtils.DEFAULT_CATEGORY_IMAGE_URL
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(start = 50.dp, end = 50.dp)
                ) {
                    println("selectedCategoryUrl is $selectedCategoryUrl")
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
                                        categoryName = if (!TextUtils.isEmpty(newCategory)) newCategory else selectedCategory,
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
