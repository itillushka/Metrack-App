package com.example.metrack.firestore



import com.example.metrack.RegisterActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FireStoreClass {

    private val mFireStore = FirebaseFirestore.getInstance()


    fun registerUserFS(activity: RegisterActivity, userInfo: User) {

        mFireStore.collection("users")
            .document(userInfo.id)
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegistrationSuccess()

            }
            .addOnFailureListener {

            }
    }
}