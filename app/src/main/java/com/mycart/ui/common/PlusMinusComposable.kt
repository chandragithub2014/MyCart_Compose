package com.mycart.ui.common

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.mycart.ui.utils.DisplayBorderedLabel
import com.mycart.ui.utils.FetchImageWithBorderFromDrawable

@Composable
fun MinusNumberPlusLayout(modifier: Modifier = Modifier, initialQuantity:Int = 1,onIncrement:(Boolean) -> Unit, onDecrement:(Boolean)->Unit, showAdd: (Boolean) -> Unit) {
    println("Initial Quantity is $initialQuantity")
    var quantity by remember { mutableStateOf(initialQuantity) }

    LaunchedEffect(initialQuantity) {
        quantity = initialQuantity
    }
    println("Quantity is .................$quantity")
    ConstraintLayout(
        modifier = modifier,
    ) {
        val (minusButton, numberText, plusButton) = createRefs()
//Minus ImageView
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
        println(" Quantity is $quantity")
        DisplayBorderedLabel(label = quantity.toString(), modifier = Modifier
            .constrainAs(numberText) {
                start.linkTo(minusButton.end)
                top.linkTo(parent.top)
            }
            .size(25.dp)
            .border(1.dp, Color.Blue)
            .wrapContentSize(Alignment.Center))

//Plus Image View
        FetchImageWithBorderFromDrawable(
            imageName = "ic_baseline_add_24",
            modifier = Modifier.constrainAs(plusButton) {
                start.linkTo(numberText.end)
                top.linkTo(parent.top)

            }) {
            println("Clicked Plus")
            quantity += 1
            if(quantity <= 3){
                onIncrement(true)
            }else{
                quantity = 3
            }
           /* if (quantity > 3) {
                quantity = 3
            }else{
                onIncrement(true)
            }*/
        }
    }
}
