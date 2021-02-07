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
