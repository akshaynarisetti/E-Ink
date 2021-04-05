package com.example.e_ink.activities.firebase

import android.util.Log
import com.example.e_ink.activities.activities.SigninAcitivity
import com.example.e_ink.activities.activities.SignupActivity
import com.example.e_ink.activities.models.User
import com.example.e_ink.activities.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FirestoreClass {
    private val mFireStore = FirebaseFirestore.getInstance()

    fun registerUser(activity : SignupActivity,userInfo : User){
        mFireStore.collection(Constants.USERS).document(getCurrentUserId()).set(userInfo,
            SetOptions.merge()).addOnSuccessListener {
            activity.userRegisteredSuccess()
        }.addOnFailureListener {

            Log.e(activity.javaClass.simpleName,"Error")
        }

    }
    fun signInUser(activity: SigninAcitivity){
        mFireStore.collection(Constants.USERS).document(getCurrentUserId())
                .get()
                .addOnSuccessListener {document ->
            val loggedInUser = document.toObject(User::class.java)!!
                    activity.signInSuccess(loggedInUser)
        }.addOnFailureListener {

            Log.e("SignInUser","Error")
        }

    }


    fun getCurrentUserId() : String {
        var currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserID = ""
        if(currentUser != null){
            currentUserID = currentUser.uid
        }

        return currentUserID

    }
}