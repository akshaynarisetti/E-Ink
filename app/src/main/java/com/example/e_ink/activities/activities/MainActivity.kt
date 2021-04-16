package com.example.e_ink.activities.activities

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.annotation.RequiresApi
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.e_ink.R
import com.example.e_ink.activities.firebase.FirestoreClass
import com.google.firebase.auth.FirebaseAuth
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*
import com.google.android.material.navigation.NavigationView
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.pspdfkit.configuration.activity.PdfActivityConfiguration
import com.pspdfkit.document.PdfDocumentLoader
import com.pspdfkit.document.processor.PdfProcessor
import com.pspdfkit.document.processor.PdfProcessorTask
import com.pspdfkit.document.processor.ocr.OcrLanguage
import com.pspdfkit.ui.PdfActivity
import kotlinx.android.synthetic.main.nav_header_main.*


class MainActivity : BaseAcitivity(),NavigationView.OnNavigationItemSelectedListener {
    val w = 1
    val h = 1
    val REQUEST_IMAGE_CAPTURE = 1
    private val IMAGE_CAPTURE_CODE: Int = 1001
    var conf: Bitmap.Config = Bitmap.Config.ARGB_8888; // see other conf types
    var bmp: Bitmap = Bitmap.createBitmap(w, h, conf); // this creates a MUTABLE bitmap
    var forUri: Uri = Uri.parse("android.resource://com.example.e_ink/drawable/dummyimage")
    private var imageList: MutableList<Uri?> = mutableListOf(forUri)
    val adapter = ImageAdapter(imageList)
    var i = 0
    var image_rui: Uri? = null
    lateinit var currentPhotoPath: String
    var f = 0
    //
    companion object {
        const val MY_PROFILE_REQUEST_CODE: Int = 11

    }

    private var btn: ImageButton? = null

    // private var llScroll: LinearLayout? = null
    // private var bitmap: Bitmap? = null
    private var llScroll: RecyclerView? = null
    //

    private val cropActivityResultContract = object : ActivityResultContract<Any?, Uri?>() {


        override fun createIntent(context: Context, input: Any?): Intent {
            return CropImage.activity().getIntent(this@MainActivity)
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            return CropImage.getActivityResult(intent)?.uri
        }
    }
    private lateinit var cropActivityResultLauncher: ActivityResultLauncher<Any?>


    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        FirestoreClass().loadUserData(this@MainActivity)


        btn_ocr.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    v.background.setColorFilter(-0x1f0b8adf, PorterDuff.Mode.SRC_ATOP)
                    v.invalidate()
                }
                MotionEvent.ACTION_UP -> {
                    v.background.clearColorFilter()
                    v.invalidate()
                }
            }
            false
        }

        btn_pdfExport.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    v.background.setColorFilter(-0x1f0b8adf, PorterDuff.Mode.SRC_ATOP)
                    v.invalidate()
                }
                MotionEvent.ACTION_UP -> {
                    v.background.clearColorFilter()
                    v.invalidate()
                }
            }
            false
        }

        et_pdf_name.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                //Perform Code
                closeKeyboard()
                return@OnKeyListener true
            }
            false
        })
        //
        setupActionBar()

        // END

        // TODO (Step 8: Assign the NavigationView.OnNavigationItemSelectedListener to navigation view.)
        // START
        // Assign the NavigationView.OnNavigationItemSelectedListener to navigation view.
        nav_view.setNavigationItemSelectedListener(this)

        //


        val attrib = window.attributes
        attrib.layoutInDisplayCutoutMode =
            WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES


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
            showProgressDialog("Generating pdf",this)
            val handler = Handler()
            handler.postDelayed({
                hideProgressDialog()
                val builder: StrictMode.VmPolicy.Builder = StrictMode.VmPolicy.Builder()
                StrictMode.setVmPolicy(builder.build())
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                createPdf(et_pdf_name.text.toString().substringBeforeLast('.'))
                hideProgressDialog()
                generate(et_pdf_name.text.toString().substringBeforeLast('.'))
            }, 1000)


        }

        btn_ocr.setOnClickListener {
            showProgressDialog("Generating OCR",this)
            val handler = Handler()
            handler.postDelayed({
                hideProgressDialog()
                val builder: StrictMode.VmPolicy.Builder = StrictMode.VmPolicy.Builder()
                StrictMode.setVmPolicy(builder.build())
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                createPdf(et_pdf_name.text.toString().substringBeforeLast('.'))
                ocrgenerate(et_pdf_name.text.toString().substringBeforeLast('.'))
                // do something after 1000ms
            }, 1000)


        }

        rvImages.adapter = adapter
        rvImages.layoutManager = LinearLayoutManager(this)

        llStart.setOnClickListener {
            // dispatchTakePictureIntent()
            Dexter.withContext(this)
                    .withPermissions(
                            android.Manifest.permission.READ_EXTERNAL_STORAGE,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                    .withListener(object : MultiplePermissionsListener {
                        override fun onPermissionsChecked(report: MultiplePermissionsReport?) {

                            // Here after all the permission are granted launch the gallery to select and image.
                            if (report!!.areAllPermissionsGranted()) {

                                cropActivityResultLauncher.launch(null)
                            }
                        }

                        override fun onPermissionRationaleShouldBeShown(
                                permissions: MutableList<PermissionRequest>?,
                                token: PermissionToken?
                        ) {
                            showRationalDialogForPermissions()
                        }
                    }).onSameThread()
                    .check()

        }
        cropActivityResultLauncher = registerForActivityResult(cropActivityResultContract) {
            it?.let { uri ->
                if (i == 0) {
                    i = i + 1
                    imageList.removeAt(0)
                }

                imageList.add(uri)
                adapter.notifyDataSetChanged()
                et_pdf_name.visibility = View.VISIBLE
                btn_pdfExport.visibility = View.VISIBLE
               // btn_ocr.visibility = View.VISIBLE


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
    fun updateNavigationUserDetails(user :com.example.e_ink.activities.models.User){
        val headerView = nav_view.getHeaderView(0)
        Glide.with(this)
                .load(user.image)
                .centerCrop()
                .placeholder(R.drawable.ic_user_place_holder)
                .into(iv_user_image)
        val navUsername = headerView.findViewById<TextView>(R.id.tv_username)
        navUsername.text = user.name

    }

    private fun showRationalDialogForPermissions() {
        androidx.appcompat.app.AlertDialog.Builder(this)
                .setMessage("It Looks like you have turned off permissions required for this feature. It can be enabled under Application Settings")
                .setPositiveButton("GO TO SETTINGS"
                ) { _, _ ->
                    try {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri = Uri.fromParts("package", packageName, null)
                        intent.data = uri
                        startActivity(intent)
                    } catch (e: ActivityNotFoundException) {
                        e.printStackTrace()
                    }
                }
                .setNegativeButton("Cancel") { dialog,
                                               _ ->
                    dialog.dismiss()
                }.show()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            if (i == 0) {
                i = i + 1
                imageList.removeAt(0)
            }

            imageList.add(image_rui)
            adapter.notifyDataSetChanged()

        }
        if (resultCode == Activity.RESULT_OK
            && requestCode == MY_PROFILE_REQUEST_CODE
        ) {
            // Get the user updated details.
            FirestoreClass().loadUserData(this@MainActivity)
        } else {
            Log.e("Cancelled", "Cancelled")
        }

    }

    private fun dispatchTakePictureIntent() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
        image_rui = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_rui)
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
        for (r in imageList) {
            var on: InputStream? = contentResolver.openInputStream(r!!)
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


    private fun openGeneratedPDF(name: String) {
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

    private fun generate(name: String) {

        val file = File("/sdcard/${name}.pdf")
        if (file.exists()) {
            var context: Context = this
            val uri = Uri.fromFile(file)
            val config = PdfActivityConfiguration.Builder(context).build()
            PdfActivity.showDocument(this, uri, config)
        }
        }

        private fun ocrgenerate(name: String) {

            val file = File("/sdcard/${name}.pdf")
            if (file.exists()) {
                var context: Context = this
                val uri = Uri.fromFile(file)
                val document = PdfDocumentLoader.openDocument(this, uri)
// This allows us to run OCR on all pages of the document.
                val pageIndexesToProcess = (0 until document.pageCount).toSet()

// Create a `PdfProcessorTask` configured to perform OCR.
// Make sure to use the correct language.
                val task = PdfProcessorTask.fromDocument(document)
                        .performOcrOnPages(pageIndexesToProcess, OcrLanguage.ENGLISH)

// Create a path where we can save the document.
                val outputFile = File(filesDir, "${document.title}-ocr-processed.pdf")

// Finally we process the file.
// OCR is quite slow, so make sure to run this in a background thread, or use `processDocumentAsync`.
                PdfProcessor.processDocument(task, outputFile)

                val urii = Uri.fromFile(outputFile)
                val config = PdfActivityConfiguration.Builder(context).build()
                PdfActivity.showDocument(this, urii, config)
            }
        }







        override fun onBackPressed() {
            if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
                drawer_layout.closeDrawer(GravityCompat.START)
            } else {
                // A double back press function is added in Base Activity.
                doubleBackToExit()
            }
        }
        // END

        // TODO (Step 7: Implement members of NavigationView.OnNavigationItemSelectedListener.)
        // START
        override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
            when (menuItem.itemId) {
                R.id.nav_my_profile -> {

                    // TODO (Step 2: Launch the my profile activity for Result.)
                    // START
                    startActivityForResult(Intent(this@MainActivity, MyProfileActivity::class.java), MY_PROFILE_REQUEST_CODE)
                    // END
                }

                R.id.nav_sign_out -> {
                    // Here sign outs the user from firebase in this device.
                    FirebaseAuth.getInstance().signOut()

                    // Send the user to the intro screen of the application.
                    val intent = Intent(this, IntroActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }
            }
            drawer_layout.closeDrawer(GravityCompat.START)
            return true
        }
        // END

        // TODO (Step 1: Create a function to setup action bar.)
        // START
        /**
         * A function to setup action bar
         */
        private fun setupActionBar() {

            setSupportActionBar(toolbar_main_activity)
            toolbar_main_activity.setNavigationIcon(R.drawable.ic_action_navigation_menu)

            // TODO (Step 3: Add click event for navigation in the action bar and call the toggleDrawer function.)
            // START
            toolbar_main_activity.setNavigationOnClickListener {
                toggleDrawer()
            }
            // END
        }
        // END

        // TODO (Step 2: Create a function for opening and closing the Navigation Drawer.)
        // START
        /**
         * A function for opening and closing the Navigation Drawer.
         */
        private fun toggleDrawer() {

            if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
                drawer_layout.closeDrawer(GravityCompat.START)
            } else {
                drawer_layout.openDrawer(GravityCompat.START)
            }
        }


}














