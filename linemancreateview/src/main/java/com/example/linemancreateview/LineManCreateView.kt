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
val handFactor : Float = 5.7f
val legFactor : Float = 3.3f

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
        drawLine(
            0f,
            -size,
            0f,
            -size + (2 * size - legSize) * sf1,
            paint
        )
        drawAt(0f, -size - r) {
            paint.style = Paint.Style.STROKE
            drawArc(RectF(-r, -r, r, r), 0f, 360f * sf2, false, paint)
        }
        drawAt(0f, -size / 2) {
            for (j in 0..1) {
                save()
                rotate(45f * (1f - 2 * j) * sf3)
                drawLine(
                    0f,
                    0f,
                    0f,
                    handSize * Math.floor(sf1.toDouble()).toFloat(),
                    paint
                )
                restore()
            }
        }
        drawAt(0f, size - legSize) {
            for (j in 0..1) {
                save()
                rotate(45f * (1f - 2 * j) * sf3)
                drawLine(
                    0f,
                    0f,
                    0f,
                    legSize * sf2,
                    paint)
                restore()
            }
        }
    }
}
fun Canvas.drawLMCNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = colors[i]
    paint.strokeCap = Paint.Cap.ROUND
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    drawLineManCreate(scale, w, h, paint)
}

class LineManCreateView(ctx : Context) : View(ctx) {

    private val renderer : Renderer = Renderer(this)

    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var dir : Float = 0f, var prevScale : Float = 0f) {

        fun update(cb : (Float) -> Unit) {
            scale += scGap * dir
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1f - 2 * prevScale
                cb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(delay)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class LMCNode(var i : Int, val state : State = State()) {

        private var next : LMCNode? = null
        private var prev : LMCNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < colors.size - 1) {
                next = LMCNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawLMCNode(i, state.scale, paint)
        }

        fun update(cb : (Float) -> Unit) {
            state.update(cb)
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : LMCNode {
            var curr : LMCNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class LineManCreate(var i : Int) {

        private var curr : LMCNode = LMCNode(0)
        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            curr.draw(canvas, paint)
        }

        fun update(cb : (Float) -> Unit) {
            curr.update {
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                cb(it)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            curr.startUpdating(cb)
        }
    }

    data class Renderer(var view : LineManCreateView) {

        private val animator : Animator = Animator(view)
        private val lmc : LineManCreate = LineManCreate(0)
        private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

        fun render(canvas : Canvas) {
            canvas.drawColor(backColor)
            lmc.draw(canvas, paint)
            animator.animate {
                lmc.update {
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            lmc.startUpdating {
                animator.start()
            }
        }
    }

    companion object {

        fun create(activity : Activity) : LineManCreateView {
            val view : LineManCreateView = LineManCreateView(activity)
            activity.setContentView(view)
            return view
        }
    }
}

