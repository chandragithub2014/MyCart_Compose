package com.mycart.ui.product

import android.text.TextUtils
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavHostController
import com.mycart.R
import com.mycart.domain.model.Category
import com.mycart.domain.model.Product
import com.mycart.domain.model.ProductUnit
import com.mycart.navigator.navigateToProductList
import com.mycart.ui.common.*
import com.mycart.ui.product.viewModel.ProductViewModel
import com.mycart.ui.utils.generateKeywords
import org.koin.androidx.compose.get
import java.util.Locale

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CreateProduct(
    userEmail: String?,
    storeName: String,
    category: String,
    navController: NavHostController,
    productViewModel: ProductViewModel = get()
) {
    var productName by rememberSaveable { mutableStateOf("") }
    var productCost by rememberSaveable { mutableStateOf("") }
    var productDiscountedCost by rememberSaveable { mutableStateOf("") }


    var selectedQty by rememberSaveable {
        mutableStateOf("10")
    }

    var selectedQtyUnits by rememberSaveable {
        mutableStateOf("")
    }
    var showDialog by remember { mutableStateOf(false) }

    var showProgress by rememberSaveable { mutableStateOf(false) }
    var categoryInfo by remember { mutableStateOf(Category()) }

    val context = LocalContext.current
    val currentState by productViewModel.state.collectAsState()
    var productUnitsList by rememberSaveable { mutableStateOf(listOf<ProductUnit>()) }
    var productPerUnit by rememberSaveable { mutableStateOf("1") }
    var isPerUnitValueEntered by remember { mutableStateOf(false) }
    LaunchedEffect(key1 = Unit) {
        productViewModel.fetchProductUnitList()
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
                    navigateToProductList(navController, category, storeName, email)
                }


            }

            is Response.Success -> {
                when ((currentState as Response.Success).data) {
                    is Category -> {
                        categoryInfo = (currentState as Response.Success).data as Category

                    }

                }
                showProgress = false
            }

            is Response.SuccessList -> {
                when ((currentState as Response.SuccessList).dataType) {
                    DataType.PRODUCT_UNIT_LIST -> {
                        productUnitsList =
                            (currentState as Response.SuccessList).dataList.filterIsInstance<ProductUnit>()
                        if (productUnitsList.isNotEmpty()) {
                            productViewModel.fetchCategoryInfoByCategoryNameAndStoreNumber(
                                category,
                                storeName
                            )
                        }
                        showProgress = false
                    }

                    else -> {
                        showProgress = false
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
            navController.popBackStack()
            navigateToProductList(navController, category, storeName, email)
        }
    }
    AppScaffold(
        title = category,
        onCartClick = {

        },
        onLogoutClick = {
            // Handle logout action

        },
        canShowBottomNavigation = false

    ) {
        if (showProgress) {
            ProgressBar()
        }
        // BoxWithConstraints {
        val keyboardController = LocalSoftwareKeyboardController.current
        val focusManager = LocalFocusManager.current
        val constraints = decoupledConstraints()
        ConstraintLayout(
            constraints, modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(value = productName, onValueChange = { productName = it },
                modifier = Modifier.layoutId("productNameTextField"),
                label = { Text(stringResource(R.string.product_name_hint_text)) },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                    }
                )
            )

            Text(
                text = "Enter Total Product Quantity",
                modifier = Modifier.layoutId("productQuantityText"),
                fontSize = 16.sp,
                color = Color.Blue
            )

            OutlinedTextField(value = selectedQty, onValueChange = { selectedQty = it },
                modifier = Modifier.layoutId("productQtyDropDown"),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                    }
                )
            )
            if (productUnitsList.isNotEmpty()) {
                ExposedDropDownMenu(
                    options = productUnitsList.map { it.unit },
                    modifier = Modifier.layoutId("productQtyUnitDropDown"),
                    label = "Units"
                ) {
                    println("Selected Items is $it")
                    selectedQtyUnits = it
                }
            }

            OutlinedTextField(value = productCost, onValueChange = { productCost = it },
                modifier = Modifier.layoutId("productCostTextField"),
                label = { Text(stringResource(R.string.product_cost_hint_text)) },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Number // or any other type you need
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        focusManager.moveFocus(FocusDirection.Down)
                    }
                )
            )

            OutlinedTextField(value = productDiscountedCost,
                onValueChange = { productDiscountedCost = it },
                modifier = Modifier.layoutId("productDiscountCostTextField"),
                label = { Text(stringResource(R.string.discounted_cost_hint)) },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Number // or any other type you need
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                    }
                )
            )
            Text(
                text = "Enter Product per unit",
                modifier = Modifier.layoutId("productCostPerUnitLabel"),
                fontSize = 16.sp,
                color = Color.Blue
            )
            OutlinedTextField(value = productPerUnit,
                onValueChange = {
                    productPerUnit = it
                    isPerUnitValueEntered = it.isNotBlank()
                },
                modifier = Modifier
                    .layoutId("productCostPerUnitValue"),
                textStyle = TextStyle(color = Color.Blue, fontSize = 16.sp,fontWeight = FontWeight.Bold ),
                label = {
                    Text(
                        "Enter PerUnit ",
                        color = if (isPerUnitValueEntered) Color.Blue else Color.Red
                    )
                },

                trailingIcon = {
                    Text(
                        text = selectedQtyUnits, // Your prepopulated text
                        color = if (isPerUnitValueEntered) Color.Blue else Color.Red,
                        modifier = Modifier.padding(8.dp) // Adjust the padding as needed
                    )
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Number // or any other type you need
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                    }
                )
            )

            OutlinedButton(
                onClick = {
                    showDialog = true
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Blue),
                modifier = Modifier
                    .layoutId("createProductButton")
                    .fillMaxWidth()
                    .padding(horizontal = 55.dp, vertical = 0.dp)


            ) {
                Text(stringResource(R.string.create_title), color = Color.White)
            }

            if (showDialog) {
                DisplaySimpleAlertDialog(
                    showDialog = showDialog,
                    title = "Create Product",
                    description = "Do you want to add this Product ?",
                    positiveButtonTitle = "OK",
                    negativeButtonTitle = "Cancel",
                    onPositiveButtonClick = {
                        // Action to perform when "OK" button is clicked
                        userEmail?.let { email ->

                            if (!TextUtils.isEmpty(productName) && !TextUtils.isEmpty(productCost) && !TextUtils.isEmpty(
                                    selectedQty
                                )
                            ) {

                                val product = Product(
                                    categoryName = category,
                                    categoryImage = categoryInfo.categoryImage,
                                    storeName = storeName,
                                    userEmail = email,
                                    productName = productName.lowercase(Locale.getDefault()),
                                    productQty = selectedQty.toInt(),
                                    productQtyUnits = selectedQtyUnits,
                                    productOriginalPrice = productCost,
                                    productDiscountedPrice = productDiscountedCost,
                                    productPerUnit = productPerUnit,
                                    keywords = generateKeywords(productName.lowercase(Locale.getDefault()))
                                )
                                productViewModel.createProduct(product)
                            } else {
                                Toast.makeText(
                                    context,
                                    "Please fill required Fields",
                                    Toast.LENGTH_LONG
                                ).show()
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

        //   }
    }
}
//Please check : "https://www.youtube.com/watch?v=FBpiOAiseD0"

private fun decoupledConstraints(): ConstraintSet {
    return ConstraintSet {
        val productNameRef = createRefFor("productNameTextField")
        val productQuantityLabelRef = createRefFor("productQuantityText")
        val qtyDropDownRef = createRefFor("productQtyDropDown")
        val qtyUnitDropDownRef = createRefFor("productQtyUnitDropDown")
        val productCostRef = createRefFor("productCostTextField")
        val productDiscountCostRef = createRefFor("productDiscountCostTextField")
        val createProductRef = createRefFor("createProductButton")
        val costPerUnitLabel = createRefFor("productCostPerUnitLabel")
        val costPerUnitValue = createRefFor("productCostPerUnitValue")

        constrain(productNameRef) {
            top.linkTo(parent.top, 20.dp)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            width = Dimension.wrapContent
        }

        constrain(productQuantityLabelRef) {
            top.linkTo(productNameRef.bottom, 10.dp)
            start.linkTo(productNameRef.start)
            end.linkTo(parent.end)
            width = Dimension.fillToConstraints


        }
        constrain(qtyDropDownRef) {
            top.linkTo(productQuantityLabelRef.bottom, 10.dp)
            start.linkTo(parent.start)
            end.linkTo(parent.end)

        }

        constrain(qtyUnitDropDownRef) {
            top.linkTo(qtyDropDownRef.bottom, 10.dp)
            start.linkTo(parent.start)
            end.linkTo(parent.end)

        }
        constrain(productCostRef) {
            top.linkTo(qtyUnitDropDownRef.bottom, 10.dp)
            start.linkTo(parent.start)
            end.linkTo(parent.end)

        }

        constrain(productDiscountCostRef) {
            top.linkTo(productCostRef.bottom, 10.dp)
            start.linkTo(parent.start)
            end.linkTo(parent.end)

        }
        constrain(costPerUnitLabel) {
            top.linkTo(productDiscountCostRef.bottom, 10.dp)
            start.linkTo(productDiscountCostRef.start)
            end.linkTo(parent.end)
            width = Dimension.fillToConstraints
        }

        constrain(costPerUnitValue) {
            top.linkTo(costPerUnitLabel.bottom, 10.dp)
            start.linkTo(parent.start)
            end.linkTo(parent.end)

        }

        constrain(createProductRef) {
            top.linkTo(costPerUnitValue.bottom, 10.dp)
            start.linkTo(parent.start)
            end.linkTo(parent.end)

        }
    }
}



