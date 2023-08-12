package com.mycart.ui.category


import android.widget.Toast
import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.mycart.domain.model.Category
import com.mycart.ui.category.viewmodel.CategoryViewModel
import org.koin.androidx.compose.get
import com.mycart.R
import com.mycart.domain.model.SeasonalCategory
import com.mycart.domain.model.User
import com.mycart.ui.category.utils.CategoryUtils
import com.mycart.ui.common.DataType
import com.mycart.ui.common.DisplaySimpleAlertDialog
import com.mycart.ui.common.FloatingActionComposable
import com.mycart.ui.common.ValidationState
import com.mycart.ui.login.viewmodel.LoginViewModel
import com.mycart.ui.utils.DisplayLabel
import com.mycart.ui.utils.DisplayOutLinedLabel
import com.mycart.ui.utils.FetchImageFromURLWithPlaceHolder


@Composable
fun Category(userEmail:String?,storeName:String, navController: NavHostController, categoryViewModel: CategoryViewModel= get()) {
    println("Received UserEmail is....... $userEmail")
    var categoryList by rememberSaveable { mutableStateOf(listOf<Category>()) }
    var dealList by rememberSaveable { mutableStateOf(listOf<Category>()) }
    var seasonalDeals by rememberSaveable { mutableStateOf(listOf<Category>()) }
    var isAdmin by rememberSaveable {
        mutableStateOf(false)
    }
    var showDialog by remember { mutableStateOf(false) }
    var selectedCategory by remember{ mutableStateOf(Category())}

    userEmail?.let {email ->
        categoryViewModel.checkForAdmin(email)
    }

    val context = LocalContext.current
    LaunchedEffect(key1 = context) {
        categoryViewModel.validationEvent.collect { event ->
            when (event) {
                is ValidationState.Success -> {
                   // Toast.makeText(context, "User Detail successful", Toast.LENGTH_LONG).show()
                    when(val data : Any = event.data){
                        is User -> {
                            val user: User = data
                            if(user.isAdmin){
                                categoryViewModel.fetchCategoryForAdmin(user)
                                categoryViewModel.fetchDealsForAdmin(user)
                                categoryViewModel.fetchSeasonalDealsForAdmin(user)
                            }else{

                                categoryViewModel.fetchCategoryByStore(storeName)
                                categoryViewModel.fetchDealsByStore(storeName)
                                categoryViewModel.fetchSeasonalDealsByStore(storeName)
                            }

                        }

                        else ->{
                                 }
                    }
                }

                is ValidationState.SuccessList -> {
                    when (event.dataType) {
                        DataType.CATEGORY -> {
                            categoryList = event.dataList.filterIsInstance<Category>()
                        }
                        DataType.DEALS -> {
                            dealList = event.dataList.filterIsInstance<Category>()
                        }
                        DataType.SEASONALDEALS -> {
                            seasonalDeals = event.dataList.filterIsInstance<Category>()
                        }
                        // Add more cases as needed
                        else -> {}
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
                title = { Text(text = "My Cart") }
            )
        },
                floatingActionButton = {
                    isAdmin  = categoryViewModel.isAdminState.value
                 FloatingActionComposable(categoryViewModel.isAdminState.value){
                         navController.popBackStack()
                         navController.navigate("createCategory/${userEmail}")

                 }
        },
        floatingActionButtonPosition = FabPosition.End
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),

            ) {
                item {
                    DisplayLabel("Hot Deals", modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 10.dp))
                }
                item{
                    DealsComposable(dealList)
                }
                 item {
                     DisplayLabel("Shop By Category", modifier = Modifier
                         .fillMaxWidth()
                         .padding(start = 16.dp, top = 10.dp))
                  }
                  item {
                      CategoryScreen(categoryList,isAdmin,onEdit = { categoryName: String, storeName: String ->
                          navigateToEditCategory(navController,categoryName,storeName)
                      },){ receivedCategory ->
                          showDialog = true
                          selectedCategory = receivedCategory
                         // categoryViewModel.deleteSelectedCategory(category = selectedCategory)
                      }
                  }
                  item {
                    //  SeasonalCategoryComposable()
                      SeasonalCategoryRow(seasonalDeals)
                  }


            }
        }
    }

    if (showDialog) {
        DeleteCategory(selectedCategory,categoryViewModel){
            showDialog = it
        }
        /*DisplaySimpleAlertDialog(
            showDialog = showDialog,
            title = "Delete Category",
            description = "Do you want to Delete selected Category ?",
            positiveButtonTitle = "OK",
            negativeButtonTitle = "Cancel",
            onPositiveButtonClick = {
                // Action to perform when "OK" button is clicked
                categoryViewModel.deleteSelectedCategory(selectedCategory)
            },
            onNegativeButtonClick = {
                showDialog = false

            },
            displayDialog = {
                showDialog = it
            }
        )*/
    }

}



@Composable
fun CategoryScreen(categoryList:List<Category>,isAdmin:Boolean,onEdit:(String,String) -> Unit,onDelete:(Category) -> Unit) {
    CategoryGrid(categoryList,isAdmin,onEdit,onDelete)

}

@Composable
fun SeasonalCategoryTitle(title: String) {
    DisplayOutLinedLabel(label = title)
}

@Composable
fun SeasonalCategoryComposable(categoryViewModel: CategoryViewModel = get()) {
    val categoryList: List<Category> = categoryViewModel.categoryList.value

    LaunchedEffect(Unit) {
        categoryViewModel.fetchSeasonalCategories()
    }
    SeasonalCategoryRow(categoryList)

}

@Composable
fun SeasonalCategoryRow(categories: List<Category>) {
    Column(){
        SeasonalCategoryTitle("Seasonal Specials")
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(categories.size) { index ->
                SeasonalCategoryItem(category = categories[index])
            }
        }
    }

}


@Composable
fun CategoryGrid(categories: List<Category>,isAdmin: Boolean,onEdit:(String,String) -> Unit,onDelete:(Category) -> Unit) {

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.height(300.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(categories.size) { index ->
            CategoryItem(category = categories[index],isAdmin,onEdit,onDelete)
        }
    }

}

@Composable
fun SeasonalCategoryItem(category: Category) {
    Box(
        modifier = Modifier
            .padding(2.dp)
            .height(height = 100.dp)
            .width(width = 100.dp)
            .border(
                BorderStroke(1.dp, Color.LightGray)/*,
                shape = RoundedCornerShape(8.dp)*/
            ),
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
fun CategoryItem(category: Category,isAdmin: Boolean,onEdit:(String,String) -> Unit,onDelete: (Category) -> Unit) {
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //  CategoryImage(category.categoryImage)
            //  CategoryImageFromUrl(category.categoryImage)
            CategoryImageFromURLWithPlaceHolder(category.categoryImage)
            CategoryName(category.categoryName)

        }
        // Placing an icon at the top-right corner
        if(isAdmin) {
            Icon(
                imageVector = Icons.Default.Edit, // Replace with your desired icon
                contentDescription = null, // Provide content description if needed
                modifier = Modifier.align(Alignment.TopEnd).clickable {
                    onEdit(category.categoryName,category.storeName)
                }
            )

            Icon(
                imageVector = Icons.Default.Delete, // Replace with your desired icon
                contentDescription = null, // Provide content description if needed
                modifier = Modifier.align(Alignment.BottomEnd).clickable {
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
fun DeleteCategory(selectedCategory: Category,categoryViewModel: CategoryViewModel, canShowDialog:(Boolean) -> Unit){
    DisplaySimpleAlertDialog(
        title = "Delete Category",
        description = "Do you want to Delete selected Category ?",
        positiveButtonTitle = "OK",
        negativeButtonTitle = "Cancel",
        onPositiveButtonClick = {
            // Action to perform when "OK" button is clicked
            categoryViewModel.deleteSelectedCategory(selectedCategory)
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
fun navigateToEditCategory(navController: NavHostController, categoryName: String, storeName: String) {
    navController.popBackStack()
    navController.navigate("edit/${categoryName}/${storeName}")
}


/*@Composable
fun Category() {
    val scrollState = rememberScrollState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "My Cart") }
            )
        }
    ) {
      Column()
            {
            CategoryScreen()
            SeasonalCategoryTitle("Seasonal Specials")
            SeasonalCategoryComposable()
        }
    }
}*/