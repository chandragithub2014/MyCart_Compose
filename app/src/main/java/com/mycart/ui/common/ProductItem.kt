package com.mycart.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import com.mycart.domain.model.Category
import com.mycart.domain.model.Product
import com.mycart.ui.utils.FetchImageFromURLWithPlaceHolder

@Composable
fun ProductListItem(category: Category, product: Product, isAdmin: Boolean, onPlusClick:(Product, Boolean) -> Unit, onMinusClick:(Product, Boolean) -> Unit, onAddClick: (Product) -> Unit, onEdit:(Product) -> Unit, onAddToCart:(Boolean) -> Unit, onDelete: (Product) -> Unit) {
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
                    MinusNumberPlusLayout(
                        Modifier.layoutId("minusPlusLayout"),
                        onIncrement = {
                            onPlusClick(product,it)
                        } ,

                        onDecrement = {
                            onMinusClick(product,it)
                        }
                    ) { showAdd ->
                        if (showAdd) {
                            showNumberPlusMinusLayout = false
                            onAddToCart(false)
                        }
                    }
                } else {
                    Button(
                        onClick = {
                            onAddClick(product)
                            onAddToCart(true)
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

private fun getStrikethroughAnnotatedString(input: String): AnnotatedString {
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