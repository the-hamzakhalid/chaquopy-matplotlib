package com.chaquo.myapplication.ui


import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.chaquo.myapplication.R
import com.chaquo.myapplication.databinding.DialogLoadingBinding


/**
 * Created by Hamza Khalid
 * Sr. Software Engineer
 * Created on 07 Sep,2021 10:04
 * Copyright (c) All rights reserved.
 * @see "<a href="https://www.linkedin.com/in/the-hamzakhalid/">Linkedin Profile</a>"
 */


class LoadingDialog  {


    private var binding: DialogLoadingBinding? = null
    private var dialog: Dialog? = null
    private var activity: AppCompatActivity? = null

    private var TAG = LoadingDialog::class.java.simpleName

    fun show(activity: AppCompatActivity) {
        this.activity = activity
        if (dialog?.isShowing == true) return

        dialog = Dialog(activity)
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(activity), R.layout.dialog_loading, null, false
        )

        binding?.run {
            root.let { dialog?.setContentView(it) }
            val inset = InsetDrawable(ColorDrawable(Color.TRANSPARENT), 30, 20, 30, 20)
            dialog?.window?.setBackgroundDrawable(inset)
            dialog?.window?.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT
            )
           /* dialog?.window?.attributes?.windowAnimations =
                com.thehk.common.R.style.DialogAnimationFadeInToOut
*/
            setCancelView(true)

            activity.let {
                if (!it.isFinishing) {
                    //show dialog
                    it.runOnUiThread { dialog?.show() }
                }
            }
        }

    }


    fun dismiss() {
        dialog?.dismiss()
    }

    private fun setCancelView(flag: Boolean) {
        dialog?.setCancelable(flag)
        dialog?.setCanceledOnTouchOutside(flag)
    }
}





