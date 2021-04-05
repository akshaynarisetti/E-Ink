package com.example.e_ink.activities.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.e_ink.R
import com.example.e_ink.activities.models.User
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_signin_acitivity.*
import kotlinx.android.synthetic.main.activity_signin_acitivity.et_email_signin
import kotlinx.android.synthetic.main.activity_signin_acitivity.toolbar_sign_in_activity

class SigninAcitivity : BaseAcitivity() {
    private lateinit var auth: FirebaseAuth




    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin_acitivity)
        auth = FirebaseAuth.getInstance()

        btn_sign_in.setOnClickListener {
            closeKeyboard()
            signInRegisteredUser()
        }
        tv_signup_redirector.setOnClickListener {
            closeKeyboard()
            startActivity(Intent(this,
                SignupActivity::class.java))
            finish()
        }
        tv_forgetPassword_redirector.setOnClickListener {
            closeKeyboard()
            startActivity(Intent(this, ForgetPasswordActivity::class.java))
            finish()
        }

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )


        val attrib = window.attributes
        attrib.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES

        setupActionBar()

    }

    private fun setupActionBar() {

        setSupportActionBar(toolbar_sign_in_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }

        toolbar_sign_in_activity.setNavigationOnClickListener { onBackPressed() }
    }

    fun signInSuccess(user : User){
        hideProgressDialog()
        startActivity(Intent(this,MainActivity::class.java))
        finish()

    }

    private fun signInRegisteredUser(){
        val email : String = et_email_signin.text.toString().trim(){ it <= ' '}
        val password : String = et_password_signin.text.toString().trim(){ it <= ' '}

        if(validateForm(email, password)) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("Sign in", "signInWithEmail:success")
                        val user = auth.currentUser
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("Sign up", "signInWithEmail:failure", task.exception)
                        Toast.makeText(
                            baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                }
        }
            }




    private fun validateForm(email: String,password: String) : Boolean{
        return when{

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

