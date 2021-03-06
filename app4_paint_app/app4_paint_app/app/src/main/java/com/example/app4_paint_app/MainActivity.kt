package com.example.app4_paint_app

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.view.*
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //supportActionBar?.hide()

        val surfaceView = CustomSurfaceView(applicationContext, null)
        val drawCanvas = findViewById<LinearLayout>(R.id.CustomSurfaceView)
        drawCanvas.addView(surfaceView)

        val button_RED = findViewById<Button>(R.id.button)
        button_RED.setOnClickListener {
            surfaceView.changePaintColor(Color.RED)
        }

        val button_YEL = findViewById<Button>(R.id.button2)
        button_YEL.setOnClickListener {
            surfaceView.changePaintColor(Color.YELLOW)
        }

        val button_BLU = findViewById<Button>(R.id.button3)
        button_BLU.setOnClickListener {
            surfaceView.changePaintColor(Color.BLUE)
        }

        val button_GRE = findViewById<Button>(R.id.button4)
        button_GRE.setOnClickListener {
            surfaceView.changePaintColor(Color.GREEN)
        }

        val button_CLEAR = findViewById<Button>(R.id.imageButton)
        button_CLEAR.setOnClickListener {
            surfaceView.clearCanvas()
        }


    }

    private fun getBitmapFromView(view: View): Bitmap {
        val returnedBitmap =
            Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(returnedBitmap)
        val bgDrawable = view.background
        if (bgDrawable != null) {
            bgDrawable.draw(canvas)
        } else {
            canvas.drawColor(Color.WHITE)
        }
        view.draw(canvas)

        return returnedBitmap
    }

    class CustomSurfaceView(context: Context, attrs: AttributeSet?) : SurfaceView(context, attrs),
        SurfaceHolder.Callback {

        private var path = Path()

        private var mPojemnik: SurfaceHolder = holder

        private var mBlokada = Object()

        lateinit var mBitmapa: Bitmap
        private lateinit var mKanwa: Canvas

        private var drawColor = Color.RED
        private val mPaintS = Paint().apply {
            color = drawColor
            style = Paint.Style.STROKE
            strokeWidth = 4F
        }
        private val mPaintF = Paint().apply {
            color = drawColor
            style = Paint.Style.FILL
            strokeWidth = 2F
        }

        fun changePaintColor(color: Int) {
            drawColor = color
            mPaintS.color = drawColor
            mPaintF.color = drawColor
        }

        private val touchTolerance = ViewConfiguration.get(context).scaledTouchSlop

        private var currentX = 0f
        private var currentY = 0f

        private var motionTouchEventX = 0f
        private var motionTouchEventY = 0f

        private var screenHeight = 0
        private var screenWidth = 0

        init {
            isFocusable = true
            mPojemnik.addCallback(this)
            setZOrderOnTop(true)
            this.setBackgroundColor(Color.RED)
        }

        override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
            super.onSizeChanged(width, height, oldWidth, oldHeight)

            if (::mBitmapa.isInitialized) mBitmapa.recycle()
            mBitmapa = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            mKanwa = Canvas(mBitmapa)
            mKanwa.drawColor(Color.WHITE)
        }

        override fun onDraw(canvas: Canvas) {
            canvas.drawBitmap(mBitmapa, 0f, 0f, null)
        }

        override fun surfaceCreated(holder: SurfaceHolder) {
            mBitmapa = Bitmap.createBitmap(
                holder.surfaceFrame.width(),
                holder.surfaceFrame.height(),
                Bitmap.Config.ARGB_8888
            )
            mKanwa = Canvas(mBitmapa)
            mKanwa.drawColor(Color.WHITE)

            screenHeight = height
            screenWidth = width
        }

        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

        override fun surfaceDestroyed(holder: SurfaceHolder) {}

        fun clearCanvas() {
            mKanwa.drawColor(Color.WHITE)
        }

        @SuppressLint("ClickableViewAccessibility")
        override fun onTouchEvent(event: MotionEvent?): Boolean {
            performClick()

            synchronized(mBlokada) {
                if (event != null) {
                    motionTouchEventX = event.x
                    motionTouchEventY = event.y

                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> touchStart()
                        MotionEvent.ACTION_MOVE -> touchMove()
                        MotionEvent.ACTION_UP -> touchUp()
                    }
                }
            }
            return true
        }

        private fun touchStart() {
            path.reset()
            path.moveTo(motionTouchEventX, motionTouchEventY)
            currentX = motionTouchEventX
            currentY = motionTouchEventY

            mKanwa.drawCircle(currentX, currentY, 10F, mPaintF)
        }

        private fun touchMove() {
            val dx = Math.abs(motionTouchEventX - currentX)
            val dy = Math.abs(motionTouchEventY - currentY)
            if (dx >= touchTolerance || dy >= touchTolerance) {
                path.quadTo(
                    currentX,
                    currentY,
                    (motionTouchEventX + currentX) / 2,
                    (motionTouchEventY + currentY) / 2
                )
                currentX = motionTouchEventX
                currentY = motionTouchEventY

                mKanwa.drawPath(path, mPaintS)
            }
            invalidate()
        }

        private fun touchUp() {
            mKanwa.drawCircle(currentX, currentY, 10F, mPaintF)
            path.reset()
        }

    }
}
