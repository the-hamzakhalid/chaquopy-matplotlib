package com.chaquo.myapplication.utils.file

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

/**
 * @author Hamza Khalid
 * Sr. Software Engineer
 * Created on 13 Mar,2024 14:16
 * Copyright (c) All rights reserved.
 * @see "<a href="https://www.linkedin.com/in/the-hamzakhalid/">Linkedin Profile</a>"
 */

const val RESOURCES_PREFIX = "android.resource://"
const val DRAWABLE_PREFIX = "drawable://"
const val ASSET_PREFIX = "file:///android_asset/"

const val JPG_IMAGE = ".jpg"
const val PNG_IMAGE = ".png"
const val WEBP_IMAGE = ".webp"
const val DNG_IMAGE = ".dng"
const val JPEG_IMAGE = ".jpeg"
const val MP4_Video = ".mp4"
const val AVI_VIDEO = ".avi"
const val GIF_VIDEO = ".gif"
const val MKV_VIDEO = ".mkv"
const val PDF_FILE = ".pdf"
const val HIDDEN_FILE = "."

const val MIME_TYPE_IMAGE = "image/*"
const val MIME_TYPE_VIDEO = "video/*"



val storagePaths: List<String> by lazy {
    mutableListOf<String>().apply {
        // Add internal storage path
        add(File("/storage/emulated/0").path)

        // Add SD card path (if available)
        val externalStorageDirectories = getExternalStorageDirectories()
        if (externalStorageDirectories.isNotEmpty()) {
            addAll(externalStorageDirectories)
        }
    }
}

 fun getExternalStorageDirectories(): List<String> {
    val dirs = mutableListOf<String>()
    val extDirs = File("/storage").listFiles()
    extDirs?.forEach { file ->
        if (file.isDirectory && file.canRead()) {
            dirs.add(file.absolutePath)
        }
    }
    return dirs
}
const val ORIGINAL_IMAGE = "original_image"
const val MASK_IMAGE = "mask_image"
const val OUTPUT_IMAGE="output_image"

fun Bitmap.saveBitmapTemp(
    context: Context, name: String = ORIGINAL_IMAGE, fileType: String = PNG_IMAGE
): String? {
   // context.deletePreviousFile(name)
    //create a file to write bitmap data
    val mediaStorageDir = File(context.cacheDir, "$name$fileType")
    mediaStorageDir.createNewFile()
    //Convert bitmap to byte array
    val bos = ByteArrayOutputStream()
    compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos)
    val bitmapdata = bos.toByteArray()
    //write the bytes in file
    val fos = FileOutputStream(mediaStorageDir)
    fos.write(bitmapdata)
    fos.flush()
    fos.close()
    Log.d("Saved", "" +mediaStorageDir.absolutePath)
    return mediaStorageDir.absolutePath
}

fun Context.getTempPath(
     name: String = OUTPUT_IMAGE, fileType: String = PNG_IMAGE
): String? {
    // context.deletePreviousFile(name)
    //create a file to write bitmap data
    val mediaStorageDir = File(cacheDir, "$name$fileType")
    mediaStorageDir.createNewFile()
    Log.d("Saved", "" +mediaStorageDir.absolutePath)
    return mediaStorageDir.absolutePath
}