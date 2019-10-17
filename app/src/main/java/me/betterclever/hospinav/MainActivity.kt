package me.betterclever.hospinav

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import me.betterclever.hospinav.ui.main.MainFragment
import me.betterclever.hospinav.utils.Department
import org.altbeacon.beacon.*

class MainActivity : AppCompatActivity(), BeaconConsumer, RangeNotifier {
    private lateinit var beaconManager: BeaconManager
    private lateinit var mainFragment: MainFragment

    private val distance = mutableMapOf<Department, Float>(
            Department.Entrance to 1000f,
            Department.XRayRoom to 1000f,
            Department.PathologyDepartment to 1000f,
            Department.MRILab to 1000f,
            Department.ICU to 1000f,
            Department.BloodBank  to 1000f
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        beaconManager = BeaconManager.getInstanceForApplication(this)
        beaconManager.beaconParsers.add(BeaconParser().
                setBeaconLayout("s:0-1=feaa,m:2-2=00,p:3-3:-41,i:4-13,i:14-19"))

        beaconManager.foregroundBetweenScanPeriod = 1000
        beaconManager.foregroundScanPeriod = 1000
        beaconManager.bind(this)
        mainFragment = MainFragment.newInstance()

        if (savedInstanceState == null) {

            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, mainFragment)
                    .commitNow()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        beaconManager.unbind(this)
    }

    override fun onPause() {
        super.onPause()
        if (beaconManager.isBound(this)) beaconManager.backgroundMode = true
    }

    override fun onResume() {
        super.onResume()
        if (beaconManager.isBound(this)) beaconManager.backgroundMode = false
    }

    override fun onBeaconServiceConnect() {
        beaconManager.addRangeNotifier(this)
        val namespaceId = Identifier.parse("0x07090808030406050508")
        val regions = listOf(
                Region(Department.Entrance.name, namespaceId, Identifier.parse("0x000000000001"), null),
                Region(Department.XRayRoom.name, namespaceId, Identifier.parse("0x000000000002"), null),
                Region(Department.PathologyDepartment.name, namespaceId, Identifier.parse("0x000000000003"), null),
                Region(Department.MRILab.name, namespaceId, Identifier.parse("0x000000000004"), null),
                Region(Department.ICU.name, namespaceId, Identifier.parse("0x000000000005"), null),
                Region(Department.BloodBank.name, namespaceId, Identifier.parse("0x000000000006"), null)
        )

        regions.forEach { beaconManager.startRangingBeaconsInRegion(it) }
    }

    override fun didRangeBeaconsInRegion(beacons: MutableCollection<Beacon>, region: Region) {
        val beacon = beacons.minBy { it.distance }
        if (beacon == null) {
            distance[Department.valueOf(region.uniqueId)] = 1000f
        }

        beacon?.let { distance[Department.valueOf(region.uniqueId)] = beacon.distance.toFloat() }

        val d = distance.filter { it.value < 3f }.minBy { it.value }
        if (d == null) {
            mainFragment.updateLocation(null, 0f)
        } else {
            Log.d("Beacon", "Region: ${d.key} Distance: ${d.value}")
            mainFragment.updateLocation(region = d.key, distance = d.value)
            //Toast.makeText(this, "Region: ${d.key} Distance: ${d.value}", Toast.LENGTH_SHORT).show()
        }
    }

}
