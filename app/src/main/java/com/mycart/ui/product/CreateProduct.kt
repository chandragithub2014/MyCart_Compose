package com.mycart.ui.product

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
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
import com.mycart.ui.common.AppScaffold
import com.mycart.ui.common.ExposedDropDownMenu
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
    AppScaffold(
        title = category,
        onLogoutClick = {
            // Handle logout action

        }

    ) {
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

                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Blue),
                    modifier = Modifier
                        .layoutId("createProductButton")
                        .fillMaxWidth()
                        .padding(horizontal = 55.dp, vertical = 0.dp)


                ) {
                    Text(stringResource(R.string.create_title), color = Color.White)
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


       /* constrain(startGuideline) {
            linkTo(start = parent.start)
        }*/
    }
}

