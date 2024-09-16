package com.chaquo.myapplication.ui

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.chaquo.myapplication.databinding.ActivityPlotGraphBinding
import com.chaquo.python.PyException
import com.chaquo.python.PyObject
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform


class PlotGraphActivity : AppCompatActivity() {

    private val appActivity: AppCompatActivity = this

    private var binding: ActivityPlotGraphBinding? = null


    private var pyObject: PyObject? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlotGraphBinding.inflate(layoutInflater)

        binding?.run {
            setContentView(binding?.root)
            init()
            initListener()
        }

    }


    private fun init() {
        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(appActivity))
        }
        val py = Python.getInstance()
        pyObject = py.getModule("plot")

    }

    private fun ActivityPlotGraphBinding.initListener() {

        plotBtn.setOnClickListener {
            try {
                val bytes = pyObject?.callAttr(
                    "plot", etX.text.toString(), etY.text.toString()
                )?.toJava(ByteArray::class.java)
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes?.size ?: 0)
                ivPlot.setImageBitmap(bitmap)
                currentFocus?.let {
                    (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
                        it.windowToken, 0
                    )
                }
            } catch (e: PyException) {
                Toast.makeText(appActivity, e.message, Toast.LENGTH_LONG).show()
            }
        }

    }
}