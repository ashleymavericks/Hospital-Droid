package me.betterclever.hospinav.ui.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import me.betterclever.hospinav.utils.Department
import kotlin.math.atan
import kotlin.math.atan2

data class Line(
        val sX: Float,
        val sY: Float,
        val eX: Float,
        val eY: Float
)

class MarkerView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint()
    private var path = Path()

    private var angle = 180f
    private var range = 80f
    private var px = -100f
    private var py = -100f

    private var activePath = listOf<Pair<Line, Path>>()

    init {

        // Entrance
        // updateLocation(320, 550)

        // Storage
        // updateLocation(300, 610)

        // Reception
        // updateLocation(300, 380)

        // Pathology Department
        // updateLocation(300, 240)

        // MRI Lab
        // updateLocation(300, 140)

        // Washroom
        // updateLocation(200, 80)

        // ICU
        // updateLocation(120, 140)

        // X-Ray Room
        // updateLocation(120, 240)

        // Orthopaedic Department
        // updateLocation(120, 340)

        // Blood Bank
        // updateLocation(100, 500)

        // Bath
        // updateLocation(100, 610)

        // Lobby 1
        // updateLocation(200, 450)

        // Lobby 2
        // updateLocation(200, 270)

        // Lobby 3
        //< updateLocation(200, 150)

    }

    fun updateLocation(x: Int, y: Int) {
        path = Path()

        px = resources.displayMetrics.density * x.toFloat()
        py = resources.displayMetrics.density * y.toFloat()

        path.moveTo(px-15, py-15)
        path.lineTo(px+15, py-15)
        path.lineTo(px, py+30f)
        path.lineTo(px-15, py-15)
        path.close()

        invalidate()
    }

    fun updateRange(range: Float) {
        this.range = resources.displayMetrics.density * range
        invalidate()
    }

    fun updateAngle(angle: Float) {
        this.angle = (angle + 180) % 360
        invalidate()
    }

    fun updateActivePath(lines: List<Line>) {
        val ds = resources.displayMetrics.density
        activePath = lines.map {
            Line(it.sX*ds, it.sY*ds, it.eX*ds, it.eY*ds)
        }.map {
            val path = Path()

            val px = resources.displayMetrics.density * it.eX
            val py = resources.displayMetrics.density * it.eY

            path.moveTo(px-15, py-15)
            path.lineTo(px+15, py-15)
            path.lineTo(px, py+30f)
            path.lineTo(px-15, py-15)
            path.close()

            Pair(it,path)
        }
        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.apply {
            paint.color = Color.RED
            paint.style = Paint.Style.FILL

            save()
            rotate(angle, px, py)
            drawPath(path, paint)
            restore()

            paint.color = Color.parseColor("#44FF4455")
            drawCircle(px, py, range, paint)

            // Draw path
            paint.color = Color.BLUE
            activePath.forEach {

                paint.style = Paint.Style.STROKE
                paint.strokeWidth = 10f

                val lineAngle = it.first.let {
                    drawLine(it.sX, it.sY, it.eX, it.eY, paint)
                    atan2(it.eY-it.sY, it.eX-it.sX) * 57.2958f + 180f
                }

                paint.style = Paint.Style.FILL

                drawCircle(it.first.eX, it.first.eY, 10f, paint)
                save()
                rotate(lineAngle, it.first.eX, it.first.eY)
                drawPath(it.second, paint)
                restore()
            }
        }
    }

    fun getDepartmentForCoordinates(x: Float, y: Float) : Department? {
        val qx = x / resources.displayMetrics.density
        val qy = y / resources.displayMetrics.density

        return when(qx) {
            in 60..170 -> {
                when(qy) {
                    in 30..150 -> Department.ICU
                    in 150..290 -> Department.XRayRoom
                    in 290..380 -> Department.OrthopaedicDepartment
                    in 380..530 -> Department.BloodBank
                    in 530..600 -> Department.Bath
                    else -> null
                }
            }
            in 170..230 -> {
                when(qy) {
                    in 40..180 -> Department.Washroom
                    else -> null
                }
            }
            in 230..330 -> {
                when(qy) {
                    in 40..190 -> Department.MRILab
                    in 190..330 -> Department.PathologyDepartment
                    in 330..410 -> Department.Reception
                    in 480..600 -> Department.Entrance
                    in 600..650 -> Department.Storage
                    else -> null
                }
            }
            else -> null
        }
    }
}