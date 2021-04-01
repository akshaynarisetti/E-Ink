package com.example.e_ink.activities

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.e_ink.R
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    val w = 1
    val h = 1
    val REQUEST_IMAGE_CAPTURE = 1
    var conf :Bitmap.Config = Bitmap.Config.ARGB_8888; // see other conf types
    var bmp : Bitmap = Bitmap.createBitmap(w, h, conf); // this creates a MUTABLE bitmap
    var imageList : MutableList<Bitmap> = mutableListOf(bmp)
    val adapter = ImageAdapter(imageList)
    var i = 0
    var image_rui : Uri? = null
    lateinit var currentPhotoPath: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)



        rvImages.adapter = adapter
        rvImages.layoutManager= LinearLayoutManager(this)

        imageButton3.setOnClickListener {
            dispatchTakePictureIntent()
        }


    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
                "JPEG_${timeStamp}_", /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            if (i==0) {
                i = i+1
                imageList.removeAt(0)
            }

            imageList.add(imageBitmap)
            adapter.notifyDataSetChanged()
        }
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            print("activity not found")
        }

    }




    }
