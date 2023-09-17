package com.mycart.ui.orders

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavHostController
import com.mycart.domain.model.Order
import com.mycart.domain.model.Product
import com.mycart.domain.model.User
import com.mycart.ui.cart.viewModel.CartViewModel
import com.mycart.ui.common.*
import com.mycart.ui.orders.viewmodel.OrderViewModel
import com.mycart.ui.utils.FetchImageFromDrawable
import org.koin.androidx.compose.get

@Composable
fun OrderComposable(userEmail: String?,
                    storeName: String,
                    navController: NavHostController,
                    orderViewModel: OrderViewModel = get()
){

    var orderList by rememberSaveable { mutableStateOf(listOf<Order>()) }
    var showProgress by rememberSaveable { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    val currentState by orderViewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(key1 = Unit) {
        userEmail?.let { email ->
            orderViewModel.checkForAdmin(email)
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
                        orderViewModel.fetchOrderListByLoggedInUser(
                            user.userEmail
                        )
                        showProgress = false
                    }

                }
            }

            is Response.SuccessList -> {
                when ((currentState as Response.SuccessList).dataType) {
                    DataType.ORDER -> {
                        orderList =
                            (currentState as Response.SuccessList).dataList.filterIsInstance<Order>()
                        showProgress = false
                    }
                    else -> {

                    }
                }
            }
            is Response.SuccessConfirmation -> {
                showProgress = false
                //    userSelectedQuantityInCart = productViewModel.userSelectedQuantity.value

            }

            else -> {

            }
        }
    }
    AppScaffold(
        title = "Orders",
        onCartClick = {

        },
        onLogoutClick = {
            showDialog = true
        }
    ) {
        if (showProgress) {
            ProgressBar()
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),

                ) {
                items(items = orderList) { order ->
                    userEmail?.let { email ->
                        DisplayOrder(order = order, email, navController)
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
    }
}



@Composable
fun DisplayOrder(order: Order, email: String, navController: NavHostController) {
    val constraintSet = orderItemConstraints()
    BoxWithConstraints(
        modifier = Modifier
            .padding(top = 0.dp, bottom = 5.dp, start = 5.dp, end = 5.dp)
            .border(
                BorderStroke(1.dp, Color.LightGray),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(8.dp)
    ){
        ConstraintLayout(constraintSet, modifier = Modifier.fillMaxWidth()) {
            /*Box(
                Modifier
                    .layoutId("cartImage")
                    .width(100.dp)
                    .height(100.dp)
                    .border(
                        BorderStroke(1.dp, Color.LightGray)
                    ),
                contentAlignment = Alignment.Center
            ) {
                FetchImageFromDrawable(imageName = "ic_baseline_shopping_cart_24")
            }*/
            FetchImageFromDrawable(imageName = "ic_baseline_shopping_cart_24",modifier = Modifier.layoutId("cartImage"))
            Text(
                text = order.store, modifier = Modifier.layoutId("storeName"),
                fontSize = 16.sp, color = Color.Blue, fontWeight = FontWeight.Bold
            )
            Text(
                text = order.orderedDateTime, modifier = Modifier.layoutId("orderedDateTime"),
                fontSize = 16.sp, color = Color.Gray
            )
            Text(
                text = "${order.totalCost}(In Rs)", modifier = Modifier.layoutId("orderCost"),
                fontSize = 16.sp, color = Color.Gray
            )
            Text(
                text = order.orderStatus, modifier = Modifier.layoutId("orderStatus"),
                fontSize = 16.sp, color = Color.Green
            )
            Text(
                text = order.additionalMessage, modifier = Modifier.layoutId("orderAdditionalMessage"),
                fontSize = 16.sp, color = Color.Red
            )
            FetchImageFromDrawable(imageName = "ic_detail", modifier = Modifier.layoutId("orderDetail"))
            /*Box(
                Modifier
                    .layoutId("orderDetail")
                    .width(100.dp)
                    .height(100.dp)
                   ,
                contentAlignment = Alignment.Center
            ) {
                FetchImageFromDrawable(imageName = "ic_detail", modifier = Modifier.layoutId("orderDetail"))
            }*/
        }
    }

}


private fun orderItemConstraints(): ConstraintSet {
    return ConstraintSet {
        val cartImageRef = createRefFor("cartImage")
        val orderedDateTimeRef = createRefFor("orderedDateTime")
        val orderCostRef = createRefFor("orderCost")
        val storeNameRef = createRefFor("storeName")
        val orderStatusRef = createRefFor("orderStatus")
        val orderDetailRef = createRefFor("orderDetail")
        var orderAdditionalMessageRef = createRefFor("orderAdditionalMessage")


        constrain(cartImageRef) {
            top.linkTo(parent.top, 5.dp)
            start.linkTo(parent.start, 10.dp)
            width = Dimension.wrapContent

        }

        constrain(storeNameRef) {
            top.linkTo(parent.top, 5.dp)
            start.linkTo(cartImageRef.end, 5.dp)
            width = Dimension.fillToConstraints

        }

        constrain(orderedDateTimeRef) {
            top.linkTo(storeNameRef.bottom, 5.dp)
            start.linkTo(cartImageRef.end, 5.dp)
            width = Dimension.wrapContent

        }

        constrain(orderCostRef) {
            top.linkTo(orderedDateTimeRef.bottom, 5.dp)
            start.linkTo(cartImageRef.end, 5.dp)
            width = Dimension.wrapContent
        }

        constrain(orderStatusRef) {
            top.linkTo(orderedDateTimeRef.bottom, 5.dp)
            start.linkTo(orderCostRef.end, 5.dp)
            width = Dimension.wrapContent
        }

        constrain(orderAdditionalMessageRef){
            top.linkTo(orderStatusRef.bottom, 5.dp)
            start.linkTo(cartImageRef.end, 5.dp)
            width = Dimension.wrapContent
        }

        constrain(orderDetailRef){
            top.linkTo(parent.top)
            end.linkTo(parent.end)
            bottom.linkTo(parent.bottom)
            width = Dimension.wrapContent

        }


    }


}