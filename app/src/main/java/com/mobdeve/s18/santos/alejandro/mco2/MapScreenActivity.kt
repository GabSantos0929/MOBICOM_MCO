package com.mobdeve.s18.santos.alejandro.mco2

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import android.app.AlertDialog
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.color.MaterialColors

class MapScreenActivity : BaseActivity(), OnMapReadyCallback {
    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }
    private lateinit var mMap: GoogleMap
    private lateinit var buildingDb: BuildingDbHelper
    private lateinit var classDb: ClassDbHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.map_screen)
        setupBottomNav(R.id.navMap)

        buildingDb = BuildingDbHelper(applicationContext)
        classDb = ClassDbHelper(applicationContext)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val prefs = getSharedPreferences("settings_prefs", MODE_PRIVATE)
        val showPins = prefs.getBoolean("show_pins", true)

        if (checkLocationPermission()) {
            enableMyLocation()
        }
        displayBuildingsOnMap(showPins)
        mMap.uiSettings.isMyLocationButtonEnabled = true
        mMap.uiSettings.isZoomControlsEnabled = true

        if (showPins) {
            mMap.setOnMapClickListener { latLng ->
                showAddBuildingDialog(latLng)
            }
        } else {
            mMap.setOnMapClickListener(null)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE &&
            grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            enableMyLocation()
        }
    }

    private fun displayBuildingsOnMap(showPins: Boolean) {
        // Center map on campus
        val campusCenter = LatLng(14.5646, 120.9933)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(campusCenter, 17f))

        if (!showPins) return

        val buildings = buildingDb.getAllBuildings()

        for (b in buildings) {
            val pos = LatLng(b.lat, b.lng)

            mMap.addMarker(
                MarkerOptions()
                    .position(pos)
                    .title(b.name)
            )
        }

        // Optional: handle marker clicks
        mMap.setOnMarkerClickListener { marker ->
            val buildingName = marker.title
            showClassesForBuilding(buildingName)
            true
        }
    }

    private fun showClassesForBuilding(buildingName: String?) {
        val classes = classDb.getClassesForToday()
            .filter { it.building == buildingName }

        val bottomSheetView = layoutInflater.inflate(R.layout.map_classes, null)
        val tvBuildingName = bottomSheetView.findViewById<TextView>(R.id.buildingName)
        val rvClasses = bottomSheetView.findViewById<RecyclerView>(R.id.classes)
        val tvNoClasses = bottomSheetView.findViewById<TextView>(R.id.noClasses)

        tvBuildingName.text = buildingName

        if (classes.isEmpty()) {
            rvClasses.visibility = View.GONE
            tvNoClasses.visibility = View.VISIBLE
        } else {
            rvClasses.visibility = View.VISIBLE
            tvNoClasses.visibility = View.GONE
            rvClasses.layoutManager = LinearLayoutManager(this)
            rvClasses.adapter = MapClassAdapter(classes)
        }

        val bottomSheetDialog = com.google.android.material.bottomsheet.BottomSheetDialog(this)
        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()
    }

    private fun showAddBuildingDialog(latLng: LatLng) {
        val dialogView = layoutInflater.inflate(R.layout.add_building, null)
        val tilBuildingName = dialogView.findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.tilBuildingName)
        val etBuildingName = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etBuildingName)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("Add", null) // We'll override the click later
            .setNegativeButton("Cancel", null)
            .create()

        dialog.setOnShowListener {
            val addButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            val cancelButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            val colorOnSurface = MaterialColors.getColor(dialogView, com.google.android.material.R.attr.colorOnSurface)

            addButton.setTextColor(colorOnSurface)
            cancelButton.setTextColor(colorOnSurface)

            addButton.setOnClickListener {
                val buildingName = etBuildingName.text.toString().trim()
                if (buildingName.isEmpty()) {
                    tilBuildingName.error = "Building name cannot be empty"
                } else {
                    tilBuildingName.error = null
                    insertBuilding(buildingName, latLng)
                    dialog.dismiss()
                }
            }
        }

        dialog.show()
    }

    private fun insertBuilding(name: String, latLng: LatLng) {
        buildingDb.insertBuilding(name, latLng.latitude, latLng.longitude)

        mMap.addMarker(
            MarkerOptions()
                .position(latLng)
                .title(name)
        )
        Toast.makeText(this, "Building added!", Toast.LENGTH_SHORT).show()
    }

    private fun checkLocationPermission(): Boolean {
        return if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            false
        } else true
    }

    private fun enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled = true
        }
    }
}
