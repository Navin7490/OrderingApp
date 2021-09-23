package com.yorder.shop.utils

import com.google.android.material.shape.EdgeTreatment
import com.google.android.material.shape.ShapePath
import kotlin.math.asin
import kotlin.math.pow
import kotlin.math.sqrt

class CustomEdge(private val circleRadius: Int,
                 private val circleCenterOffset: Int) : EdgeTreatment(), Cloneable {

    init {
        // do not allow to offset circle center up
        if (circleCenterOffset < 0)
            throw IllegalArgumentException()
    }

    override fun getEdgePath(length: Float, center: Float, interpolation: Float, shapePath: ShapePath) {

        // use interpolated radius
        val radius = circleRadius * interpolation

        // if circle lays entirely inside the rectangle then just draw a line
        val circleTop = circleCenterOffset - radius
        if (circleTop >= 0) {
            shapePath.lineTo(length, 0f)
            return
        }

        // calc the distance from the center of the edge to the point where arc begins
        // ignore the case when the radius is so big that the circle fully covers the edge
        // just draw a line for now, but maybe it can be replaced by drawing the arc
        val c = sqrt(radius.pow(2) - circleCenterOffset.toDouble().pow(2))
        if (c > center) {
            shapePath.lineTo(length, 0f)
            return
        }

        // draw a line from the left corner to the start of the arc
        val arcStart = center - c
        shapePath.lineTo(arcStart.toFloat(), 0f)

        // calc the start angle and the sweep angle of the arc and draw the arc
        // angles are measured clockwise with 0 degrees at 3 o'clock
        val alpha = Math.toDegrees(asin(circleCenterOffset / radius).toDouble())
        val startAngle = 180 + alpha
        val sweepAngle = 180 - 2 * alpha
        shapePath.addArc(
            center - radius,
            circleCenterOffset - radius,
            center + radius,
            circleCenterOffset + radius,
            startAngle.toFloat(),
            sweepAngle.toFloat())

        // draw the line from the end of the arc to the right corner
        shapePath.lineTo(length, 0f)
    }
}