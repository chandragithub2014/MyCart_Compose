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
import com.mycart.domain.model.Category
import com.mycart.domain.model.Product
import com.mycart.domain.model.User
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
    var showProgress by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    val currentState by productViewModel.state.collectAsState()
    var isAdmin by rememberSaveable {
        mutableStateOf(false)
    }
    var canShowMinusPlusLayout by rememberSaveable { mutableStateOf(false) }
    var categoryInfo by remember { mutableStateOf(Category()) }
    var isLogOut by remember { mutableStateOf(false) }
    var selectedProduct by remember { mutableStateOf(Product())}
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = Unit) {
        userEmail?.let { email ->
            productViewModel.checkForAdminFromFireStore(email)
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
                        /*if (user.admin) {
                           // categoryViewModel.fetchCategoryByStoreFromFireStore(user.userStore)
                        } else {
                           // categoryViewModel.fetchCategoryByStoreFromFireStore(storeName)
                        }*/
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

                    }
                    else -> {

                    }
                }
            }
            is Response.SuccessConfirmation -> {
                showProgress = false
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

        if (showProgress) {
            ProgressBar()
        }
        if (!TextUtils.isEmpty(categoryInfo.categoryImage) && productList.isNotEmpty()) {
            //ProductListItem(categoryInfo)
            ProductList(category = categoryInfo,
                productList = productList,
                isAdmin,
                onPlusClick = {productIncrement:Product,canIncrement:Boolean ->
                    productViewModel.updateProductQuantity(productIncrement,true)
                },
                onMinusClick = {productDecrement:Product,canIncrement:Boolean ->
                    productViewModel.updateProductQuantity(productDecrement,false)
                },
                onAddClick = { productToAdd: Product ->
                   productViewModel.updateProductQuantity(productToAdd,true)
                },

                onEdit = { selectedProductToEdit:Product ->
                userEmail?.let { email ->
                    navigateToEditProduct(navController, selectedProductToEdit.categoryName, selectedProductToEdit.storeName,selectedProductToEdit.productName,email)
                }

            }
            ){ toDeletedProduct ->
                selectedProduct = toDeletedProduct
                showDialog = true
            }
        }
        if (isLogOut) {
            ShowLogOutDialog(productViewModel) {
                isLogOut = it
            }
        }

        if(showDialog){
            DeleteProduct(selectedProduct, productViewModel) {
                showDialog = it
            }
        }
    }
}

@Composable
fun ProductList(category: Category, productList: List<Product>, isAdmin: Boolean,onPlusClick:(Product,Boolean) -> Unit,onMinusClick:(Product,Boolean) -> Unit,onAddClick: (Product) -> Unit,onEdit:(Product) -> Unit,onDelete: (Product) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),

            ) {
            items(items = productList) { product ->
                ProductListItem(category, product, isAdmin,onPlusClick,onMinusClick,onAddClick,onEdit,onDelete)
            }
        }
    }
}

@Composable
fun ProductListItem(category: Category, product: Product, isAdmin: Boolean,onPlusClick:(Product,Boolean) -> Unit,onMinusClick:(Product,Boolean) -> Unit,onAddClick: (Product) -> Unit,onEdit:(Product) -> Unit,onDelete: (Product) -> Unit) {
    var showNumberPlusMinusLayout by remember { mutableStateOf(false) }
    val constraintSet = productListItemConstraints()
    BoxWithConstraints(
        modifier = Modifier
            .padding(top = 0.dp, bottom = 5.dp, start = 5.dp, end = 5.dp)
            .border(
                BorderStroke(1.dp, Color.LightGray),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(8.dp)
    ) {
        ConstraintLayout(constraintSet, modifier = Modifier.fillMaxWidth()) {
            Box(
                Modifier
                    .layoutId("productImage")
                    .width(100.dp)
                    .height(100.dp)
                    .border(
                        BorderStroke(1.dp, Color.LightGray)
                    ),
                contentAlignment = Alignment.Center
            ) {
                //FetchImageFromDrawable(imageName = "ic_baseline_shopping_cart_24")
                FetchImageFromURLWithPlaceHolder(imageUrl = category.categoryImage)
            }

            Text(
                text = product.productName, modifier = Modifier.layoutId("productName"),
                fontSize = 16.sp, color = Color.Blue, fontWeight = FontWeight.Bold
            )
            Text(
                text = product.productQtyUnits, modifier = Modifier.layoutId("productUnit"),
                fontSize = 16.sp, color = Color.Gray
            )
            Text(
                text = product.productDiscountedPrice,
                modifier = Modifier.layoutId("productDiscountedCost"),
                fontSize = 16.sp,
                color = Color.Blue,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = getStrikethroughAnnotatedString(product.productOriginalPrice),
                modifier = Modifier.layoutId("productCost"),
                fontSize = 16.sp
            )

            if (!isAdmin) {
                if (showNumberPlusMinusLayout) {
                    MinusNumberPlusLayout(Modifier.layoutId("minusPlusLayout"),
                        onIncrement = {
                            onPlusClick(product,it)
                        } ,

                        onDecrement = {
                             onMinusClick(product,it)
                        }
                         ) { showAdd ->
                        if (showAdd) {
                            showNumberPlusMinusLayout = false
                        }
                    }
                } else {
                    Button(
                        onClick = {
                            onAddClick(product)
                            showNumberPlusMinusLayout = true },
                        Modifier.layoutId("productAddButton")
                    ) {
                        Text(text = "ADD")

                    }

                }
            }
            if (isAdmin) {

                Icon(
                    imageVector = Icons.Default.Edit, // Replace with your desired icon
                    contentDescription = null, // Provide content description if needed
                    modifier = Modifier
                        .layoutId("editProductIcon")
                        .clickable {
                              onEdit(product)
                        }
                )

                Icon(
                    imageVector = Icons.Default.Delete, // Replace with your desired icon
                    contentDescription = null, // Provide content description if needed
                    modifier = Modifier
                        .layoutId("deleteProductIcon")
                        .clickable {
                            onDelete(product)
                        }
                )
            }
        }
    }
}

@Composable
fun MinusNumberPlusLayout(modifier: Modifier = Modifier, onIncrement:(Boolean) -> Unit,onDecrement:(Boolean)->Unit,showAdd: (Boolean) -> Unit) {
    var quantity by remember { mutableStateOf(1) }
    ConstraintLayout(
        modifier = modifier,
    ) {
        val (minusButton, numberText, plusButton) = createRefs()

        FetchImageWithBorderFromDrawable(
            imageName = "ic_baseline_minus_24",
            modifier = Modifier.constrainAs(minusButton) {
                start.linkTo(parent.start)
                top.linkTo(parent.top)

            }) {
            println("Clicked Minus ")
            quantity -= 1
            if (quantity < 1) {
                quantity = 0
                showAdd(true)
            }
            onDecrement(true)

        }

        DisplayBorderedLabel(label = quantity.toString(), modifier = Modifier
            .constrainAs(numberText) {
                start.linkTo(minusButton.end)
                top.linkTo(parent.top)
            }
            .size(25.dp)
            .border(1.dp, Color.Blue)
            .wrapContentSize(Alignment.Center))


        FetchImageWithBorderFromDrawable(
            imageName = "ic_baseline_add_24",
            modifier = Modifier.constrainAs(plusButton) {
                start.linkTo(numberText.end)
                top.linkTo(parent.top)

            }) {
            println("Clicked Plus")
            quantity += 1
            if (quantity > 3) {
                quantity = 3
            }else{
                onIncrement(true)
            }
        }
    }
}

private fun productListItemConstraints(): ConstraintSet {
    return ConstraintSet {
        val productImageRef = createRefFor("productImage")
        val productNameRef = createRefFor("productName")
        val productUnitRef = createRefFor("productUnit")
        val productCostRef = createRefFor("productCost")
        val productDiscountedCostRef = createRefFor("productDiscountedCost")
        val productAddButtonRef = createRefFor("productAddButton")
        val minusPlusLayoutRef = createRefFor("minusPlusLayout")
        val deleteProductRef = createRefFor("deleteProductIcon")
        val editProductRef = createRefFor("editProductIcon")

        constrain(productImageRef) {
            top.linkTo(parent.top, 5.dp)
            start.linkTo(parent.start, 10.dp)
            width = Dimension.wrapContent

        }

        constrain(productNameRef) {
            top.linkTo(parent.top, 5.dp)
            start.linkTo(productImageRef.end, 5.dp)
            width = Dimension.fillToConstraints

        }

        constrain(productUnitRef) {
            top.linkTo(productNameRef.bottom, 5.dp)
            start.linkTo(productImageRef.end, 5.dp)
            width = Dimension.wrapContent

        }

        constrain(productDiscountedCostRef) {
            top.linkTo(productUnitRef.bottom, 5.dp)
            start.linkTo(productImageRef.end, 5.dp)
            width = Dimension.wrapContent
        }

        constrain(productCostRef) {
            top.linkTo(productUnitRef.bottom, 5.dp)
            start.linkTo(productDiscountedCostRef.end, 5.dp)
            width = Dimension.wrapContent
        }

        constrain(productAddButtonRef) {
            top.linkTo(productCostRef.bottom, 10.dp)
            end.linkTo(parent.end, 5.dp)
            width = Dimension.wrapContent
        }
        constrain(minusPlusLayoutRef) {
            top.linkTo(productCostRef.bottom, 10.dp)
            end.linkTo(parent.end, 5.dp)
            width = Dimension.wrapContent
        }
        constrain(deleteProductRef) {
            top.linkTo(productCostRef.bottom, 20.dp)
            end.linkTo(productAddButtonRef.start, 10.dp)
            width = Dimension.wrapContent
        }

        constrain(editProductRef) {
            top.linkTo(productCostRef.bottom, 20.dp)
            end.linkTo(deleteProductRef.start, 10.dp)
            width = Dimension.wrapContent
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

fun getStrikethroughAnnotatedString(input: String): AnnotatedString {
    return AnnotatedString.Builder().apply {
        withStyle(
            style = SpanStyle(
                textDecoration = TextDecoration.LineThrough,
                color = Color.Gray
            )
        ) {
            append("(")
            append(input)
            append(")")
        }
    }.toAnnotatedString()
}