package com.mycart.ui.category


import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.mycart.domain.model.Category
import com.mycart.ui.category.viewmodel.CategoryViewModel
import org.koin.androidx.compose.get
import com.mycart.domain.model.User
import com.mycart.navigator.navigateToCart
import com.mycart.navigator.navigateToProductList
import com.mycart.ui.common.*
import com.mycart.ui.utils.DisplayHeaderLabel
import com.mycart.ui.utils.DisplayLabel
import com.mycart.ui.utils.FetchImageFromURLWithPlaceHolder


@Composable
fun Category(
    userEmail: String?,
    storeName: String,
    navController: NavHostController,
    categoryViewModel: CategoryViewModel = get()
) {

    var categoryList by rememberSaveable { mutableStateOf(listOf<Category>()) }
    var dealList by rememberSaveable { mutableStateOf(listOf<Category>()) }
    var seasonalDeals by rememberSaveable { mutableStateOf(listOf<Category>()) }
    var isAdmin by rememberSaveable {
        mutableStateOf(false)
    }
    var showDialog by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf(Category()) }
    var showProgress by rememberSaveable { mutableStateOf(false) }

    val context = LocalContext.current
    val currentState by categoryViewModel.state.collectAsState()
    var isLogOut by remember { mutableStateOf(false) }
    var cartCount by remember {
        mutableIntStateOf(0)
    }
    var orderCount by remember {
        mutableIntStateOf(0)
    }


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
            is Response.SignOut -> {
                navController.navigate("loginScreen") {
                    popUpTo("loginScreen") {
                        inclusive = true
                    }
                }

            }
            is Response.Success -> {
                when ((currentState as Response.Success).data) {
                    is User -> {
                        val user = (currentState as Response.Success).data as User
                        if (user.admin) {
                            categoryViewModel.fetchCategoryByStoreFromFireStore(user.userStore)
                        } else {
                            categoryViewModel.fetchCategoryByStoreFromFireStore(storeName)
                        }
                    }

                }
            }

            is Response.SuccessList -> {
                when ((currentState as Response.SuccessList).dataType) {
                    DataType.CATEGORY -> {
                        categoryList =
                            (currentState as Response.SuccessList).dataList.filterIsInstance<Category>()
                        showProgress = false
                        println("CategoryList in Composable :::: $categoryList")
                        dealList = categoryList.filter { it.deal }
                        seasonalDeals = categoryList.filter { it.seasonal }

                        userEmail?.let { email ->
                            categoryViewModel.fetchProductListFromCart(email, storeName)
                        }

                    }

                    DataType.DEALS -> {
                        dealList =
                            (currentState as Response.SuccessList).dataList.filterIsInstance<Category>()
                        showProgress = false
                        println("DealList in Composable :::: $dealList")
                    }

                    DataType.SEASONALDEALS -> {
                        seasonalDeals =
                            (currentState as Response.SuccessList).dataList.filterIsInstance<Category>()
                        showProgress = false
                        println("SeasonalDealList in Composable :::: $seasonalDeals")
                    }
                    DataType.CART -> {
                        cartCount = categoryViewModel.cartCount.value
                        showProgress = false
                        if(isAdmin){
                            categoryViewModel.fetchOrderListCountByStore(storeName)
                        }else{
                            userEmail?.let { email ->
                                categoryViewModel.fetchOrderLisCountByLoggedInUser(email)
                            }

                        }
                    }
                    DataType.ORDER -> {
                        orderCount = categoryViewModel.orderCount.value
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


    AppScaffold(
        title = "Category",
        navController = navController,
        userEmail = userEmail?:"",
        store = storeName,
        canShowCart = true,
        cartItemCount = cartCount,
        orderCount = orderCount,
        onCartClick = {
            navigateToCart(navController, "NONE", storeName, userEmail)
        },
        onLogoutClick = {
            // Handle logout action
            isLogOut = true
        },
        floatingActionButton = {
            isAdmin = categoryViewModel.isAdminState.value
            FloatingActionComposable(categoryViewModel.isAdminState.value) {
                navController.popBackStack()
                navController.navigate("createCategory/${userEmail}")
            }
        },
        floatingActionButtonPosition = FabPosition.End
    )

    {
        if (showProgress) {
            ProgressBar()
        }
        Box(modifier = Modifier.fillMaxSize().padding(bottom = 30.dp)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),

                ) {
                item {
                   /* DisplayLabel(
                        "Hot Deals", modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, top = 10.dp)
                    )*/
                    DisplayHeaderLabel("Hot Deals", paddingHorizontal = 10.dp, backgroundColor = Color.Blue, textColor = Color.White)
                }
                item {
                    DealsComposable(dealList,onClick = { categoryName: String, storeName: String ->
                        navigateToProductList(navController, categoryName, storeName, userEmail)
                    })
                }
                item {
                   /* DisplayLabel(
                        "Shop By Category", modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, top = 10.dp)
                    )*/
                    DisplayHeaderLabel("Shop By Category", paddingHorizontal = 10.dp, backgroundColor = Color.Blue, textColor = Color.White)
                }
                item {
                    CategoryScreen(
                        categoryList, isAdmin,
                        onEdit = { categoryName: String, storeName: String ->
                            navigateToEditCategory(navController, categoryName, storeName,userEmail?:"")
                        },
                        onClick = { categoryName: String, storeName: String ->
                            navigateToProductList(navController, categoryName, storeName, userEmail)
                        }
                    ) { receivedCategory ->
                        showDialog = true
                        selectedCategory = receivedCategory
                        // categoryViewModel.deleteSelectedCategory(category = selectedCategory)
                    }
                }
                item {
                    SeasonalCategoryRow(seasonalDeals,onClick = { categoryName: String, storeName: String ->
                        navigateToProductList(navController, categoryName, storeName, userEmail)
                    })
                }


            }
        }
    }

    if (showDialog) {
        DeleteCategory(selectedCategory, categoryViewModel) {
            showDialog = it
        }
    }

    if (isLogOut) {
        ShowLogOutDialog(categoryViewModel) {
            isLogOut = it
        }
    }

}


@Composable
fun CategoryScreen(
    categoryList: List<Category>,
    isAdmin: Boolean,
    onEdit: (String, String) -> Unit,
    onClick: (String, String) -> Unit,
    onDelete: (Category) -> Unit,

    ) {
    CategoryGrid(categoryList, isAdmin, onEdit, onClick, onDelete)

}

@Composable
fun SeasonalCategoryTitle(title: String) {
   // DisplayOutLinedLabel(label = title,Modifier.padding(horizontal = 16.dp, vertical = 5.dp))
    DisplayHeaderLabel(title, paddingHorizontal = 10.dp, backgroundColor = Color.Blue, textColor = Color.White)
}


@Composable
fun SeasonalCategoryRow(categories: List<Category>,onClick: (String, String) -> Unit) {
    Column(modifier = Modifier.padding(bottom = 20.dp)) {
        SeasonalCategoryTitle("Seasonal Specials")
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(categories.size) { index ->
                SeasonalCategoryItem(category = categories[index],onClick)
            }
        }
    }

}


@Composable
fun CategoryGrid(
    categories: List<Category>,
    isAdmin: Boolean,
    onEdit: (String, String) -> Unit,
    onClick: (String, String) -> Unit,
    onDelete: (Category) -> Unit

) {

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.height(300.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(categories.size) { index ->
            CategoryItem(category = categories[index], isAdmin, onEdit, onClick, onDelete)
        }
    }

}

@Composable
fun SeasonalCategoryItem(category: Category,onClick: (String, String) -> Unit) {
    Box(
        modifier = Modifier
            .padding(2.dp)
            .height(height = 100.dp)
            .width(width = 100.dp)
            .border(
                BorderStroke(1.dp, Color.LightGray)/*,
                shape = RoundedCornerShape(8.dp)*/
            )
            .clickable {
                       onClick(category.categoryName,category.storeName)
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //  CategoryImage(category.categoryImage)
            //  CategoryImageFromUrl(category.categoryImage)
            CategoryImageFromURLWithPlaceHolder(category.categoryImage)
            CategoryName(category.categoryName)

        }
    }
}

@Composable
fun CategoryItem(
    category: Category,
    isAdmin: Boolean,
    onEdit: (String, String) -> Unit,
    onClick: (String, String) -> Unit,
    onDelete: (Category) -> Unit

) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .border(
                BorderStroke(1.dp, Color.LightGray)/*,
                shape = RoundedCornerShape(8.dp)*/
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.clickable {
                onClick(category.categoryName, category.storeName)
            },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //  CategoryImage(category.categoryImage)
            //  CategoryImageFromUrl(category.categoryImage)
            CategoryImageFromURLWithPlaceHolder(category.categoryImage)
            CategoryName(category.categoryName)

        }
        // Placing an icon at the top-right corner
        if (isAdmin) {
            Icon(
                imageVector = Icons.Default.Edit, // Replace with your desired icon
                contentDescription = null, // Provide content description if needed
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .clickable {
                        onEdit(category.categoryName, category.storeName)
                    }
            )

            Icon(
                imageVector = Icons.Default.Delete, // Replace with your desired icon
                contentDescription = null, // Provide content description if needed
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .clickable {
                        onDelete(category)
                    }
            )
        }
    }

}

@Composable
fun CategoryName(categoryName: String) {
    DisplayLabel(categoryName)

}

@Composable
fun CategoryImageFromURLWithPlaceHolder(imageUrl: String) {
    FetchImageFromURLWithPlaceHolder(imageUrl = imageUrl)
}

@Composable
fun DeleteCategory(
    selectedCategory: Category,
    categoryViewModel: CategoryViewModel,
    canShowDialog: (Boolean) -> Unit
) {
    DisplaySimpleAlertDialog(
        title = "Delete Category",
        description = "Do you want to Delete selected Category ?",
        positiveButtonTitle = "OK",
        negativeButtonTitle = "Cancel",
        onPositiveButtonClick = {
            categoryViewModel.deleteCategory(selectedCategory)
            canShowDialog(false)
        },
        onNegativeButtonClick = {
            canShowDialog(false)

        },
        displayDialog = {
            canShowDialog(it)
        }
    )
}

@Composable
fun ShowLogOutDialog(categoryViewModel: CategoryViewModel, canShowDialog: (Boolean) -> Unit) {
    DisplaySimpleAlertDialog(
        title = "My Cart",
        description = "Do you want to Logout ?",
        positiveButtonTitle = "OK",
        negativeButtonTitle = "Cancel",
        onPositiveButtonClick = {
            categoryViewModel.signOut()

        },
        onNegativeButtonClick = {
            canShowDialog(false)

        },
        displayDialog = {
            canShowDialog(it)
        }
    )
}

fun navigateToEditCategory(
    navController: NavHostController,
    categoryName: String,
    storeName: String,
    userEmail:String
) {
    navController.popBackStack()
    if(userEmail.isNotEmpty()){
        navController.navigate("edit/${categoryName}/${storeName}/${userEmail}")
    }


}




