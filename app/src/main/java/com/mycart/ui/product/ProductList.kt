package com.mycart.ui.product

import android.text.TextUtils
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavHostController
import com.google.common.io.Files.append
import com.mycart.domain.model.Cart
import com.mycart.domain.model.Category
import com.mycart.domain.model.Product
import com.mycart.domain.model.User
import com.mycart.navigator.navigateToCart
import com.mycart.navigator.navigateToCategoryList
import com.mycart.navigator.navigateToEditProduct
import com.mycart.navigator.navigateToProductList
import com.mycart.ui.category.CategoryImageFromURLWithPlaceHolder
import com.mycart.ui.category.DeleteCategory
import com.mycart.ui.category.ShowLogOutDialog
import com.mycart.ui.category.navigateToEditCategory
import com.mycart.ui.category.viewmodel.CategoryViewModel
import com.mycart.ui.common.*
import com.mycart.ui.product.viewModel.ProductViewModel
import com.mycart.ui.utils.DisplayBorderedLabel
import com.mycart.ui.utils.FetchImageFromDrawable
import com.mycart.ui.utils.FetchImageFromURLWithPlaceHolder
import com.mycart.ui.utils.FetchImageWithBorderFromDrawable
import org.koin.androidx.compose.get

@Composable
fun DisplayProductList(
    userEmail: String?,
    storeName: String,
    category: String,
    navController: NavHostController,
    productViewModel: ProductViewModel = get()
) {
    var productList by rememberSaveable { mutableStateOf(listOf<Product>()) }
    var cartProductList by rememberSaveable { mutableStateOf(listOf<Cart>()) }
    var showProgress by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    val currentState by productViewModel.state.collectAsState()
    var isAdmin by rememberSaveable {
        mutableStateOf(false)
    }
    var categoryInfo by remember { mutableStateOf(Category()) }
    var isLogOut by remember { mutableStateOf(false) }
    var selectedProduct by remember { mutableStateOf(Product()) }
    var showDialog by remember { mutableStateOf(false) }
    var cartCount by remember {
        mutableStateOf(0)
    }
    var maxProductCountWarning by remember { mutableStateOf("") }

    LaunchedEffect(key1 = Unit) {
        userEmail?.let { email ->
            productViewModel.checkForAdmin(email)
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
                        productViewModel.fetchCategoryInfoByCategoryNameAndStoreNumber(
                            category,
                            storeName
                        )
                    }
                    is Category -> {
                        categoryInfo = (currentState as Response.Success).data as Category
                        productViewModel.fetchProductListByCategoryAndStoreNumber(
                            categoryInfo.categoryName,
                            categoryInfo.storeName
                        )
                    }

                }
                showProgress = false
            }

            is Response.SuccessList -> {
                when ((currentState as Response.SuccessList).dataType) {
                    DataType.PRODUCT -> {
                        productList =
                            (currentState as Response.SuccessList).dataList.filterIsInstance<Product>()
                        showProgress = false
                        userEmail?.let { email ->
                            productViewModel.fetchProductListFromCart(email, storeName)
                        }

                    }
                    DataType.CART -> {
                        cartCount = productViewModel.cartCount.value
                        cartProductList =
                            (currentState as Response.SuccessList).dataList.filterIsInstance<Cart>()
                        showProgress = false

                        println("ProductCount and ProductList is $cartCount $cartProductList")

                    }
                    else -> {

                    }
                }
            }
            is Response.SuccessConfirmation -> {
                showProgress = false
                //    userSelectedQuantityInCart = productViewModel.userSelectedQuantity.value

            }
            is Response.Refresh -> {
                showProgress = false
                userEmail?.let { email ->
                    productViewModel.fetchProductListFromCart(email, storeName)
                }
            }
            else -> {
                showProgress = false
            }
        }
    }
    BackHandler(true) {
        userEmail?.let { email ->
            navController.popBackStack()
            navigateToCategoryList(navController, storeName, email)
        }
    }
    AppScaffold(
        title = category,
        canShowCart = true,
        cartItemCount = cartCount,
        onCartClick = {
            navigateToCart(navController, category, storeName, userEmail)
        },
        onLogoutClick = {
            // Handle logout action
            isLogOut = true
        },
        floatingActionButton = {
            isAdmin = productViewModel.isAdminState.value
            FloatingActionComposable(productViewModel.isAdminState.value) {
                navController.popBackStack()
                navController.navigate("createProduct/${userEmail}/${storeName}/${category}")
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) {

        if (productViewModel.maxProductAddCountWarning.value == 3) {
            Toast.makeText(context, "Maximum Count of 3 can be added..", Toast.LENGTH_LONG).show()
        }
        if (showProgress) {
            ProgressBar()
        }
        if (!TextUtils.isEmpty(categoryInfo.categoryImage) && productList.isNotEmpty()) {
            userEmail?.let { email ->


                ProductList(
                    email,
                    category = categoryInfo,
                    cartProductList = cartProductList,
                    productList = productList,
                    isAdmin,
                    onPlusClick = { productIncrement: Product, canIncrement: Boolean ->

                        productViewModel.updateProductQuantity(
                            email,
                            productIncrement,
                            true,
                            categoryInfo.categoryImage
                        )


                    },
                    onMinusClick = { productDecrement: Product, canIncrement: Boolean ->

                        productViewModel.updateProductQuantity(
                            email,
                            productDecrement,
                            false,
                            categoryInfo.categoryImage
                        )

                    },
                    onAddClick = { productToAdd: Product ->

                        productViewModel.updateProductQuantity(
                            email,
                            productToAdd,
                            true,
                            categoryInfo.categoryImage
                        )


                    },
                    onAddToCart = { canAdd ->
                        if (canAdd) {
                            cartCount += 1
                        } else {
                            cartCount -= 1
                        }

                    },
                    onEdit = { selectedProductToEdit: Product ->

                        navigateToEditProduct(
                            navController,
                            selectedProductToEdit.categoryName,
                            selectedProductToEdit.storeName,
                            selectedProductToEdit.productName,
                            email
                        )


                    }
                ) { toDeletedProduct ->
                    selectedProduct = toDeletedProduct
                    showDialog = true
                }
            }
            if (isLogOut) {
                ShowLogOutDialog(productViewModel) {
                    isLogOut = it
                }
            }

            if (showDialog) {
                DeleteProduct(selectedProduct, productViewModel) {
                    showDialog = it
                }
            }
        }
    }

}

@Composable
fun ProductList(
    loggedInUserEmail: String,
    category: Category,
    cartProductList: List<Cart>,
    productList: List<Product>,
    isAdmin: Boolean,
    onPlusClick: (Product, Boolean) -> Unit,
    onMinusClick: (Product, Boolean) -> Unit,
    onAddClick: (Product) -> Unit,
    onEdit: (Product) -> Unit,
    onAddToCart: (Boolean) -> Unit,
    onDelete: (Product) -> Unit
) {
    println("CartProductList is $cartProductList")
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),

            ) {
            items(items = productList) { product ->
                val userSelectedQuantity = getUserSelectedProductQty(
                    cartProductList,
                    loggedInUserEmail,
                    product.productName,
                    product.storeName
                )
                println("UserSelectedQuantity is $userSelectedQuantity")
                ProductListItem(
                    category,
                    product,
                    userSelectedQty = userSelectedQuantity,
                    isAdmin,
                    onPlusClick,
                    onMinusClick,
                    onAddClick,
                    onEdit,
                    onAddToCart,
                    onDelete
                )
            }
        }
    }
}


@Composable
fun ShowLogOutDialog(productViewModel: ProductViewModel, canShowDialog: (Boolean) -> Unit) {
    DisplaySimpleAlertDialog(
        title = "My Cart",
        description = "Do you want to Logout ?",
        positiveButtonTitle = "OK",
        negativeButtonTitle = "Cancel",
        onPositiveButtonClick = {
            productViewModel.signOut()

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
fun DeleteProduct(
    selectedProduct: Product,
    productViewModel: ProductViewModel,
    canShowDialog: (Boolean) -> Unit
) {
    DisplaySimpleAlertDialog(
        title = "Delete Product",
        description = "Do you want to Delete selected Product ?",
        positiveButtonTitle = "OK",
        negativeButtonTitle = "Cancel",
        onPositiveButtonClick = {
            productViewModel.deleteProduct(product = selectedProduct)
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


fun getUserSelectedProductQty(
    cartList: List<Cart>,
    loggedInUserEmail: String,
    productName: String,
    storeName: String
): Int {
    // Filter the cartList based on the criteria
    val filteredCartList = cartList.filter { cart ->
        cart.loggedInUserEmail == loggedInUserEmail &&
                cart.product.productName == productName &&
                cart.product.storeName == storeName
    }

    // If there are matching items, return the userSelectedProductQty of the first item
    if (filteredCartList.isNotEmpty()) {
        return filteredCartList[0].product.userSelectedProductQty
    }

    // Return a default value (e.g., 0) if no matching items were found
    return 0
}


