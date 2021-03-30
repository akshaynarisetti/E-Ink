package com.example.e_ink.activities

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.e_ink.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_signup.*

class SignupActivity : BaseAcitivity() {
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_signup)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
setupActionBar()

        btn_sign_up.setOnClickListener {
            registerUser()
        }



    }

    private fun setupActionBar(){

        setSupportActionBar(toolbar_sign_up_activity)
        val actionbar = supportActionBar
        if(actionbar != null){
            actionbar.setDisplayHomeAsUpEnabled(true)
            actionbar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }
        toolbar_sign_up_activity.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun registerUser(){

        val name : String = et_name.text.toString().trim{ it <= ' '}
        val email : String = et_email.text.toString().trim{ it <= ' '}
        val password : String = et_password.text.toString().trim{ it <= ' '}

        if(validateForm(name,email, password)){
            showProgressDialog("Please wait")
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password).addOnCompleteListener { task ->
                hideProgressDialog()
                if(task.isSuccessful){
                    val firebaseUser : FirebaseUser = task.result!!.user!!
                    val registeredEmail  = firebaseUser.email!!
                    Toast.makeText(this,"$name you have succesfully registered",Toast.LENGTH_LONG).show()
                    FirebaseAuth.getInstance().signOut()
                    finish()
                }else{

                    Toast.makeText(this,"Registration failed",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun validateForm(name: String,email: String,password: String) : Boolean{
        return when{
            TextUtils.isEmpty(name)->{
                showErrorSnackBar("Please enter a name")
                false
        }
            TextUtils.isEmpty(email)->{
                showErrorSnackBar("Please enter an email address")
                false
            }
            TextUtils.isEmpty(password)->{
                showErrorSnackBar("Please enter a password")
                false
            } else -> {
                true
            }
        }
    }
}