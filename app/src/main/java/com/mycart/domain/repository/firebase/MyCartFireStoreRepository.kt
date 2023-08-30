package com.mycart.domain.repository.firebase

import androidx.room.PrimaryKey
import com.mycart.domain.model.*
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

typealias ProductAvailableInCartResponse = Response<Boolean>
typealias AddProductCartResponse = Response<Boolean>
typealias EditProductQuantityInCartResponse = Response<Boolean>
typealias DeleteCartProductResponse = Response<Boolean>

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
    suspend fun updateProductQuantity(productID:String,productQty:Int,userSelectedQty:Int):EditProductQuantityResponse

    suspend fun fetchUserSelectedProductQuantity(loggedInUserEmail:String,categoryName: String,store: String,productName:String):Int
    suspend fun isProductAvailableInCart(productName:String,categoryName: String,store:String,userEmail:String) : ProductAvailableInCartResponse
    suspend fun addProductToCart(cartProduct: Cart) : AddProductCartResponse
    suspend fun updateUserSelectedQuantity(cartId:String,productQty:Int,userSelectedQty:Int,userEmail:String):EditProductQuantityInCartResponse
    suspend fun fetchCartInfo(productName:String,categoryName: String,store:String,userEmail:String):Cart?
    suspend fun deleteProductFromCart(product: Product,loggedInUserEmail:String):DeleteCartProductResponse
}
