package com.mycart.data.repository.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.mycart.domain.model.Store
import com.mycart.domain.model.User
import com.mycart.domain.repository.firebase.AddStoreResponse
import com.mycart.domain.repository.firebase.MyCartFireStoreRepository
import com.mycart.ui.common.Response
import kotlinx.coroutines.tasks.await

class MyCartFireStoreRepositoryImpl(private val fireStore: FirebaseFirestore) : MyCartFireStoreRepository {

    override suspend fun addUserToFireStore(user: User) =try {
        val querySnapshot = fireStore.collection("users")
            .whereEqualTo("userEmail", user.userEmail)
            .get()
            .await()
        if(querySnapshot.isEmpty) {
            fireStore.collection("users")
                .document(user.userEmail)
                .set(user)
                .await()
            Response.Success(true)
        }else{
            Response.Error("User Already Exists....")
        }

    } catch (e: Exception) {
        Response.Error(e.message.toString())
    }


    override suspend fun createStore(store: Store) =try {
        fireStore.collection("stores")
            .document(store.storeName)
            .set(store)
            .await()
        Response.Success(true)
    } catch (e: Exception) {
        Response.Error(e.message.toString())
    }

    override suspend fun fetchAllStores() : List<Store>{
        val stores: List<Store>
        val querySnapshot = fireStore.collection("stores").get().await()
         stores = querySnapshot.documents.mapNotNull { document ->
            document.toObject(Store::class.java)
        }
        return stores
    }

    override suspend fun checkForAdmin(email: String): User?{
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


}