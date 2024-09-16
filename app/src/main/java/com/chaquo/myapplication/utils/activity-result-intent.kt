package com.chaquo.myapplication.utils

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher

/**
 * @author Hamza Khalid
 * PSE - Technical Lead
 * Created on 09 Sep,2024 15:23
 * Copyright (c) All rights reserved.
 * @see "<a href="https://www.linkedin.com/in/the-hamzakhalid/">Linkedin Profile</a>"

 */
const val IMAGE_LAUNCHER = "image/*"
fun ActivityResultLauncher<Intent>.selectImageLauncherIntent(isMultiple: Boolean = false) { //Code for gallery
    val intent = Intent()
    intent.type = IMAGE_LAUNCHER
    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, isMultiple)
    intent.action = Intent.ACTION_GET_CONTENT
    launch(
        Intent.createChooser(
            intent, "Select Picture"
        )
    )
}



