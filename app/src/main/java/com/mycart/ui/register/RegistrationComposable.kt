package com.mycart.ui.register

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.mycart.R
import com.mycart.ui.common.InputTextField
import com.mycart.ui.common.ProgressBar
import com.mycart.ui.common.Response
import com.mycart.ui.login.ImageItem
import com.mycart.ui.register.viewmodel.RegistrationEvent
import com.mycart.ui.register.viewmodel.RegistrationViewModel
import org.koin.androidx.compose.get

@Composable
fun Register(navController: NavHostController, registrationViewModel: RegistrationViewModel = get()) {

// Observe the validationResult from the ViewModel

    var isAdmin by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    val localFocus = LocalFocusManager.current
    var showProgress by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(key1 = context) {
        registrationViewModel.receivedResponse.collect { event ->
            when (event) {
                is Response.Loading -> {
                  showProgress = true
                }
                is Response.Success -> {
                    showProgress = false
                    val successResult = event.data as? Boolean
                    if(successResult == true) {
                        Toast.makeText(context, "User Registration Successful", Toast.LENGTH_LONG).show()
                        navigateToLogin(navController)
                    }
                }
                is Response.Error -> {
                    showProgress = false
                    val errorMessage = event.errorMessage
                    Toast.makeText(context,errorMessage,Toast.LENGTH_LONG).show()
                }
                is Response.SuccessConfirmation ->{
                    showProgress = false
                    val successMessage = event.successMessage
                    Toast.makeText(context, successMessage, Toast.LENGTH_LONG).show()
                }
                else -> {
                    showProgress = false
                }
            }
        }
    }

     BackHandler(enabled = true) {
         navigateToLogin(navController)
     }
    if(showProgress){

        ProgressBar()
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {

        Column(

            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            ImageItem(R.drawable.ic_baseline_shopping_cart_24)



            InputTextField(
                onValueChanged = { registrationViewModel.onAction(RegistrationEvent.EmailChanged(it)) },
                label = stringResource(R.string.username_label),
                isError = registrationViewModel.errorState.value.emailStatus,
                error = "Please Enter Valid Email"
            )

            InputTextField(
                onValueChanged = {
                    registrationViewModel.onAction(
                        RegistrationEvent.PasswordChanged(
                            it
                        )
                    )
                },
                label = stringResource(R.string.password_label),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                isError = registrationViewModel.errorState.value.passwordStatus,
                error = "Please Enter Valid Password (>=6)"
            )
            InputTextField(
                onValueChanged = {
                    registrationViewModel.onAction(
                        RegistrationEvent.ReEnterPasswordChanged(
                            it
                        )
                    )
                },
                label = stringResource(R.string.password_reenter_label),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                isError = registrationViewModel.errorState.value.confirmationPasswordStatus,
                error = "Password and ConfirmPassword are not same)"
            )



            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(end = 50.dp)
                    .align(Alignment.End)
            ) {
                Text(text = "Are you Admin ?")
                Switch(
                    checked = isAdmin, onCheckedChange = {
                        isAdmin = it
                        registrationViewModel.onAction(RegistrationEvent.AdminChanged(it))
                    },
                    colors = SwitchDefaults.colors(checkedThumbColor = Color.Blue)
                )


            }
            if (isAdmin) {
                InputTextField(
                    onValueChanged = {
                        registrationViewModel.onAction(
                            RegistrationEvent.StoreNameChanged(
                                it
                            )
                        )
                    },
                    label = stringResource(R.string.store_name_label),
                    isError = registrationViewModel.errorState.value.storeNameStatus,
                    error = "Store Name not valid"
                )
                InputTextField(
                    onValueChanged = {
                        registrationViewModel.onAction(
                            RegistrationEvent.StoreLocationChanged(
                                it
                            )
                        )
                    },
                    label = stringResource(R.string.store_location_label),
                    isError = registrationViewModel.errorState.value.storeLocStatus,
                    error = "Store Location not valid"
                )
                InputTextField(
                    onValueChanged = {
                        registrationViewModel.onAction(
                            RegistrationEvent.MobileNumberChanged(
                                it
                            )
                        )
                    },
                    label = stringResource(R.string.user_mobile),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = registrationViewModel.errorState.value.mobileStatus,
                    error = "Mobile Number  must be 10 digit"
                )
                InputTextField(
                    onValueChanged = {
                        registrationViewModel.onAction(
                            RegistrationEvent.PinCodeChanged(
                                it
                            )
                        )
                    },
                    label = stringResource(R.string.store_pincode_label),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = registrationViewModel.errorState.value.pinCodeStatus,
                    error = "PinCode must be 6 digit"
                )
            }
            OutlinedButton(
                onClick = {
                    registrationViewModel.onAction(RegistrationEvent.Submit)
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Blue),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 50.dp, top = 10.dp, end = 50.dp),
            ) {
                Text(stringResource(R.string.registration_label), color = Color.White)
            }

        }
    }
}

private fun navigateToLogin(navController: NavHostController){
    navController.popBackStack()
    navController.navigate("loginScreen")
}

@Composable
@Preview
fun RegisterPreview() {

   // Register()
}


//Validations
//https://betterprogramming.pub/how-to-validate-fields-using-jetpack-compose-in-android-5ea1522331c7