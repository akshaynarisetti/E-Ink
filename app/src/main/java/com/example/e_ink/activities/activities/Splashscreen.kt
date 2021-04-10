package com.example.e_ink.activities.activities

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.example.e_ink.R
import com.example.e_ink.activities.firebase.FirestoreClass


class Splashscreen : AppCompatActivity() {

val SPLASH_SCREEN = 3000
    private lateinit var topAnimation : Animation
    private lateinit var bottomAnimation : Animation
    private lateinit var title_txt :TextView
    private lateinit var imageView :ImageView
    private  lateinit var description_txt : TextView
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        window.decorView.apply {
            systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

        window.decorView.setOnSystemUiVisibilityChangeListener {
            window.decorView.apply {
                systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
            }
        }
        val attrib = window.attributes
        attrib.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
    
        setContentView(R.layout.activity_splashscreen)
        val actionBar = supportActionBar
        actionBar!!.hide()
        topAnimation = AnimationUtils.loadAnimation(this,
            R.anim.top_animation
        )
        bottomAnimation = AnimationUtils.loadAnimation(this,
            R.anim.bottom_animation
        )
       imageView = findViewById(R.id.hr_image)
        title_txt = findViewById(R.id.title_text)
        description_txt = findViewById(R.id.title_text2)

        imageView.animation = topAnimation
        title_txt.animation = bottomAnimation
        description_txt.animation = bottomAnimation

        Handler().postDelayed({
            var currentUserID = FirestoreClass().getCurrentUserID()
            if(currentUserID.isNotEmpty()){
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)


            }else{
                val intent = Intent(this, IntroActivity::class.java)
                startActivity(intent)
            }
            finish()

        },SPLASH_SCREEN.toLong())

    }

}