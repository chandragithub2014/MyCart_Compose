package com.mycart.ui.cart

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import com.mycart.domain.model.Cart
import com.mycart.domain.model.Product
import com.mycart.ui.common.MinusNumberPlusLayout
import com.mycart.ui.utils.FetchImageFromURLWithPlaceHolder
import com.mycart.ui.utils.getStrikethroughAnnotatedString

@Composable
fun CartListItem(loggedInUserEmail:String, store:String, cart: Cart, onPlusClick:(Product, Boolean) -> Unit, onMinusClick:(Product, Boolean) -> Unit) {
    println("Received Cart is $cart")
    var showNumberPlusMinusLayout by remember { mutableStateOf(false) }
    val constraintSet = productListItemConstraints()
    if(cart.product.userSelectedProductQty > 0) {
        showNumberPlusMinusLayout = true
    }
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
                FetchImageFromURLWithPlaceHolder(imageUrl = cart.product.productImage)
            }

            Text(
                text = cart.product.productName, modifier = Modifier.layoutId("productName"),
                fontSize = 16.sp, color = Color.Blue, fontWeight = FontWeight.Bold
            )
            Text(
                text = "${cart.product.productPerUnit}${cart.product.productQtyUnits}", modifier = Modifier.layoutId("productUnit"),
                fontSize = 16.sp, color = Color.Gray
            )
            Text(
                text = cart.product.productDiscountedPrice,
                modifier = Modifier.layoutId("productDiscountedCost"),
                fontSize = 16.sp,
                color = Color.Blue,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = getStrikethroughAnnotatedString(cart.product.productOriginalPrice),
                modifier = Modifier.layoutId("productCost"),
                fontSize = 16.sp
            )
            if (showNumberPlusMinusLayout) {
                MinusNumberPlusLayout(
                    Modifier.layoutId("minusPlusLayout"),
                    initialQuantity = cart.product.userSelectedProductQty,
                    onIncrement = {
                        onPlusClick(cart.product,it)
                    } ,

                    onDecrement = {
                        onMinusClick(cart.product,it)
                    }
                ) { showAdd ->
                    if (showAdd) {
                        showNumberPlusMinusLayout = false
                      //Delete that Product from Cart
                        // This is Irrelevant in Cart , but is relevant in Product screen

                    }
                }
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
        val minusPlusLayoutRef = createRefFor("minusPlusLayout")


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


        constrain(minusPlusLayoutRef) {
            top.linkTo(productCostRef.bottom, 10.dp)
            end.linkTo(parent.end, 5.dp)
            width = Dimension.wrapContent
        }

    }


}

