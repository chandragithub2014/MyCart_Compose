package com.mycart.ui.product

import android.text.TextUtils
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.mycart.R
import com.mycart.domain.model.Category
import com.mycart.domain.model.Product
import com.mycart.navigator.navigateToProductList
import com.mycart.ui.category.navigateToCategory
import com.mycart.ui.common.*
import com.mycart.ui.product.utils.ProductUtils
import com.mycart.ui.product.viewModel.ProductViewModel
import org.koin.androidx.compose.get

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
        mutableStateOf(ProductUtils.fetchProductQty()[0])
    }

    var selectedQtyUnits by rememberSaveable {
        mutableStateOf(ProductUtils.fetchProductQtyInUnits()[0])
    }
    var showDialog by remember { mutableStateOf(false) }

    var showProgress by rememberSaveable { mutableStateOf(false) }

    val context = LocalContext.current
    val currentState by productViewModel.state.collectAsState()

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
                    navigateToProductList(navController, category, storeName,email)
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
            navigateToProductList(navController, category, storeName,email)
        }
    }
    AppScaffold(
        title = category,
        onLogoutClick = {
            // Handle logout action

        }

    ) {
        if (showProgress) {
            ProgressBar()
        }
       // BoxWithConstraints {
            val constraints = decoupledConstraints()
            ConstraintLayout(
                constraints, modifier = Modifier
                    .fillMaxSize()
            ) {
                OutlinedTextField(value = productName, onValueChange = { productName = it },
                    modifier = Modifier.layoutId("productNameTextField"),
                    label = { Text(stringResource(R.string.product_name_hint_text)) })

                Text(text = "Select Product Quantity", modifier = Modifier.layoutId("productQuantityText"),
                    fontSize = 16.sp, color = Color.Blue)

                ExposedDropDownMenu(options = ProductUtils.fetchProductQty(), modifier = Modifier.layoutId("productQtyDropDown"),label ="Qty" ) {
                    println("Selected Items is $it")
                    selectedQty = it
                }

                ExposedDropDownMenu(options = ProductUtils.fetchProductQtyInUnits(), modifier = Modifier.layoutId("productQtyUnitDropDown"),label ="Units" ) {
                    println("Selected Items is $it")
                    selectedQtyUnits = it
                }

                OutlinedTextField(value = productCost, onValueChange = { productCost = it },
                    modifier = Modifier.layoutId("productCostTextField"),
                    label = { Text(stringResource(R.string.product_cost_hint_text)) })

                OutlinedTextField(value = productDiscountedCost, onValueChange = { productDiscountedCost = it },
                    modifier = Modifier.layoutId("productDiscountCostTextField"),
                    label = { Text(stringResource(R.string.discounted_cost_hint)) })


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

                                if(!TextUtils.isEmpty(productName) && !TextUtils.isEmpty(productCost)) {

                                    val product = Product(
                                        categoryName = category,
                                        storeName = storeName,
                                        userEmail = email,
                                        productName = productName,
                                        productQty = selectedQty.toInt(),
                                        productQtyUnits = selectedQtyUnits,
                                        productOriginalPrice = productCost,
                                        productDiscountedPrice = productDiscountedCost
                                    )
                                    productViewModel.createProduct(product)
                                }else{
                                    Toast.makeText(context,"Please fill required Fields",Toast.LENGTH_LONG).show()
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
            start.linkTo(parent.start)
            end.linkTo(parent.end)

        }
    }
}



