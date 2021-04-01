package com.example.e_ink.activities

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi
import com.example.e_ink.R
import kotlinx.android.synthetic.main.activity_intro.*

class IntroActivity : BaseAcitivity() {
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)





        setContentView(R.layout.activity_intro)

        btn_sign_up_intro.setOnClickListener {
            val intent = Intent(this@IntroActivity,
                SignupActivity::class.java)
            startActivity(intent)
        }

        btn_sign_in_intro.setOnClickListener{
            val intent = Intent(this@IntroActivity,
                SigninAcitivity::class.java)
            startActivity(intent)

        }

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        val attrib = window.attributes
        attrib.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES





    }
}