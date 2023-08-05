package com.mycart.ui.category

import android.graphics.fonts.FontStyle
import android.text.TextUtils
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mycart.R
import com.mycart.domain.model.User
import com.mycart.ui.category.viewmodel.CategoryViewModel
import com.mycart.ui.common.*
import com.mycart.ui.login.ImageItem
import com.mycart.ui.register.viewmodel.RegistrationEvent
import org.koin.androidx.compose.get
import com.mycart.ui.category.utils.*
import com.mycart.ui.category.utils.CategoryUtils.fetchCategoryImageUrlByCategory
import com.mycart.ui.utils.FetchImageFromURLWithPlaceHolder

@Composable
fun CreateCategory(userEmail:String?,categoryViewModel: CategoryViewModel = get()) {
    var storeLocation by rememberSaveable { mutableStateOf("") }
    var storeName by rememberSaveable { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val defaultCategoryImageURL = fetchCategoryImageUrlByCategory(CategoryUtils.fetchCategoryList()[0])
    var selectedCategory by rememberSaveable {
        mutableStateOf(CategoryUtils.fetchCategoryList()[0])
    }
    var selectedCategoryUrl by rememberSaveable{
        mutableStateOf(defaultCategoryImageURL)
    }
    LaunchedEffect(key1 = context) {
        userEmail?.let {  email ->
            categoryViewModel.checkForAdmin(email)

        }
        categoryViewModel.validationEvent.collect { event ->
            when (event) {
                is ValidationState.Success -> {
                    when(val data : Any = event.data){
                        is User -> {
                            val user: User = data
                            storeLocation = user.userStoreLocation
                            storeName = user.userStore
                        }else ->{


                        }
                    }

                }
                is ValidationState.Error -> {
                    val errorMessage = event.errorMessage
                    Toast.makeText(context,errorMessage, Toast.LENGTH_LONG).show()
                }
                else -> {}
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Create Category") }
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
                    text = "StoreInfo:$storeName _ $storeLocation",
                    color = Color.Blue,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(start = 50.dp, end = 50.dp, bottom = 20.dp),
                    style = TextStyle(fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Blue,
                    )

                )
                
                ExposedDropDownMenu(options = CategoryUtils.fetchCategoryList()){
                    println("Selected Items is $it")
                    selectedCategory = it
                }

                    Text(text = "Category Image",
                        color = Color.Blue,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .padding(start = 50.dp, end = 50.dp,top=20.dp),
                    style = TextStyle(fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Blue,
                       )
                    )
                    selectedCategoryUrl =  fetchCategoryImageUrlByCategory(selectedCategory)
                   Row(horizontalArrangement = Arrangement.Start,
                       modifier = Modifier
                           .fillMaxWidth()
                           .height(50.dp)
                           .padding(start = 50.dp, end = 50.dp)) {
                       selectedCategoryUrl?.let { it1 -> FetchImageFromURLWithPlaceHolder(imageUrl = it1) }
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

                if(showDialog){
                    DisplaySimpleAlertDialog(
                        showDialog = showDialog,
                        title = "Create Category",
                        description = "Do you want to Create selected Category ?",
                        positiveButtonTitle = "OK",
                        negativeButtonTitle = "Cancel",
                        onPositiveButtonClick = {
                            // Action to perform when "OK" button is clicked
                        },
                        onNegativeButtonClick = {
                            // Action to perform when "Cancel" button is clicked
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