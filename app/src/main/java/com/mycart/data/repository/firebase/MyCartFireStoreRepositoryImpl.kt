package com.mycart.data.repository.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.mycart.domain.model.Category
import com.mycart.domain.model.Product
import com.mycart.domain.model.Store
import com.mycart.domain.model.User
import com.mycart.domain.repository.firebase.*
import com.mycart.ui.common.Response
import kotlinx.coroutines.tasks.await
import okhttp3.internal.wait

class MyCartFireStoreRepositoryImpl(private val fireStore: FirebaseFirestore) :
    MyCartFireStoreRepository {

    override suspend fun addUserToFireStore(user: User) = try {
        val querySnapshot = fireStore.collection("users")
            .whereEqualTo("userEmail", user.userEmail)
            .get()
            .await()
        if (querySnapshot.isEmpty) {
            fireStore.collection("users")
                .document(user.userEmail)
                .set(user)
                .await()
            Response.Success(true)
        } else {
            Response.Error("User Already Exists....")
        }

    } catch (e: Exception) {
        Response.Error(e.message.toString())
    }


    override suspend fun createStore(store: Store) = try {
        fireStore.collection("stores")
            .document(store.storeName)
            .set(store)
            .await()
        Response.Success(true)
    } catch (e: Exception) {
        Response.Error(e.message.toString())
    }

    override suspend fun fetchAllStores(): List<Store> {
        val stores: List<Store>
        val querySnapshot = fireStore.collection("stores").get().await()
        stores = querySnapshot.documents.mapNotNull { document ->
            document.toObject(Store::class.java)
        }
        return stores
    }

    override suspend fun checkForAdmin(email: String): User? {
        val querySnapshot = fireStore.collection("users")
            .whereEqualTo("userEmail", email)
            .get()
            .await()
        val users: List<User> = querySnapshot.documents.mapNotNull { documentSnapshot ->
            documentSnapshot.toObject(User::class.java)
        }
        return users.firstOrNull()
    }

    override suspend fun fetchStoreByEmail(email: String): Store? {
        val querySnapshot = fireStore.collection("stores")
            .whereEqualTo("ownerEmail", email)
            .get()
            .await()

        val stores: List<Store> = querySnapshot.documents.mapNotNull { documentSnapshot ->
            documentSnapshot.toObject(Store::class.java)
        }
        return stores.firstOrNull()

    }

    override suspend fun createCategory(category: Category) = try {
        fireStore.collection("categories")
            .document(category.categoryId)
            .set(category)
            .await()
        Response.Success(true)
    } catch (e: Exception) {
        Response.Error(e.message.toString())
    }

    override suspend fun fetchCategoryBasedOnStore(store: String): List<Category> {
        val categoryList: MutableList<Category>
        val querySnapshot = fireStore.collection("categories")
            .whereEqualTo("storeName", store)
            .get()
            .await()

        val category: List<Category> = querySnapshot.documents.mapNotNull { documentSnapshot ->
            documentSnapshot.toObject(Category::class.java)
        }

        categoryList = category.toMutableList()

        return categoryList
    }

    override suspend fun fetchDealsBasedOnStore(store: String): List<Category> {
        val categoryList: MutableList<Category>
        val querySnapshot = fireStore.collection("categories")
            .whereEqualTo("storeName", store)
            .whereEqualTo("deal", true)
            .get()
            .await()

        val category: List<Category> = querySnapshot.documents.mapNotNull { documentSnapshot ->
            documentSnapshot.toObject(Category::class.java)
        }

        categoryList = category.toMutableList()

        return categoryList
    }

    override suspend fun fetchSeasonalDealsBasedOnStore(store: String): List<Category> {
        val categoryList: MutableList<Category>
        val querySnapshot = fireStore.collection("categories")
            .whereEqualTo("storeName", store)
            .whereEqualTo("seasonal", true)
            .get()
            .await()

        val category: List<Category> = querySnapshot.documents.mapNotNull { documentSnapshot ->
            documentSnapshot.toObject(Category::class.java)
        }

        categoryList = category.toMutableList()

        return categoryList
    }


    override suspend fun deleteCategoryFromFireStore(
        categoryName: String,
        store: String
    ): DeleteCategoryResponse = try {
        val querySnapshot = fireStore.collection("categories")
            .whereEqualTo("storeName", store)
            .whereEqualTo("categoryName", categoryName)
            .get()
            .await()
        for (documentSnapshot in querySnapshot.documents) {
            val categoryDocumentRef = documentSnapshot.reference
            categoryDocumentRef.delete().await()
        }
        Response.Success(true)
    } catch (e: Exception) {
        Response.Error(e.message.toString())
    }

    override suspend fun isCategoryAvailable(
        categoryName: String,
        store: String
    ) = try {

        val querySnapshot = fireStore.collection("categories")
            .whereEqualTo("storeName", store)
            .get()
            .await()

        val categoryExists = querySnapshot.documents.any { documentSnapshot ->
            val category = documentSnapshot.toObject(Category::class.java)
            category?.categoryName == categoryName

        }

        if (categoryExists) {
            Response.Success(true)
        } else {
            Response.Success(false)
        }
    } catch (e: Exception) {
        Response.Error(e.message.toString())
    }

    override suspend fun fetchCategoryInfo(categoryName: String, store: String): Category? {
        val querySnapshot = fireStore.collection("categories")
            .whereEqualTo("storeName", store)
            .whereEqualTo("categoryName", categoryName)
            .get()
            .await()

        val categories: List<Category> = querySnapshot.documents.mapNotNull { documentSnapshot ->
            documentSnapshot.toObject(Category::class.java)
        }
        return categories.firstOrNull()
    }

    override suspend fun editCategoryInfo(
        categoryId:String,
        isDeal: Boolean,
        isSeasonal: Boolean,
        dealInfo: String
    ) = try{

     /*   fireStore.collection("categories").document(categoryId).update("deal", isDeal,
            "dealInfo", dealInfo,
            "seasonal", isSeasonal).wait()*/
        val categoryDocRef = fireStore.collection("categories").document(categoryId)
        categoryDocRef.update(
            mapOf(
                "deal" to isDeal,
                "dealInfo" to dealInfo,
                "seasonal" to isSeasonal
            )
        ).await()

        Response.Success(true)
    }catch (e: Exception) {
        Response.Error(e.message.toString())
    }

    private suspend fun getCategoryDataForCategories(
        storeName: String,
        categoryNames: List<String>
    ): Response<List<Category>> = try {
        val categoryDataList = mutableListOf<Category>()

        for (categoryName in categoryNames) {
            val documentSnapshot = fireStore.collection("categories")
                .document(storeName)
                .collection(storeName)
                .document(categoryName)
                .get()
                .await()

            val categoryData = documentSnapshot.toObject(Category::class.java)
            categoryData?.let { categoryDataList.add(it) }
        }
        println("CategoryList is $categoryDataList")

        Response.Success(categoryDataList)
    } catch (e: Exception) {
        Response.Error(e.message.toString())
    }

    override suspend fun createProduct(product: Product) = try {
        fireStore.collection("products")
            .document(product.productId)
            .set(product)
            .await()
        Response.Success(true)
    } catch (e: Exception) {
        Response.Error(e.message.toString())
    }

    override suspend fun isProductAvailable(
        productName: String,
        categoryName: String,
        store: String
    )= try {

        val querySnapshot = fireStore.collection("products")
            .whereEqualTo("storeName", store)
            .whereEqualTo("categoryName",categoryName)
            .get()
            .await()

        val productExists = querySnapshot.documents.any { documentSnapshot ->
            val product = documentSnapshot.toObject(Product::class.java)
            product?.productName == productName

        }

        if (productExists) {
            Response.Success(true)
        } else {
            Response.Success(false)
        }
    } catch (e: Exception) {
        Response.Error(e.message.toString())
    }

}