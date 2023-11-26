package com.mycart.ui.product

import android.text.TextUtils
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavHostController
import com.mycart.R
import com.mycart.domain.model.Product
import com.mycart.domain.model.ProductUnit
import com.mycart.navigator.navigateToProductList
import com.mycart.ui.common.*
import com.mycart.ui.product.viewModel.ProductViewModel
import com.mycart.ui.utils.generateKeywords
import org.koin.androidx.compose.get
import java.util.Locale

@Composable
fun EditProduct(
    selectedCategory: String,
    store: String,
    productName:String,
    userEmail: String?,
    navController: NavHostController,
    productViewModel: ProductViewModel = get()
) {
    var showProgress by rememberSaveable { mutableStateOf(false) }
    var product by remember { mutableStateOf(Product()) }
    val context = LocalContext.current

    var selectedProductName by rememberSaveable { mutableStateOf("") }
    var productCost by rememberSaveable { mutableStateOf("") }
    var productDiscountedCost by rememberSaveable { mutableStateOf("") }
    var productQuantity by rememberSaveable {
        mutableStateOf("")
    }

    var selectedQtyUnits by rememberSaveable {
        mutableStateOf("")
    }

    var productUnitsList by rememberSaveable { mutableStateOf(listOf<ProductUnit>()) }

    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = Unit){
        productViewModel.fetchProductUnitList()
    }
    val currentState by productViewModel.state.collectAsState()
    LaunchedEffect(key1 = currentState ){
        when (currentState) {
            is Response.Loading -> {
                showProgress = true
            }


            is Response.Success -> {
                showProgress = false
                when ((currentState as Response.Success).data) {
                    is Product -> {
                        product = (currentState as Response.Success).data as Product
                        productDiscountedCost = product.productDiscountedPrice
                        selectedProductName = product.productName
                        productCost = product.productOriginalPrice
                        productQuantity = product.productQty.toString()
                       /* isSeasonal = category.seasonal
                        isDeal = category.deal
                        dealInfo = category.dealInfo*/
                    }
                }
            }
            is Response.SuccessList -> {
                when ((currentState as Response.SuccessList).dataType) {
                    DataType.PRODUCT_UNIT_LIST -> {
                        productUnitsList =
                            (currentState as Response.SuccessList).dataList.filterIsInstance<ProductUnit>()
                        if(productUnitsList.isNotEmpty()) {
                            productViewModel.fetchProductInfoByCategoryStore(
                                selectedCategory,
                                store,
                                productName,
                                productUnitsList
                            )
                        }
                        showProgress = false
                    }
                    else -> {
                        showProgress = false
                    }
                }
            }
            is Response.Error -> {
                val errorMessage = (currentState as Response.Error).errorMessage
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                showProgress = false
            }

            is Response.SuccessConfirmation -> {
                val successMessage = (currentState as Response.SuccessConfirmation).successMessage
                Toast.makeText(context, successMessage, Toast.LENGTH_LONG).show()
               // navigateToCategory(navController, category.userEmail, category.storeName)
                userEmail?.let { email ->
                    navigateToProductList(navController, categoryName = selectedCategory,store,email)
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
            navigateToProductList(navController, selectedCategory, store, email)
        }
    }
    AppScaffold(
        title = "Edit Product",
        onCartClick = {

        },
        canShowLogout = false,
        onLogoutClick = {
            // Handle logout action
        },
        canShowBottomNavigation = false,

        )

    {
        if (showProgress) {
            ProgressBar()
        }

        val constraints = decoupledConstraints()
        ConstraintLayout(
            constraints, modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(value = selectedProductName,
                onValueChange = { newValue -> selectedProductName = newValue },
                modifier = Modifier.layoutId("productNameTextField"),
                label = { Text(stringResource(R.string.product_name_hint_text)) })

            Text(
                text = "Product Quantity",
                modifier = Modifier.layoutId("productQuantityText"),
                fontSize = 16.sp,
                color = Color.Blue
            )



           /* if (productViewModel.selectedQtyIndex.value != -1) {
                ExposedDropDownMenu(
                    options = ProductUtils.fetchProductQty(),
                    modifier = Modifier.layoutId("productQtyDropDown"),
                    label = "Qty",
                    selectedItemPosition = productViewModel.selectedQtyIndex.value
                ) {
                    println("Selected Items is $it")
                    selectedQty = it
                }
            }*/
            OutlinedTextField(value = productQuantity,
                onValueChange = { newValue -> productQuantity = newValue },
                modifier = Modifier.layoutId("productQtyDropDown"),
                label = { Text(stringResource(R.string.product_Qty_hint_text)) })

            if (productViewModel.selectedQtyUnitIndex.value != -1) {
                ExposedDropDownMenu(
                    options = productUnitsList.map { it.unit },
                    modifier = Modifier.layoutId("productQtyUnitDropDown"),
                    label = "Units",
                    selectedItemPosition = productViewModel.selectedQtyUnitIndex.value
                ) {
                    println("Selected Items is $it")
                    selectedQtyUnits = it
                }
            }

            OutlinedTextField(value = productCost,
                onValueChange = { newValue -> productCost = newValue },
                modifier = Modifier.layoutId("productCostTextField"),
                label = { Text(stringResource(R.string.product_cost_hint_text)) })

            OutlinedTextField(value = productDiscountedCost,
                onValueChange = { newValue -> productDiscountedCost = newValue },
                modifier = Modifier.layoutId("productDiscountCostTextField"),
                label = { Text(stringResource(R.string.discounted_cost_hint)) })


            OutlinedButton(
                onClick = {
                    showDialog = true
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Blue),
                modifier = Modifier
                    .layoutId("createProductButton")


            ) {
                Text(stringResource(R.string.save_product), color = Color.White)
            }

            OutlinedButton(
                onClick = {
                    navigateToProductList(
                        navController,
                        categoryName = selectedCategory,
                        store,
                        userEmail
                    )
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Blue),
                modifier = Modifier
                    .layoutId("cancelProductButton")
                // .padding(horizontal = 55.dp, vertical = 0.dp)


            ) {
                Text(stringResource(R.string.cancel_product), color = Color.White)
            }


            if (showDialog) {
                DisplaySimpleAlertDialog(
                    showDialog = showDialog,
                    title = "Edit Product",
                    description = "Do you want to Save this Product ?",
                    positiveButtonTitle = "OK",
                    negativeButtonTitle = "Cancel",
                    onPositiveButtonClick = {



                            if(!TextUtils.isEmpty(selectedProductName) && !TextUtils.isEmpty(productCost)) {

                                val editedProduct = Product(
                                    categoryName = product.categoryName,
                                    storeName = product.storeName,
                                    productName = selectedProductName.lowercase(Locale.getDefault()),
                                    productQty = productQuantity.toInt(),
                                    productQtyUnits = selectedQtyUnits,
                                    productOriginalPrice = productCost,
                                    productDiscountedPrice = productDiscountedCost,
                                    productId = product.productId,
                                    keywords = generateKeywords(selectedProductName.lowercase(Locale.getDefault()))
                                )
                                productViewModel.updateSelectedProduct(editedProduct)
                            }else{
                                Toast.makeText(context,"Please fill required Fields",Toast.LENGTH_LONG).show()
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




private fun decoupledConstraints(): ConstraintSet {
    return ConstraintSet {
        val productNameRef = createRefFor("productNameTextField")
        val productQuantityLabelRef = createRefFor("productQuantityText")
        val qtyDropDownRef = createRefFor("productQtyDropDown")
        val qtyUnitDropDownRef = createRefFor("productQtyUnitDropDown")
        val productCostRef = createRefFor("productCostTextField")
        val productDiscountCostRef = createRefFor("productDiscountCostTextField")
        val createProductRef = createRefFor("createProductButton")
        val cancelProductRef = createRefFor("cancelProductButton")
        val horizontalGuideline = createGuidelineFromStart(0.5f)

        constrain(productNameRef) {
            top.linkTo(parent.top, 20.dp)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            width = Dimension.wrapContent
        }

        constrain(productQuantityLabelRef){
            top.linkTo(productNameRef.bottom,10.dp)
            start.linkTo(productNameRef.start)
            end.linkTo(parent.end)
            width = Dimension.fillToConstraints


        }
        constrain(qtyDropDownRef){
            top.linkTo(productQuantityLabelRef.bottom,10.dp)
            start.linkTo(parent.start)
            end.linkTo(parent.end)

        }

        constrain(qtyUnitDropDownRef){
            top.linkTo(qtyDropDownRef.bottom,10.dp)
            start.linkTo(parent.start)
            end.linkTo(parent.end)

        }
        constrain(productCostRef){
            top.linkTo(qtyUnitDropDownRef.bottom,10.dp)
            start.linkTo(parent.start)
            end.linkTo(parent.end)

        }

        constrain(productDiscountCostRef){
            top.linkTo(productCostRef.bottom,10.dp)
            start.linkTo(parent.start)
            end.linkTo(parent.end)

        }

        constrain(createProductRef){
            top.linkTo(productDiscountCostRef.bottom,10.dp)
            start.linkTo(parent.start,50.dp)
            end.linkTo(horizontalGuideline)
            width = Dimension.fillToConstraints

        }

        constrain(cancelProductRef){
            top.linkTo(productDiscountCostRef.bottom,10.dp)
            start.linkTo(horizontalGuideline,5.dp)
            end.linkTo(parent.end,50.dp)
            width = Dimension.fillToConstraints

        }
    }
}