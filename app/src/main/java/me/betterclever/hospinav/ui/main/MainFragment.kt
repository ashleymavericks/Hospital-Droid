package me.betterclever.hospinav.ui.main

import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.info_layout.*
import kotlinx.android.synthetic.main.main_fragment.*
import me.betterclever.hospinav.R
import me.betterclever.hospinav.ui.views.Line
import me.betterclever.hospinav.utils.Department

class MainFragment : Fragment(), SensorEventListener{

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel
    private lateinit var sensorManager: SensorManager
    private var currentKnownDept: Department? = null
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private var destinationDept: Department? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sensorManager = requireContext().getSystemService(SENSOR_SERVICE) as SensorManager
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onResume() {
        super.onResume()

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        nav_btn.setOnClickListener{
            // TODO: Make this happen. . . sometime9`
        }
        showInfoForDepartment(Department.Entrance)
        floorMapView.setOnTouchListener { v, event ->
            when(event.action) {
                MotionEvent.ACTION_DOWN -> {
                    when(bottomSheetBehavior.state) {
                        BottomSheetBehavior.STATE_DRAGGING -> return@setOnTouchListener false
                        BottomSheetBehavior.STATE_EXPANDED -> return@setOnTouchListener false
                    }
                    val department = markerView.getDepartmentForCoordinates(event.x, event.y)
                    showInfoForDepartment(department)
                    showNavigationInformation(department)
                }
            }
            true
        }

        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
    }



    private fun showNavigationInformation(newDepartment: Department?) {
        if (currentKnownDept != null) {
            if (currentKnownDept == Department.Entrance && (newDepartment== Department.BloodBank || newDepartment == Department.XRayRoom)) {
                Log.d(tag, "Path Updated")
                markerView.updateActivePath(listOf(
                        Line(290f, 550f, 290f, 470f),
                        Line(290f, 470f, 200f, 470f)
                ))
                destinationDept = newDepartment
            }
            else if(currentKnownDept == Department.XRayRoom && newDepartment == Department.MRILab)
            {
                markerView.updateActivePath(listOf(Line(200f, 270f, 200f, 180f),
                        Line(200f, 180f, 230f, 180f)))
                destinationDept = newDepartment
            }

        }
        else
        {
            Toast.makeText(activity, "Ensure you're inside the hospital", Toast.LENGTH_LONG).show()
        }
    }


    private fun showInfoForDepartment(department: Department?) {
        department?.apply {
            val info = departmentInfo
            departmentName.text = info.name
            val openingHour = info.openingTime/100
            val openingMinute = info.openingTime % 100
            val closingHour = info.closingTime/100
            val closingMinute = info.closingTime % 100
            timeView.text = "$openingHour:$openingMinute ${if(openingHour < 12) "am" else "pm"} - " +
                    "$closingHour:$closingMinute ${if(closingHour < 12) "am" else "pm"}"
            descView.text = info.description;


        }
    }

    fun updateLocation(region: Department?, distance: Float) {
        val (x, y) = when(region) {
            Department.Entrance -> Pair(320, 550)
            Department.XRayRoom -> Pair(120, 240)
            Department.PathologyDepartment -> Pair(300, 240)
            Department.MRILab -> Pair(300, 140)
            Department.ICU -> Pair(120, 140)
            Department.BloodBank -> Pair(100, 500)
            else -> return
        }

        currentKnownDept = region

        markerView.updateLocation(x, y)
//        markerView.updateActivePath(listOf(
//                Line(290f, 550f, 290f, 470f),
//                Line(290f, 470f, 200f, 470f),
//                Line(200f, 470f, 200f, 270f),
//                Line(200f, 270f, 200f, 180f),
//                Line(200f, 180f, 230f, 180f)
//        ))

        if(destinationDept != null)
        {
            if(destinationDept?.departmentInfo?.name == "Blood Bank" && currentKnownDept?.departmentInfo?.name == "Blood Bank")
            {
                destinationReached();

            }

            if(destinationDept == Department.XRayRoom && currentKnownDept == Department.XRayRoom)
            {
                destinationReached();

            }

            if(destinationDept == Department.XRayRoom && currentKnownDept == Department.BloodBank)
            {
                markerView.updateActivePath(listOf(Line(200f, 470f, 200f, 270f)))
            }

            if(destinationDept == Department.MRILab && currentKnownDept == Department.MRILab)
            {
                destinationReached();
            }

        }
        markerView.updateRange(distance * 25)
    }

    override fun onSensorChanged(event: SensorEvent) {
        // Log.d("Sensor Event", event.values!!.contentToString())
        val angle = event.values[0]
        markerView.updateAngle(angle)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    private fun destinationReached()
    {
        Toast.makeText(activity, "Destination Reached", Toast.LENGTH_LONG).show();
        markerView.updateActivePath(listOf())
    }
}
