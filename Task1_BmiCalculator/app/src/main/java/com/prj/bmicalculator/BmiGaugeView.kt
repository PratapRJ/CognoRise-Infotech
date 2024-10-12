package com.prj.bmicalculator

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class BmiGaugeView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        textSize = 40f
        textAlign = Paint.Align.CENTER
    }
    private val needlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        strokeWidth = 8f
        style = Paint.Style.STROKE
    }

    private var currentBmiValue: Float = 0f
    private var animatedBmiValue: Float = 0f // For animating the needle
    private val animationDuration = 1000L // 1 second for smooth animation

    // Set BMI value and start the animation
    fun setBMIValue(bmi: Double) {
        // Clamp the BMI value to a maximum of 38
        val targetBmi = bmi.toFloat().coerceIn(0f, 36f)
        animateNeedleTo(targetBmi)
    }

    // Animate the needle to the target BMI value
    private fun animateNeedleTo(targetBmi: Float) {
        val animator = ValueAnimator.ofFloat(currentBmiValue, targetBmi)
        animator.duration = animationDuration
        animator.addUpdateListener { animation ->
            animatedBmiValue = animation.animatedValue as Float
            invalidate() // Redraw the view with the updated needle position
        }
        animator.start()
        currentBmiValue = targetBmi // Update the current BMI value
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width.toFloat()
        val height = height.toFloat()
        val radius = min(width, height) / 2.5f
        val centerX = width / 2
        val centerY = height / 1.2f

        // Draw the BMI meter segments (colored arcs)
        drawMeterSegments(canvas, centerX, centerY, radius)

        // Draw the range labels on the meter
        drawBmiRanges(canvas, centerX, centerY, radius)

        // Draw the needle based on the animated BMI value
        drawNeedle(canvas, centerX, centerY, radius)


    }

    private fun drawBmiRanges(canvas: Canvas, cx: Float, cy: Float, radius: Float) {
        val bmiLabels = arrayOf("0", "18.5", "25", "30", "30<")

        // These angles correspond to the start and end of each color range:
        val bmiAngles = arrayOf(165f, 200f, 270f, 340f, 15f) // Adjusted to match the exact boundaries

        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            textSize = 40f
            textAlign = Paint.Align.CENTER
        }

        for (i in bmiLabels.indices) {
            // Convert angle to radians for calculation
            val angle = Math.toRadians(bmiAngles[i].toDouble())

            // Position the labels slightly outside the meter arc
            val labelRadius = radius + 100f // Adjust for label position

            // Calculate the label's x and y coordinates based on the angle
            val x = cx + labelRadius * cos(angle).toFloat()
            val y = cy + labelRadius * sin(angle).toFloat()

            // Draw the BMI label at the calculated position
            canvas.drawText(bmiLabels[i], x, y, textPaint)
        }
    }

    private fun drawMeterSegments(canvas: Canvas, cx: Float, cy: Float, radius: Float) {
        val segmentColors = arrayOf(
            ContextCompat.getColor(context, R.color.underWeightColor), // Underweight (blue)
            ContextCompat.getColor(context, R.color.normalWeightColor), // Normal (cyan)
            ContextCompat.getColor(context, R.color.overWeightColor), // Overweight (yellow)
            ContextCompat.getColor(context, R.color.obeseColor)  // Obese (red)
        )
        val bmiRanges = arrayOf(0f, 18.5f, 25f, 30f, 40f)

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 100f // Doubled stroke width (from 50f to 100f)

        val startAngle = 135f
        val sweepAngle = 270f / (bmiRanges.size - 1) // Divide 270 degrees for the segments

        for (i in 0 until bmiRanges.size - 1) {
            paint.color = segmentColors[i]
            canvas.drawArc(
                cx - radius, cy - radius, cx + radius, cy + radius,
                startAngle + (sweepAngle * i),
                sweepAngle,
                false,
                paint
            )
        }
    }

    private fun drawNeedle(canvas: Canvas, cx: Float, cy: Float, radius: Float) {
        // Calculate angle based on the animated BMI value
        val angle = getNeedleAngleForBMI(animatedBmiValue)

        // Calculate needle end point (the tip of the arrow)
        val needleLength = radius * 0.8f
        val endX = cx + needleLength * cos(Math.toRadians(angle.toDouble())).toFloat()
        val endY = cy + needleLength * sin(Math.toRadians(angle.toDouble())).toFloat()

        // Adjust the arrow width (smaller value will make the arrow thinner)
        val arrowWidth = 15f // Reduced width for the arrow
        val baseLength = needleLength * 0.09f // Adjust the base of the arrow to match the smaller width
        val baseX1 = cx + baseLength * cos(Math.toRadians(angle.toDouble() + 90)).toFloat()
        val baseY1 = cy + baseLength * sin(Math.toRadians(angle.toDouble() + 90)).toFloat()
        val baseX2 = cx + baseLength * cos(Math.toRadians(angle.toDouble() - 90)).toFloat()
        val baseY2 = cy + baseLength * sin(Math.toRadians(angle.toDouble() - 90)).toFloat()

        // Create a path to draw the arrowhead
        val needlePath = android.graphics.Path().apply {
            moveTo(endX, endY) // Tip of the arrow
            lineTo(baseX1, baseY1) // First base corner
            lineTo(baseX2, baseY2) // Second base corner
            close() // Close the path to create the triangle (arrow)
        }

        // Draw the arrow
        paint.style = Paint.Style.FILL
        paint.color = resources.getColor(R.color.red, null)
        canvas.drawPath(needlePath, paint)

        // Draw the needle's pivot point at the center
        paint.color = resources.getColor(R.color.darBlue, null)
        paint.strokeWidth = 2F
        paint.style = Paint.Style.FILL // Ensure it's filled
        paint.strokeCap = Paint.Cap.ROUND // Make it rounded

        // Draw a small circle for the pivot point
        canvas.drawCircle(cx, cy, 30f, paint) // 15f is the radius of the circle
    }




    private fun getNeedleAngleForBMI(bmi: Float): Float {
        // Define BMI ranges and corresponding angle segments
        val bmiRanges = arrayOf(0f, 18.5f, 25f, 30f, 38f) // Updated max to 38
        val angleRanges = arrayOf(135f, 200f, 270f, 340f, 405f) // Angle ranges for each BMI segment

        // Ensure BMI is within the defined ranges (clamped to 38 max)
        val clampedBmi = bmi.coerceIn(bmiRanges.first(), bmiRanges.last())

        // Find which BMI range the current BMI falls into
        for (i in 0 until bmiRanges.size - 1) {
            if (clampedBmi >= bmiRanges[i] && clampedBmi <= bmiRanges[i + 1]) {
                // Interpolate the angle within the BMI range
                val rangeFraction = (clampedBmi - bmiRanges[i]) / (bmiRanges[i + 1] - bmiRanges[i])
                return angleRanges[i] + rangeFraction * (angleRanges[i + 1] - angleRanges[i])
            }
        }

        // Default case (should never happen if BMI is clamped correctly)
        return angleRanges.last()
    }




}
