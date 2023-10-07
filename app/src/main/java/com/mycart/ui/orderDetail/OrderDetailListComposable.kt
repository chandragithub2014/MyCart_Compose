package com.mycart.ui.orderDetail

import android.text.TextUtils
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavHostController
import com.mycart.R
import com.mycart.bottomnavigation.Screen
import com.mycart.domain.model.Order
import com.mycart.domain.model.OrderDetail
import com.mycart.domain.model.Product
import com.mycart.domain.model.User
import com.mycart.navigator.navigateToOrders
import com.mycart.ui.category.navigateToCategory
import com.mycart.ui.common.*
import com.mycart.ui.orderDetail.viewModel.OrderDetailViewModel
import com.mycart.ui.utils.DisplayLabel
import com.mycart.ui.utils.FetchImageFromURLWithPlaceHolder
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun OrderDetailComposable(
    userEmail: String?,
    orderId: String,
    storeName: String,
    navController: NavHostController,
    orderDetailViewModel: OrderDetailViewModel = get()
) {

    var orderDetailList by rememberSaveable { mutableStateOf(listOf<OrderDetail>()) }
    var orderInfo by remember { mutableStateOf(Order()) }
    var showProgress by rememberSaveable { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var additionalInfo by rememberSaveable { mutableStateOf("") }
    val currentState by orderDetailViewModel.state.collectAsState()
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val scrollState = rememberScrollState()
    val scaffoldState = rememberScaffoldState()

    LaunchedEffect(key1 = Unit) {
        userEmail?.let { email ->
            orderDetailViewModel.checkForAdmin(email)
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
            is Response.Success -> {
                when ((currentState as Response.Success).data) {
                    is User -> {
                        val user = (currentState as Response.Success).data as User
                        if (user.admin) {
                            orderDetailViewModel.fetchOrderListByOrderId(orderId)
                        } else {
                            orderDetailViewModel.fetchOrderListByOrderIdLoggedInUser(
                                user.userEmail, orderId
                            )
                        }
                        showProgress = false

                    }

                    is Order -> {
                        orderInfo = (currentState as Response.Success).data as Order
                        showProgress = false

                    }


                }
            }

            is Response.SuccessList -> {
                when ((currentState as Response.SuccessList).dataType) {
                    DataType.ORDER_DETAIL -> {
                        orderDetailList =
                            (currentState as Response.SuccessList).dataList.filterIsInstance<OrderDetail>()


                        if (orderDetailViewModel.isAdminState.value) {
                            orderDetailViewModel.fetchOrderInfoByOrderId(orderId)
                        }
                        showProgress = false
                    }
                    else -> {

                    }
                }
            }
            is Response.SuccessConfirmation -> {
                showProgress = false
                navController.popBackStack()
                navigateToOrders(navController,userEmail,storeName)
                //    userSelectedQuantityInCart = productViewModel.userSelectedQuantity.value

            }

            else -> {

            }
        }
    }

    AppScaffold(
        title = "Order Details",
      //  scaffoldState = scaffoldState,
        userEmail = userEmail ?: "",
        store = storeName,
        navController = navController,
        selectedScreen = Screen.OrderDetails,
        onCartClick = {

        },
        onLogoutClick = {
            showDialog = true
        }
    ) {
        if (showProgress) {
            ProgressBar()
        }
        Box(modifier = Modifier.padding(PaddingValues(bottom = 60.dp))) {


            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                if (orderDetailViewModel.isAdminState.value) {
                    DisplayLabel(
                        "Order Id :",
                        textColor = Color.Blue,
                        textFont = FontWeight.Bold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp)
                    )
                    DisplayLabel(
                        orderId, textColor = Color.Blue, modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp)
                    )
                    DisplayLabel(
                        "Total Cost(In Rupees) :${orderInfo.totalCost}",
                        textColor = Color.Blue,
                        textFont = FontWeight.Bold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp)
                    )

                    InputTextField(
                        onValueChanged = {
                            additionalInfo = it
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .padding(start = 10.dp, end = 10.dp),
                        label = stringResource(R.string.additonal_info_hint),
                        singleLine = false,
                        maxLines = 5,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                keyboardController?.hide()
                            }
                        ),
                        textValue = additionalInfo
                    )
                    DisplayLabel(
                        "Order Details :",
                        textColor = Color.Blue,
                        textFont = FontWeight.Bold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp)
                    )
                }
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)


                ) {
                    items(items = orderDetailList) { order ->
                        userEmail?.let { email ->
                            DisplayOrderDetail(orderDetail = order, email, navController)
                        }
                    }
                    if (orderDetailViewModel.isAdminState.value) {
                        item {
                            Row(
                                horizontalArrangement = Arrangement.Start,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .padding(start = 50.dp, end = 50.dp)
                            ) {
                                Button(
                                    onClick = {
                                      if(!TextUtils.isEmpty(additionalInfo)){
                                          orderDetailViewModel.updateOrder(orderId,"Confirmed",additionalInfo)
                                      }else{
                                          Toast.makeText(context,"Please Enter Additional Info",Toast.LENGTH_LONG).show()
                                      }
                                    },
                                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Blue),
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(2.dp)
                                ) {
                                    Text(text = "Confirm", color = Color.White)
                                }

                                OutlinedButton(
                                    onClick = {
                                        if(!TextUtils.isEmpty(additionalInfo)){
                                            orderDetailViewModel.updateOrder(orderId,"Rejected",additionalInfo)
                                        }else{
                                            Toast.makeText(context,"Please Enter Additional Info",Toast.LENGTH_LONG).show()
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Blue),
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(2.dp)
                                ) {
                                    Text(text = "Reject", color = Color.White)
                                }
                            }

                        }
                    }


                }

            }
        }


        if (showDialog) {
            DisplaySimpleAlertDialog(
                showDialog = showDialog,
                title = "My Cart",
                description = "Do you want to Logout ?",
                positiveButtonTitle = "OK",
                negativeButtonTitle = "Cancel",
                onPositiveButtonClick = {
                    //  storeViewModel.signOut()

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
    val coroutineScope = rememberCoroutineScope()
    if (scrollState.value > 200) {
        scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()
        // Launch a coroutine to close the drawer
        coroutineScope.launch {
            scaffoldState.drawerState.close()
        }
    }
}

@Composable
fun DisplayOrderDetail(orderDetail: OrderDetail, email: String, navController: NavHostController) {
    val constraintSet = orderDetailItemConstraints()
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
            FetchImageFromURLWithPlaceHolder(
                imageUrl = orderDetail.product.productImage,
                modifier = Modifier.layoutId("productImage")
            )

            Text(
                text = orderDetail.product.productName, modifier = Modifier.layoutId("productName"),
                fontSize = 16.sp, color = Color.Blue, fontWeight = FontWeight.Bold
            )
            Text(
                text = "${orderDetail.product.productDiscountedPrice}(In Rs)",
                modifier = Modifier.layoutId("productCost"),
                fontSize = 16.sp,
                color = Color.Gray
            )
            Text(
                text = "Quantity:${orderDetail.product.userSelectedProductQty} in ${orderDetail.product.productQtyUnits}",
                modifier = Modifier.layoutId("productQty"),
                fontSize = 16.sp,
                color = Color.Black
            )
            Text(
                text = orderDetail.status, modifier = Modifier.layoutId("orderDetailStatus"),
                fontSize = 16.sp, color = Color.Magenta
            )
            Text(
                text = orderDetail.additionalMessage,
                modifier = Modifier.layoutId("orderAdditionalMessage"),
                fontSize = 16.sp,
                color = Color.Red
            )
        }
    }

}

private fun orderDetailItemConstraints(): ConstraintSet {
    return ConstraintSet {
        val productImageRef = createRefFor("productImage")
        val productNameRef = createRefFor("productName")
        val productCostRef = createRefFor("productCost")
        val productQuantityRef = createRefFor("productQty")
        val orderDetailStatusRef = createRefFor("orderDetailStatus")
        val orderAdditionalMessageRef = createRefFor("orderAdditionalMessage")


        constrain(productImageRef) {
            top.linkTo(parent.top)
            start.linkTo(parent.start, 10.dp)
            bottom.linkTo(parent.bottom)
            width = Dimension.wrapContent

        }

        constrain(productNameRef) {
            top.linkTo(parent.top, 5.dp)
            start.linkTo(productImageRef.end, 5.dp)
            width = Dimension.fillToConstraints

        }

        constrain(productCostRef) {
            top.linkTo(productNameRef.bottom, 5.dp)
            start.linkTo(productImageRef.end, 5.dp)
            width = Dimension.wrapContent
        }

        constrain(productQuantityRef) {
            top.linkTo(productCostRef.bottom, 5.dp)
            start.linkTo(productImageRef.end, 10.dp)
            width = Dimension.wrapContent
        }

        constrain(orderDetailStatusRef) {
            top.linkTo(productQuantityRef.bottom, 5.dp)
            start.linkTo(productImageRef.end, 10.dp)
            width = Dimension.wrapContent
        }

        constrain(orderAdditionalMessageRef) {
            top.linkTo(orderDetailStatusRef.bottom, 5.dp)
            start.linkTo(productImageRef.end, 5.dp)
            width = Dimension.wrapContent
        }


    }


}
