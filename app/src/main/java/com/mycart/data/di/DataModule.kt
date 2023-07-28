package com.mycart.data.di

import com.mycart.data.mock.MockAPI
import com.mycart.data.repository.MyCartRepositoryImpl
import com.mycart.domain.repository.MyCartRepository
import com.mycart.ui.category.viewmodel.CategoryViewModel
import com.mycart.ui.register.viewmodel.RegistrationViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import kotlin.math.sin

val appModule = module {
    // Define dependencies for the app layer here
    single{MockAPI()}
    single<MyCartRepository>{ MyCartRepositoryImpl(get()) }
    viewModel { CategoryViewModel(get()) }
    viewModel { RegistrationViewModel()}
}