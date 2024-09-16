package com.chaquo.myapplication.ui

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.chaquo.myapplication.R
import com.chaquo.myapplication.databinding.ActivityObjectRemoverBinding
import com.chaquo.myapplication.utils.file.MASK_IMAGE
import com.chaquo.myapplication.utils.file.ORIGINAL_IMAGE
import com.chaquo.myapplication.utils.file.getTempPath
import com.chaquo.myapplication.utils.file.saveBitmapTemp
import com.chaquo.myapplication.utils.fileToBitmap
import com.chaquo.myapplication.utils.getBitmapFromContentUri
import com.chaquo.myapplication.utils.ioCoroutine
import com.chaquo.myapplication.utils.mainCoroutine
import com.chaquo.myapplication.utils.scaleDownBitmap
import com.chaquo.myapplication.utils.selectImageLauncherIntent
import com.chaquo.python.PyObject
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import java.io.File


class ObjectRemoverActivity : AppCompatActivity() {

    private val TAG = ObjectRemoverActivity::class.java.simpleName

    private val appActivity: AppCompatActivity = this

    private var binding: ActivityObjectRemoverBinding? = null

    private var pyObject: PyObject? = null

    private var pythonFunctionName = "in_paint_open_cv"

    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>

    private val loadingDialog = LoadingDialog()

    private var originalImagePath: String? = null
    private var maskImagePath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityObjectRemoverBinding.inflate(layoutInflater)

        binding?.run {
            setContentView(binding?.root)
            init()
            initListener()
        }

    }

    var resultingBitmap: Bitmap? = null

    private fun ActivityObjectRemoverBinding.init() {
        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(appActivity))
        }
        val py = Python.getInstance()
        pyObject = py.getModule(pythonFunctionName)

        pickImageLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val uri: Uri? = result.data?.data
                    uri?.let {
                        resultingBitmap = appActivity.getBitmapFromContentUri(it)
                        if (resultingBitmap != null) {
                            drawingView.setBackgroundImage(resultingBitmap)
                        }
                    }
                }
            }


    }


    private fun ActivityObjectRemoverBinding.initListener() {


        btnPickImage.setOnClickListener {
            pickImageLauncher.selectImageLauncherIntent(false)
        }

        btnClearDrawing.setOnClickListener {
            drawingView.clear()
            if (resultingBitmap != null) {
                drawingView.setBackgroundImage(resultingBitmap)
            }
        }


        btnRemoveObject.setOnClickListener {

            kotlin.runCatching {
                loadingDialog.show(appActivity)
                drawingView.setDrawingBackground(ContextCompat.getColor(appActivity, R.color.white))
                maskImagePath =
                    drawingView.exportDrawingWithBackground().saveBitmapTemp(
                        appActivity,
                        MASK_IMAGE
                    )
                originalImagePath =
                    drawingView.exportBackgroundBitmap().saveBitmapTemp(
                        appActivity,
                        ORIGINAL_IMAGE
                    )

                val outputPath = getTempPath()

                /*Use testing image form asset*/
                /*originalImagePath = appActivity.copyAssetImageToCache(ORIGINAL_IMAGE_NAME)
                maskImagePath = appActivity.copyAssetImageToCache(MASK_IMAGE_NAME)*/

                Log.d(TAG, "RemoveObject : originalImagePath $originalImagePath")
                Log.d(TAG, "RemoveObject : maskImagePath $maskImagePath")
                // Call the in_paint_image function and get byte array
                ioCoroutine {
                    pyObject?.callAttr(
                        pythonFunctionName, originalImagePath, maskImagePath, outputPath
                    )

                    if (outputPath != null) {
                        File(outputPath).run {
                            drawingView.setBackgroundImage(fileToBitmap(this))
                        }
                    }


                    mainCoroutine {
                        loadingDialog.dismiss()
                    }

                }
            }.getOrElse {
                Log.e(TAG, "RemoveObject: Exception -> ${it.message}")
                loadingDialog.dismiss()

            }

        }
    }
}