package com.mycart.domain.repository.firebase

import androidx.room.PrimaryKey
import com.mycart.domain.model.Category
import com.mycart.domain.model.Product
import com.mycart.domain.model.Store
import com.mycart.domain.model.User
import com.mycart.ui.common.Response

typealias AddUserResponse = Response<Boolean>
typealias AddStoreResponse = Response<Boolean>
typealias AddCategoryResponse = Response<Boolean>
typealias DeleteCategoryResponse = Response<Boolean>
typealias CategoryAvailableResponse = Response<Boolean>
typealias EditCategoryResponse = Response<Boolean>

typealias AddProductResponse = Response<Boolean>
typealias ProductAvailableResponse = Response<Boolean>
typealias DeleteProductResponse = Response<Boolean>
typealias EditProductResponse = Response<Boolean>
typealias EditProductQuantityResponse = Response<Boolean>

interface MyCartFireStoreRepository {

    suspend fun addUserToFireStore(user: User): AddUserResponse
    suspend fun createStore(store:Store): AddStoreResponse
    suspend fun fetchAllStores():List<Store>
    suspend fun checkForAdmin(email:String):User?
    suspend fun fetchStoreByEmail(email:String):Store?
    suspend fun createCategory(category: Category): AddCategoryResponse
    suspend fun fetchCategoryBasedOnStore(store:String):List<Category>
    suspend fun fetchDealsBasedOnStore(store:String):List<Category>
    suspend fun fetchSeasonalDealsBasedOnStore(store:String):List<Category>

    suspend fun deleteCategoryFromFireStore(categoryName: String,store: String):DeleteCategoryResponse
    suspend fun isCategoryAvailable(categoryName: String,store:String) : CategoryAvailableResponse
    suspend fun fetchCategoryInfo(categoryName: String,store: String):Category?
    suspend fun editCategoryInfo(categoryId:String,isDeal:Boolean,isSeasonal:Boolean,dealInfo:String):EditCategoryResponse


    suspend fun createProduct(product: Product) : AddProductResponse
    suspend fun isProductAvailable(productName:String,categoryName: String,store:String) : ProductAvailableResponse
    suspend fun fetchProductsByCategoryAndStore(categoryName: String,store: String) : List<Product>
    suspend fun deleteProduct(categoryName: String,store: String,productName:String):DeleteProductResponse
    suspend fun fetchProductInfo(categoryName: String,store: String,productName:String):Product?
    suspend fun editProductInfo(product:Product):EditProductResponse
    suspend fun fetchProductQuantity(categoryName: String,store: String,productName:String):Int
    suspend fun fetchUserSelectedProductQuantity(categoryName: String,store: String,productName:String):Int
    suspend fun updateProductQuantity(productID:String,productQty:Int,userSelectedQty:Int):EditProductQuantityResponse
}
