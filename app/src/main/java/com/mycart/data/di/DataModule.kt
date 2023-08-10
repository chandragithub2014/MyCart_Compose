package com.mycart.data.di

import androidx.room.Room
import com.mycart.data.database.MyCartDataBase
import com.mycart.data.mock.MockAPI
import com.mycart.data.repository.MyCartRepositoryImpl
import com.mycart.domain.repository.MyCartRepository
import com.mycart.ui.category.viewmodel.CategoryViewModel
import com.mycart.ui.login.viewmodel.LoginViewModel
import com.mycart.ui.register.viewmodel.RegistrationViewModel
import com.mycart.ui.store.viewmodel.StoreViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import kotlin.math.sin

val appModule = module {
    // Define dependencies for the app layer here
    single {
        Room.databaseBuilder(androidContext(), MyCartDataBase::class.java, "mycart-db")
            .fallbackToDestructiveMigration()
            .build()
    }
    single { get<MyCartDataBase>().myCartDao() }
    single { MockAPI() }
    single<MyCartRepository> { MyCartRepositoryImpl(get(), get()) }
    viewModel { CategoryViewModel(get()) }
    viewModel { RegistrationViewModel(get()) }
    viewModel{LoginViewModel(get())}
    viewModel{StoreViewModel(get())}
}