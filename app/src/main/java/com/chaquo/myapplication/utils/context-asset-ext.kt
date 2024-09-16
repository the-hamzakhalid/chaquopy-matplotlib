package com.chaquo.myapplication.utils

import android.content.Context
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

/**
 * @author Hamza Khalid
 * PSE - Technical Lead
 * Created on 11 Sep,2024 12:14
 * Copyright (c) All rights reserved.
 * @see "<a href="https://www.linkedin.com/in/the-hamzakhalid/">Linkedin Profile</a>"
 */

const val ORIGINAL_IMAGE_NAME="original_image.png"
const val MASK_IMAGE_NAME="mask_image.png"


fun Context.loadImageFromAssets(fileName: String): ByteArray {
    val assetManager = assets
    val inputStream: InputStream = assetManager.open(fileName)
    return inputStream.readBytes()
}

fun Context.copyAssetImageToCache(fileName: String): String {
    val assetManager = assets
    val file = File(cacheDir, fileName)

    assetManager.open(fileName).use { inputStream ->
        FileOutputStream(file).use { outputStream ->
            inputStream.copyTo(outputStream)
        }
    }

    return file.absolutePath // Return the file path
}