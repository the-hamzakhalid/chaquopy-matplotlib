package com.chaquo.myapplication.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.chaquo.myapplication.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val appActivity: AppCompatActivity = this

    private var binding: ActivityMainBinding? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        binding?.run {
            setContentView(binding?.root)
            init()
            initListener()
        }

    }


    private fun init() {


    }

    private fun ActivityMainBinding.initListener() {

        btnObjectRemover.setOnClickListener {
            Intent(appActivity, ObjectRemoverActivity::class.java).apply {
                startActivity(this)
            }
        }

        btnPlotGraph.setOnClickListener {
            Intent(appActivity, PlotGraphActivity::class.java).apply {
                startActivity(this)
            }
        }

    }
}