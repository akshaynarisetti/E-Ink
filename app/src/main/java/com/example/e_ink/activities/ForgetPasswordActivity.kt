package com.example.e_ink.activities

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.strictmode.WebViewMethodCalledOnWrongThreadViolation
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.getSystemService
import com.example.e_ink.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_forget_password.*
import kotlinx.android.synthetic.main.activity_signin_acitivity.toolbar_sign_in_activity

class ForgetPasswordActivity : BaseAcitivity() {
    private lateinit var auth: FirebaseAuth


    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget_password)


        setupActionBar()

        btn_reset_password.setOnClickListener {

            closeKeyboard()
            resetUser()
        }

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )


        val attrib = window.attributes
        attrib.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES





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

    private fun resetUser(){


        val emailAddress : String = et_email_signin_reset.text.toString().trim{ it <= ' '}


        if(validateForm(emailAddress)){
            animationView.visibility = View.VISIBLE
            closeKeyboard()



            val emailAddress : String = et_email_signin_reset.text.toString().trim{ it <= ' '}

            FirebaseAuth.getInstance().sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener { task ->
                    if(task.isSuccessful) {
                        Log.d("ResetPassword", "Email sent.")
                        Toast.makeText(this,"Email Sent",Toast.LENGTH_SHORT).show()



                    } else{
                        Toast.makeText(this,"Password Reset Failed",Toast.LENGTH_SHORT).show()
                    }
                }

        }
    }

    private fun validateForm(email: String) : Boolean{
        return when{

            TextUtils.isEmpty(email)->{
                showErrorSnackBar("Please enter an email address")
                false
            } else -> {
                true
            }
        }
    }




}


