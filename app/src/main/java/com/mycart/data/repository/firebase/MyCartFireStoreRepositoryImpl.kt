package com.mycart.data.repository.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.mycart.domain.model.*
import com.mycart.domain.repository.firebase.*
import com.mycart.ui.common.Response
import kotlinx.coroutines.tasks.await


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
        categoryId: String,
        isDeal: Boolean,
        isSeasonal: Boolean,
        dealInfo: String
    ) = try {

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
    } catch (e: Exception) {
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
    ) = try {

        val querySnapshot = fireStore.collection("products")
            .whereEqualTo("storeName", store)
            .whereEqualTo("categoryName", categoryName)
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

    override suspend fun fetchProductsByCategoryAndStore(
        categoryName: String,
        store: String
    ): List<Product> {
        val productList: MutableList<Product>
        val querySnapshot = fireStore.collection("products")
            .whereEqualTo("storeName", store)
            .whereEqualTo("categoryName", categoryName)
            .get()
            .await()

        val category: List<Product> = querySnapshot.documents.mapNotNull { documentSnapshot ->
            documentSnapshot.toObject(Product::class.java)
        }

        productList = category.toMutableList()

        return productList
    }

    override suspend fun deleteProduct(
        categoryName: String,
        store: String,
        productName: String
    ): DeleteCategoryResponse = try {
        val querySnapshot = fireStore.collection("products")
            .whereEqualTo("storeName", store)
            .whereEqualTo("categoryName", categoryName)
            .whereEqualTo("productName", productName)
            .get()
            .await()
        for (documentSnapshot in querySnapshot.documents) {
            val productDocumentRef = documentSnapshot.reference
            productDocumentRef.delete().await()
        }
        Response.Success(true)
    } catch (e: Exception) {
        Response.Error(e.message.toString())
    }

    override suspend fun fetchProductInfo(
        categoryName: String,
        store: String,
        productName: String
    ): Product? {
        val querySnapshot = fireStore.collection("products")
            .whereEqualTo("storeName", store)
            .whereEqualTo("categoryName", categoryName)
            .whereEqualTo("productName", productName)

            .get()
            .await()

        val products: List<Product> = querySnapshot.documents.mapNotNull { documentSnapshot ->
            documentSnapshot.toObject(Product::class.java)
        }
        return products.firstOrNull()
    }

    override suspend fun editProductInfo(product: Product): EditProductResponse = try {

        val productDocRef = fireStore.collection("products").document(product.productId)
        productDocRef.update(
            mapOf(
                "productName" to product.productName,
                "productQty" to product.productQty,
                "productQtyUnits" to product.productQtyUnits,
                "productDiscountedPrice" to product.productDiscountedPrice,
                "productOriginalPrice" to product.productOriginalPrice,
                "keywords" to product.keywords
            )
        ).await()
        Response.Success(true)
    } catch (e: Exception) {
        Response.Error(e.message.toString())
    }

    override suspend fun fetchProductQuantity(
        categoryName: String,
        store: String,
        productName: String
    ): Int {
        var quantity = -1
        try {
            val querySnapshot = fireStore.collection("products")
                .whereEqualTo("storeName", store)
                .whereEqualTo("categoryName", categoryName)
                .whereEqualTo("productName", productName)

                .get()
                .await()

            val products: List<Product> = querySnapshot.documents.mapNotNull { documentSnapshot ->
                documentSnapshot.toObject(Product::class.java)
            }
            products.firstOrNull()?.productQty?.let { productQuantity ->
                quantity = productQuantity
            }


        } catch (e: Exception) {
            return quantity
        }

        return quantity
    }

    override suspend fun fetchUserSelectedProductQuantity(
        loggedInUserEmail: String,
        categoryName: String,
        store: String,
        productName: String
    ): Int {
        var quantity = -1
        try {
            val querySnapshot = fireStore.collection("cart")
                .whereEqualTo("loggedInUserEmail", loggedInUserEmail)
                .whereEqualTo("product.storeName", store)
                .whereEqualTo("product..categoryName", categoryName)
                .whereEqualTo("product.productName", productName)
                .get()
                .await()

            val products: List<Product> = querySnapshot.documents.mapNotNull { documentSnapshot ->
                documentSnapshot.toObject(Product::class.java)
            }
            products.firstOrNull()?.userSelectedProductQty?.let { productQuantity ->
                quantity = productQuantity
            }


        } catch (e: Exception) {
            return quantity
        }

        return quantity
    }

    override suspend fun updateProductQuantity(
        productID: String,
        productQty: Int,
        userSelectedQty: Int

    ) = try {
        val productDocRef = fireStore.collection("products").document(productID)
        productDocRef.update(
            mapOf(
                "productQty" to productQty,
                "userSelectedProductQty" to 0
            )
        ).await()
        Response.Success(true)
    } catch (e: Exception) {
        Response.Error(e.message.toString())
    }

    override suspend fun isProductAvailableInCart(
        productName: String,
        categoryName: String,
        store: String,
        userEmail: String
    ): ProductAvailableInCartResponse = try {
        val querySnapshot = fireStore.collection("cart")
            .whereEqualTo("loggedInUserEmail", userEmail)
            .whereEqualTo("product.storeName", store)
            .whereEqualTo("product.categoryName", categoryName)
            .whereEqualTo("product.productName", productName)
            .get()
            .await()

        val productExists = querySnapshot.documents.any { documentSnapshot ->
            val cart = documentSnapshot.toObject(Cart::class.java)
            cart?.product?.productName == productName

        }
        if (productExists) {
            Response.Success(true)
        } else {
            Response.Success(false)
        }
    } catch (e: Exception) {
        Response.Error(e.message.toString())
    }

    override suspend fun addProductToCart(cartProduct: Cart): AddProductCartResponse = try {
        fireStore.collection("cart")
            .document(cartProduct.cartId)
            .set(cartProduct)
            .await()
        Response.Success(true)
    } catch (e: Exception) {
        Response.Error(e.message.toString())
    }

    override suspend fun updateUserSelectedQuantity(
        cartId: String,
        productQty: Int,
        userSelectedQty: Int,
        userEmail: String
    ): EditProductQuantityInCartResponse = try {
        val productDocRef = fireStore.collection("cart").document(cartId)
        productDocRef.update(
            mapOf(
                "product.productQty" to productQty,
                "product.userSelectedProductQty" to userSelectedQty
            )
        ).await()
        Response.Success(true)
    } catch (e: Exception) {
        Response.Error(e.message.toString())
    }

    override suspend fun fetchCartInfo(
        productName: String,
        categoryName: String,
        store: String,
        userEmail: String
    ): Cart? {
        val querySnapshot = fireStore.collection("cart")
            .whereEqualTo("loggedInUserEmail", userEmail)
            .whereEqualTo("product.storeName", store)
            .whereEqualTo("product.categoryName", categoryName)
            .whereEqualTo("product.productName", productName)

            .get()
            .await()

        val cartProduct: List<Cart> = querySnapshot.documents.mapNotNull { documentSnapshot ->
            documentSnapshot.toObject(Cart::class.java)
        }
        return cartProduct.firstOrNull()
    }

    override suspend fun deleteProductFromCart(
        product: Product,
        loggedInUserEmail: String
    ): DeleteCartProductResponse = try {
        val querySnapshot = fireStore.collection("cart")
            .whereEqualTo("loggedInUserEmail", loggedInUserEmail)
            .whereEqualTo("product.storeName", product.storeName)
            .whereEqualTo("product.categoryName", product.categoryName)
            .whereEqualTo("product.productName", product.productName)
            .get()
            .await()
        for (documentSnapshot in querySnapshot.documents) {
            val productDocumentRef = documentSnapshot.reference
            productDocumentRef.delete().await()
        }
        Response.Success(true)
    } catch (e: Exception) {
        Response.Error(e.message.toString())
    }

    override suspend fun fetchProductListFromCart(userEmail: String, store: String): List<Cart> {
        val productList: MutableList<Cart>
        val querySnapshot = fireStore.collection("cart")
            .whereEqualTo("product.storeName", store)
            .whereEqualTo("loggedInUserEmail", userEmail)
            .get()
            .await()

        val cartList: List<Cart> = querySnapshot.documents.mapNotNull { documentSnapshot ->
            documentSnapshot.toObject(Cart::class.java)
        }

        productList = cartList.toMutableList()

        return productList
    }

    override suspend fun createOrder(order: Order): AddOrderResponse = try {
        fireStore.collection("orders")
            .document(order.orderId)
            .set(order)
            .await()
        Response.Success(true)
    } catch (e: Exception) {
        Response.Error(e.message.toString())
    }

    override suspend fun createOrderDetails(orderDetail: OrderDetail): CreateOrderDetailResponse =
        try {
            fireStore.collection("orderDetails")
                .document(orderDetail.orderDetailId)
                .set(orderDetail)
                .await()
            Response.Success(true)
        } catch (e: Exception) {
            Response.Error(e.message.toString())
        }

    override suspend fun deleteCartInfoBasedOnLoggedUser(
        loggedInUser: String,
        storeName: String
    ): DeleteCartInfoResponse = try {
        val querySnapshot = fireStore.collection("cart")
            .whereEqualTo("loggedInUserEmail", loggedInUser)
            .whereEqualTo("product.storeName", storeName)
            .get()
            .await()
        for (documentSnapshot in querySnapshot.documents) {
            val productDocumentRef = documentSnapshot.reference
            productDocumentRef.delete().await()
        }
        Response.Success(true)
    } catch (e: Exception) {
        Response.Error(e.message.toString())
    }

    override suspend fun fetchOrderList(email: String): List<Order> {

        val orderList: MutableList<Order>
        val querySnapshot = fireStore.collection("orders")
            .whereEqualTo("loggedInUserEmail", email)
            .whereEqualTo("orderStatus", "In Progress")
            .get()
            .await()

        val category: List<Order> = querySnapshot.documents.mapNotNull { documentSnapshot ->
            documentSnapshot.toObject(Order::class.java)
        }

        orderList = category.toMutableList()

        return orderList
    }

    override suspend fun fetchOrderDetailList(email: String, orderID: String): List<OrderDetail> {
        val orderDetailList: MutableList<OrderDetail>
        val querySnapshot = fireStore.collection("orderDetails")
            .whereEqualTo("loggedInUserEmail", email)
            .whereEqualTo("orderId", orderID)
            .get()
            .await()

        val orderDetail: List<OrderDetail> =
            querySnapshot.documents.mapNotNull { documentSnapshot ->
                documentSnapshot.toObject(OrderDetail::class.java)

            }
        orderDetailList = orderDetail.toMutableList()
        return orderDetailList
    }

    override suspend fun fetchOrderDetailListByOrderId(orderID: String): List<OrderDetail> {
        val orderDetailList: MutableList<OrderDetail>
        val querySnapshot = fireStore.collection("orderDetails")
            .whereEqualTo("orderId", orderID)
            .get()
            .await()

        val orderDetail: List<OrderDetail> =
            querySnapshot.documents.mapNotNull { documentSnapshot ->
                documentSnapshot.toObject(OrderDetail::class.java)

            }
        orderDetailList = orderDetail.toMutableList()
        return orderDetailList
    }

    override suspend fun fetchOrderListByStore(store: String): List<Order> {
        val orderList: MutableList<Order>
        val querySnapshot = fireStore.collection("orders")
            .whereEqualTo("store", store)
            .whereEqualTo("orderStatus", "In Progress")
            .get()
            .await()

        val category: List<Order> = querySnapshot.documents.mapNotNull { documentSnapshot ->
            documentSnapshot.toObject(Order::class.java)
        }

        orderList = category.toMutableList()

        return orderList
    }

    override suspend fun fetchOrderInfo(orderID: String): Order? {
        val querySnapshot = fireStore.collection("orders")
            .whereEqualTo("orderId", orderID)
            .get()
            .await()

        val orders: List<Order> = querySnapshot.documents.mapNotNull { documentSnapshot ->
            documentSnapshot.toObject(Order::class.java)
        }
        return orders.firstOrNull()
    }

    override suspend fun updateOrderStatus(
        orderId: String,
        orderAdditionalMessage: String,
        orderStatus: String
    ) = try {


        val orderRef = fireStore.collection("orders").document(orderId)
        orderRef.update(
            mapOf(
                "orderStatus" to orderStatus,
                "additionalMessage" to orderAdditionalMessage
            )
        ).await()

        Response.Success(true)
    } catch (e: Exception) {
        Response.Error(e.message.toString())
    }

    override suspend fun fetchOrderListHistory(email: String): List<Order> {
        val orderList: MutableList<Order>
        val querySnapshot = fireStore.collection("orders")
            .whereEqualTo("loggedInUserEmail", email)
            .whereIn("orderStatus", listOf("Confirmed", "Rejected"))
            .orderBy("orderStatus")
            // .whereEqualTo("orderStatus", "Confirmed")
            .get()
            .await()

        val category: List<Order> = querySnapshot.documents.mapNotNull { documentSnapshot ->
            documentSnapshot.toObject(Order::class.java)
        }

        orderList = category.toMutableList()
        println("OrderListHistory is $orderList")

        return orderList
    }

    override suspend fun fetchOrderListHistoryByStore(store: String): List<Order> {
        val orderList: MutableList<Order>
        val querySnapshot = fireStore.collection("orders")
            .whereEqualTo("store", store)
            .whereIn("orderStatus", listOf("Confirmed", "Rejected"))
            .orderBy("orderStatus")
           // .whereEqualTo("orderStatus", "Confirmed")
            .get()
            .await()

        val category: List<Order> = querySnapshot.documents.mapNotNull { documentSnapshot ->
            documentSnapshot.toObject(Order::class.java)
        }

        orderList = category.toMutableList()

        return orderList
    }

    override suspend fun fetchProductsBySearchForStore(
        searchProduct:String,
        categoryName: String,
        store: String
    ): List<Product> {
        val productList: MutableList<Product>
      /* val querySnapshot = fireStore.collection("products")
            .whereEqualTo("storeName", store)
            .whereGreaterThanOrEqualTo("productName", searchProduct)
            .whereLessThanOrEqualTo("productName", searchProduct + "\uf8ff")
            .get()
            .await()*/

        val querySnapshot = fireStore.collection("products")
            .whereEqualTo("storeName", store)
            .whereArrayContains("keywords", searchProduct.trim())
            .limit(50)
            .get()
            .await()
        val products: List<Product> = querySnapshot.documents.mapNotNull { documentSnapshot ->
            documentSnapshot.toObject(Product::class.java)
        }

        productList = products.toMutableList()

        return productList
    }
}