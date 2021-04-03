package com.example.e_ink.activities

import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.e_ink.R
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {
    val w = 1
    val h = 1
    val REQUEST_IMAGE_CAPTURE = 1
    private val IMAGE_CAPTURE_CODE: Int = 1001
    var conf :Bitmap.Config = Bitmap.Config.ARGB_8888; // see other conf types
    var bmp : Bitmap = Bitmap.createBitmap(w, h, conf); // this creates a MUTABLE bitmap
    var forUri : Uri = Uri.parse("android.resource://com.example.e_ink/drawable/dummyimage")
    private var imageList : MutableList<Uri?> = mutableListOf(forUri)
    val adapter = ImageAdapter(imageList)
    var i = 0
    var image_rui : Uri? = null
    lateinit var currentPhotoPath: String
    //

    private var btn : ImageButton? = null
    // private var llScroll: LinearLayout? = null
    // private var bitmap: Bitmap? = null
    private var llScroll : RecyclerView? = null
    //

    private val cropActivityResultContract = object : ActivityResultContract<Any?,Uri?>(){


        override fun createIntent(context: Context, input: Any?): Intent {
            return CropImage.activity().getIntent(this@MainActivity)
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            return CropImage.getActivityResult(intent)?.uri
        }
    }
    private lateinit var cropActivityResultLauncher : ActivityResultLauncher<Any?>


    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        et_pdf_name.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                //Perform Code
                closeKeyboard()
                return@OnKeyListener true
            }
            false
        })


        val attrib = window.attributes
        attrib.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES


        // llScroll = findViewById(R.id.llScroll)
        llScroll = findViewById(R.id.rvImages)
        /* btn!!.setOnClickListener {
             Log.d("size", " " + llScroll!!.width + "  " + llScroll!!.width)
             bitmap = loadBitmapFromView(
                     llScroll,
                     llScroll!!.width,
                     llScroll!!.height
             ) */
        btn_pdfExport.setOnClickListener {
            val builder: StrictMode.VmPolicy.Builder = StrictMode.VmPolicy.Builder()
            StrictMode.setVmPolicy(builder.build())
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            createPdf(et_pdf_name.text.toString().substringBeforeLast('.'))
            openGeneratedPDF(et_pdf_name.text.toString().substringBeforeLast('.'))
        }

        rvImages.adapter = adapter
        rvImages.layoutManager= LinearLayoutManager(this)

        imageButton3.setOnClickListener {
            // dispatchTakePictureIntent()
            cropActivityResultLauncher.launch(null)
        }
        cropActivityResultLauncher = registerForActivityResult(cropActivityResultContract) {
            it?.let{uri ->
                if (i==0) {
                    i = i+1
                    imageList.removeAt(0)
                }

                imageList.add(uri)
                adapter.notifyDataSetChanged()
                et_pdf_name.visibility = View.VISIBLE



            }
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

            if (i==0) {
                i = i+1
                imageList.removeAt(0)
            }

            imageList.add(image_rui)
            adapter.notifyDataSetChanged()
        }
    }

    private fun dispatchTakePictureIntent() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE,"New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION,"From the Camera")
        image_rui = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values)
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,image_rui)
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            print("activity not found")
        }

    }





    private fun createPdf(name: String) {
        var count = 1
        val wm =
                getSystemService(Context.WINDOW_SERVICE) as WindowManager
        //  Display display = wm.getDefaultDisplay();
        val displaymetrics = DisplayMetrics()
        this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics)
        val hight = displaymetrics.heightPixels.toFloat()
        val width = displaymetrics.widthPixels.toFloat()
        val convertHighet = hight.toInt()
        val convertWidth = width.toInt()
        val document = PdfDocument()
        for (r in imageList){
            var  on : InputStream? = contentResolver.openInputStream(r!!)
            var bitmap: Bitmap = BitmapFactory.decodeStream(on)
            on!!.close()
            val pageInfo = PdfDocument.PageInfo.Builder(convertWidth, convertHighet, count).create()
            count += 1
            val page = document.startPage(pageInfo)
            val canvas = page.canvas
            val paint = Paint()
            var e = 0F
            canvas.drawPaint(paint)
            bitmap = Bitmap.createScaledBitmap(bitmap!!, convertWidth, convertHighet, true)
            paint.color = Color.BLUE
            canvas.drawBitmap(bitmap!!, e, e, null)
            document.finishPage(page)
        }

        // write the document content
        val targetPdf = "/sdcard/${name}.pdf"
        val filePath: File
        filePath = File(targetPdf)
        try {
            document.writeTo(FileOutputStream(filePath))
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Something wrong: ", Toast.LENGTH_LONG).show()
        }

        // close the document
        document.close()
        Toast.makeText(this, "PDF is created!!!", Toast.LENGTH_SHORT).show()

    }



    private fun openGeneratedPDF(name : String) {
        val file = File("/sdcard/${name}.pdf")
        if (file.exists()) {
            val intent = Intent(Intent.ACTION_VIEW)
            val uri = Uri.fromFile(file)
            intent.setDataAndType(uri, "application/pdf")
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(
                        this@MainActivity,
                        "No Application available to view pdf",
                        Toast.LENGTH_LONG
                ).show()
            }
        }
    }



    companion object {
        fun loadBitmapFromView(v: View?, width: Int, height: Int): Bitmap {
            val b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val c = Canvas(b)
            v!!.draw(c)
            return b
        }
    }

    fun closeKeyboard(){
        val view = this.currentFocus
        if(view != null){
            val hideMe = getSystemService( Context.INPUT_METHOD_SERVICE) as InputMethodManager
            hideMe.hideSoftInputFromWindow(view.windowToken,0)

        }




    }








}
