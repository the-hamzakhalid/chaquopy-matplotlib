package com.chaquo.myapplication.utils

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Rect
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException

/**
 * @author Hamza Khalid
 * PSE - Technical Lead
 * Created on 11 Sep,2024 16:52
 * Copyright (c) All rights reserved.
 * @see "<a href="https://www.linkedin.com/in/the-hamzakhalid/">Linkedin Profile</a>"
 */


fun isMarshmallowPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
fun isNougatPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
fun isNougatMR1Plus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1
fun isOreoPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
fun isOreo() = Build.VERSION.SDK_INT == Build.VERSION_CODES.O
fun isOreoMr1Plus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1
fun isPiePlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
fun isQPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
fun isRPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
fun isSPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S


fun Context.getBitmapFromContentUri(imageUri: Uri): Bitmap? {
    val decodedBitmap = getBitmapFromContentUriWithoutExif(imageUri)

    val orientation = if (isNougatPlus()) {
        getExifOrientationTag(contentResolver, imageUri)
    } else {
        return decodedBitmap
    }
    var rotationDegrees = 0
    var flipX = false
    var flipY = false
    when (orientation) {
        ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> flipX = true
        ExifInterface.ORIENTATION_ROTATE_90 -> rotationDegrees = 90
        ExifInterface.ORIENTATION_TRANSPOSE -> {
            rotationDegrees = 90
            flipX = true
        }

        ExifInterface.ORIENTATION_ROTATE_180 -> rotationDegrees = 180
        ExifInterface.ORIENTATION_FLIP_VERTICAL -> flipY = true
        ExifInterface.ORIENTATION_ROTATE_270 -> rotationDegrees = -90
        ExifInterface.ORIENTATION_TRANSVERSE -> {
            rotationDegrees = -90
            flipX = true
        }

        ExifInterface.ORIENTATION_UNDEFINED, ExifInterface.ORIENTATION_NORMAL -> {}
        else -> {}
    }
    return rotateBitmap(decodedBitmap, rotationDegrees, flipX, flipY)
}


@RequiresApi(Build.VERSION_CODES.N)
private fun getExifOrientationTag(resolver: ContentResolver, imageUri: Uri): Int {
    // We only support parsing EXIF orientation tag from local file on the device.
    // See also:
    // https://android-developers.googleblog.com/2016/12/introducing-the-exifinterface-support-library.html
    if (ContentResolver.SCHEME_CONTENT != imageUri.scheme && ContentResolver.SCHEME_FILE != imageUri.scheme) {
        return 0
    }
    var exif: ExifInterface
    try {
        resolver.openInputStream(imageUri).use { inputStream ->
            if (inputStream == null) {
                return 0
            }
            exif = ExifInterface(inputStream)
        }
    } catch (e: IOException) {
        Log.e("TAG", "failed to open file to read rotation meta data: $imageUri", e)
        return 0
    }
    return exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
}

/**
 * TODO =================> Rotates a bitmap if it is converted from a bytebuffer.
 * * Ref : See the project of ml kit firebase
 */
private fun rotateBitmap(
    bitmap: Bitmap, rotationDegrees: Int, flipX: Boolean, flipY: Boolean,
): Bitmap? {
    val matrix = Matrix()

    // Rotate the image back to straight.
    matrix.postRotate(rotationDegrees.toFloat())

    // Mirror the image along the X or Y axis.
    matrix.postScale(if (flipX) -1.0f else 1.0f, if (flipY) -1.0f else 1.0f)
    val rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)

    // Recycle the old bitmap if it has changed.
    if (rotatedBitmap != bitmap) {
        bitmap.recycle()
    }
    return rotatedBitmap
}
//TODO Get Bitmap From Content Uri Without Exif

fun Context.getBitmapFromContentUriWithoutExif(
    imageUri: Uri,
): Bitmap {
    val contentResolver = this.contentResolver
    return /*if (isPiePlus()) {
        ImageDecoder.decodeBitmap(
            ImageDecoder.createSource(
                contentResolver, imageUri
            )
        )
    } else*/ MediaStore.Images.Media.getBitmap(
        contentResolver, imageUri
    )
}


fun duplicateBitmap(bmpSrc: Bitmap?, width: Int, height: Int): Bitmap? {
    if (null == bmpSrc) {
        return null
    }

    val bmpSrcWidth = bmpSrc.width
    val bmpSrcHeight = bmpSrc.height

    val bmpDest = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    if (null != bmpDest) {
        val canvas = Canvas(bmpDest)
        val viewRect = Rect()
        val rect = Rect(0, 0, bmpSrcWidth, bmpSrcHeight)
        if (bmpSrcWidth <= width && bmpSrcHeight <= height) {
            viewRect.set(rect)
        } else if (bmpSrcHeight > height && bmpSrcWidth <= width) {
            viewRect[0, 0, bmpSrcWidth] = height
        } else if (bmpSrcHeight <= height && bmpSrcWidth > width) {
            viewRect[0, 0, width] = bmpSrcWidth
        } else if (bmpSrcHeight > height && bmpSrcWidth > width) {
            viewRect[0, 0, width] = height
        }
        canvas.drawBitmap(bmpSrc, rect, viewRect, null)
    }

    return bmpDest
}

fun duplicateBitmap(bmpSrc: Bitmap?): Bitmap? {
    if (null == bmpSrc) {
        return null
    }

    val bmpSrcWidth = bmpSrc.width
    val bmpSrcHeight = bmpSrc.height

    val bmpDest = Bitmap.createBitmap(
        bmpSrcWidth, bmpSrcHeight,
        Bitmap.Config.ARGB_8888
    )
    if (null != bmpDest) {
        val canvas = Canvas(bmpDest)
        val rect = Rect(0, 0, bmpSrcWidth, bmpSrcHeight)

        canvas.drawBitmap(bmpSrc, rect, rect, null)
    }

    return bmpDest
}


fun bitampToByteArray(bitmap: Bitmap?): ByteArray? {
    var array: ByteArray? = null
    try {
        if (null != bitmap) {
            val os = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, os)
            array = os.toByteArray()
            os.close()
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }

    return array
}



fun Bitmap.scaleDownBitmap(maxWidth: Int=800, maxHeight: Int=600): Bitmap {

    // Calculate the aspect ratio
    val aspectRatio = width.toFloat() / height.toFloat()

    // Determine new dimensions while keeping the aspect ratio
    var newWidth = maxWidth
    var newHeight = Math.round(newWidth / aspectRatio)

    // Ensure the new height does not exceed the maxHeight
    if (newHeight > maxHeight) {
        newHeight = maxHeight
        newWidth = Math.round(newHeight * aspectRatio)
    }

    // Scale down the bitmap to the new width and height
    val scaledBitmap = Bitmap.createScaledBitmap(this, newWidth, newHeight, true)

    return scaledBitmap
}


fun fileToBitmap(file: File): Bitmap? {
    return if (file.exists()) {
        BitmapFactory.decodeFile(file.absolutePath)
    } else {
        null
    }
}