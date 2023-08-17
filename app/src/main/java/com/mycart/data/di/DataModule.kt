package com.mycart.data.di

import androidx.room.Room
import com.google.firebase.firestore.FirebaseFirestore
import com.mycart.data.database.MyCartDataBase
import com.mycart.data.mock.MockAPI
import com.mycart.data.repository.MyCartRepositoryImpl
import com.mycart.data.repository.firebase.MyCartAuthenticationRepositoryImpl
import com.mycart.data.repository.firebase.MyCartFireStoreRepositoryImpl
import com.mycart.domain.repository.MyCartRepository
import com.mycart.domain.repository.firebase.MyCartAuthenticationRepository
import com.mycart.domain.repository.firebase.MyCartFireStoreRepository
import com.mycart.ui.category.viewmodel.CategoryViewModel
import com.mycart.ui.launcher.viewmodel.AppLauncherViewModel
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
    single { FirebaseFirestore.getInstance() }
    single<MyCartRepository> { MyCartRepositoryImpl(get(), get()) }
    single<MyCartAuthenticationRepository> { MyCartAuthenticationRepositoryImpl() }
    single<MyCartFireStoreRepository>{ MyCartFireStoreRepositoryImpl(get()) }
    viewModel { CategoryViewModel(get(),get()) }
    viewModel { RegistrationViewModel(get(),get()) }
    viewModel{LoginViewModel(get())}
    viewModel{StoreViewModel(get(),get())}
    viewModel{AppLauncherViewModel(get())}
}