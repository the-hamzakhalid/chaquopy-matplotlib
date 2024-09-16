package com.chaquo.myapplication.utils.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class DrawView(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    private var paint: Paint = Paint()
    private var path: Path = Path()
    private var canvasBitmap: Bitmap? = null
    private var drawCanvas: Canvas? = null
    private var backgroundBitmap: Bitmap? = null
    private var lastX = 0f
    private var lastY = 0f

    init {
        // Initialize paint settings
        paint.isAntiAlias = true
        paint.color = Color.BLACK // Black color
        paint.style = Paint.Style.STROKE
        paint.strokeJoin = Paint.Join.ROUND
        paint.strokeCap = Paint.Cap.ROUND
        paint.strokeWidth = 10f // Pen thickness
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        drawCanvas = Canvas(canvasBitmap!!)
        if (backgroundBitmap != null) {
            drawCanvas?.drawBitmap(backgroundBitmap!!, 0f, 0f, null)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (backgroundBitmap != null) {
            canvas.drawBitmap(backgroundBitmap!!, 0f, 0f, null)
        }
        canvas.drawBitmap(canvasBitmap!!, 0f, 0f, paint)
        canvas.drawPath(path, paint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val touchX = event.x
        val touchY = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                path.moveTo(touchX, touchY)
                lastX = touchX
                lastY = touchY
            }

            MotionEvent.ACTION_MOVE -> {
                path.quadTo(lastX, lastY, touchX, touchY)
                lastX = touchX
                lastY = touchY
            }

            MotionEvent.ACTION_UP -> {
                path.lineTo(touchX, touchY)
                drawCanvas?.drawPath(path, paint)
                path.reset()
            }

            else -> return false
        }
        invalidate()
        return true
    }

    fun setImageBitmap(bitmap: Bitmap) {
        // Scale the bitmap to fit the view dimensions
        backgroundBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true)
        invalidate()
    }

    fun getMaskFromDrawing(): Bitmap {
        // Ensure that the background bitmap is a software bitmap
        val safeBackgroundBitmap = ensureSoftwareBitmap(backgroundBitmap!!)

        // Create a bitmap to hold the mask
        val maskBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val maskCanvas = Canvas(maskBitmap)

        // Set up paint for the mask: only drawing the user's selection
        val maskPaint = Paint().apply {
            color = Color.BLACK // Mask color
            style = Paint.Style.FILL_AND_STROKE
            isAntiAlias = true
        }

        // Draw the user's path on the mask canvas
        maskCanvas.drawPath(path, maskPaint)

        // Check if the maskBitmap has content
        // Debugging: Uncomment the following code to save the maskBitmap and view it
        // val maskFile = File(context.cacheDir, "maskBitmap.png")
        // FileOutputStream(maskFile).use {
        //     maskBitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
        // }

        // Apply the mask to the original background bitmap
        val resultBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val resultCanvas = Canvas(resultBitmap)

        // Create a paint object to combine the image with the mask
        val paint = Paint().apply {
            isAntiAlias = true
            xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        }

        // Draw the original background image (ensuring it's a software bitmap)
        resultCanvas.drawBitmap(safeBackgroundBitmap, 0f, 0f, null)

        // Apply the mask by drawing the masked area of the original bitmap
        resultCanvas.drawBitmap(maskBitmap, 0f, 0f, paint)

        return resultBitmap
    }

    // Utility function to ensure bitmap is in software format
    private fun ensureSoftwareBitmap(bitmap: Bitmap): Bitmap {
        // Check if the bitmap is already mutable (software-rendered)
        if (bitmap.config != Bitmap.Config.HARDWARE) {
            return bitmap // It's already software-rendered
        }

        // Convert hardware bitmap to a software-rendered bitmap
        val softwareBitmap =
            Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(softwareBitmap)
        canvas.drawBitmap(bitmap, 0f, 0f, null)
        return softwareBitmap
    }

    fun clearCanvas() {
        drawCanvas?.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
        invalidate()
    }
}
