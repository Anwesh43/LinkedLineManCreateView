package com.example.linemancreateview

import android.view.View
import android.view.MotionEvent
import android.app.Activity
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Color
import android.graphics.RectF
import android.content.Context

val colors : Array<Int> = arrayOf(
    "#F44336",
    "#2196F3",
    "#8BC34A",
    "#009688",
    "#673AB7"
).map {
    Color.parseColor(it)
}.toTypedArray()
val backColor : Int = Color.parseColor("#BDBDBD")
val strokeFactor : Float = 90f
val sizeFactor : Float = 2.9f
val parts : Int = 3
val scGap : Float = 0.02f / parts
val delay : Long = 20
val rFactor : Float = 12.9f
val handFactor : Float = 10.8f
val legFactor : Float = 8.3f

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n
fun Float.sinify() : Float = Math.sin(this * Math.PI).toFloat()

fun Canvas.drawAt(x : Float, y : Float, cb : () -> Unit) {
    save()
    translate(x, y)
    cb()
    restore()
}

fun Canvas.drawLineManCreate(scale : Float, w : Float, h : Float, paint : Paint) {
    val size: Float = Math.min(w, h) / sizeFactor
    val handSize: Float = Math.min(w, h) / handFactor
    val legSize: Float = Math.min(w, h) / legFactor
    val r: Float = Math.min(w, h) / rFactor
    val sf: Float = scale.sinify()
    val sf1: Float = sf.divideScale(0, parts)
    val sf2: Float = sf.divideScale(1, parts)
    val sf3: Float = sf.divideScale(2, parts)

    drawAt(w / 2, h / 2) {
        drawLine(0f, -size * sf1, 0f, size * sf1, paint)
        drawAt(0f, -size - r) {
            paint.style = Paint.Style.STROKE
            drawArc(RectF(-r, r, r, r), 0f, 360f * sf2, false, paint)
        }
        drawAt(0f, -size / 2) {
            for (j in 0..1) {
                save()
                rotate(45f * (1f - 2 * j) * sf3)
                drawLine(0f, 0f, 0f, handSize * sf1, paint)
                restore()
            }
        }
        drawAt(0f, size - legSize) {
            for (j in 0..1) {
                save()
                rotate(45f * (1f - 2 * j) * sf3)
                drawLine(0f, 0f, 0f, legSize * sf1, paint)
                restore()
            }
        }
    }

    fun Canvas.drawLMCNode(i : Int, scale : Float, paint : Paint) {
        val w : Float = width.toFloat()
        val h : Float = height.toFloat()
        paint.color = colors[i]
        paint.strokeCap = Paint.Cap.ROUND
        paint.strokeWidth = Math.min(w, h) / strokeFactor
    }
}

class LineManCreateView(ctx : Context) : View(ctx) {

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }
}

