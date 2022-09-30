package com.udacity.project4.locationreminders.savereminder.selectreminderlocation


import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.databinding.FragmentSelectLocationBinding
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.utils.Constants
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import org.koin.android.ext.android.inject
import java.util.*

@Suppress("DEPRECATED_IDENTITY_EQUALS")
class SelectLocationFragment : BaseFragment(),OnMapReadyCallback {

    //Use Koin to get the view model of the SaveReminder
    override val _viewModel: SaveReminderViewModel by inject()
    private lateinit var binding: FragmentSelectLocationBinding
    private  var mMap: GoogleMap? =null

    var poiMarker:Marker?=null
    private val runningQOrLater = android.os.Build.VERSION.SDK_INT >=
            android.os.Build.VERSION_CODES.Q
    private val runneronandroid11=android.os.Build.VERSION.SDK_INT >
            android.os.Build.VERSION_CODES.Q
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_select_location, container, false)
        binding.map.onCreate(savedInstanceState)
        binding.viewModel = _viewModel
        binding.lifecycleOwner = this
        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(true)
        _viewModel.selectedPOI.observe(viewLifecycleOwner) { binding.savelocation.isEnabled = true }
//        TODO: add the map setup implementation
        binding.map.getMapAsync(this)
//        TODO: zoom to the user location after taking his permission
        if(!isPermissionGranted())
            requstpremssion()
//        TODO: add style to the map
//        TODO: put a marker to location that the user selected
//        TODO: call this function after the user confirms on the selected location
        binding.savelocation.setOnClickListener {
            onLocationSelected()
        }
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        if(isPermissionGranted()) {
            _viewModel.showsettingsnackBar.value=-1
            makehomelocation()
        }
        binding.map.onStart()
    }

    override fun onStop() {
        super.onStop()
        binding.map.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.map.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        binding.map.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.map.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.map.onLowMemory()
    }
    @TargetApi(29 )
    private fun requstpremssion() {
        var permissionsArray = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        val resultCode = when(runningQOrLater){
            true and (!runneronandroid11)->{
                permissionsArray+=Manifest.permission.ACCESS_BACKGROUND_LOCATION
                Constants.fine_course_background
            }
            else->{
                Constants.fine_course
            }
        }
        requestPermissions(permissionsArray,resultCode)
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        grantResults.forEach {
            if(it==PackageManager.PERMISSION_DENIED){
                if(runningQOrLater)
                    _viewModel.showsettingsnackBar.value= Constants.fine_course_background
                else
                    _viewModel.showsettingsnackBar.value= Constants.fine_course
                return
            }
        }
        if( android.os.Build.VERSION.SDK_INT>android.os.Build.VERSION_CODES.Q)
        {
            requestPermissions(arrayOf( Manifest.permission.ACCESS_BACKGROUND_LOCATION), Constants.fine_course_background)
            return
        }
        if (isPermissionGranted())
            makehomelocation()
    }
    @TargetApi(29)
    private fun isPermissionGranted() : Boolean {
        var bool= ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) === PackageManager.PERMISSION_GRANTED
        if(runningQOrLater) {
            bool = bool && ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        }
        return bool
    }
    private fun onLocationSelected() {
        //        TODO: When the user confirms on the selected location,
        //         send back the selected location details to the view model
        //         and navigate back to the previous fragment to save the reminder and add the geofence
        _viewModel.latitude.value=_viewModel.selectedPOI.value?.latLng?.latitude
        _viewModel.longitude.value=_viewModel.selectedPOI.value?.latLng?.longitude
        _viewModel.reminderSelectedLocationStr.value=_viewModel.selectedPOI.value?.name
        _viewModel.selectedPOI.value=null
        _viewModel.navigationCommand.value=NavigationCommand.Back
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_options, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        // TODO: Change the map type based on the user's selection.
        R.id.normal_map -> {
            mMap?.mapType = GoogleMap.MAP_TYPE_NORMAL

            true
        }
        R.id.hybrid_map -> {
            mMap?.mapType = GoogleMap.MAP_TYPE_HYBRID

            true
        }
        R.id.satellite_map -> {
            mMap?.mapType = GoogleMap.MAP_TYPE_SATELLITE
            true
        }
        R.id.terrain_map -> {
            mMap?.mapType = GoogleMap.MAP_TYPE_TERRAIN
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(p0: GoogleMap) {
        mMap=p0
        mMap?.apply {
            val latitude =30.135984897343306
            val longitude= 31.366324444040078
            val home = LatLng(latitude ,longitude)
            moveCamera(CameraUpdateFactory.newLatLngZoom(home,15f ))
            setMapLongClick(this)
            setPoiClick(this)

        }
        if (isPermissionGranted()) {
            makehomelocation()
        }
    }

    @SuppressLint("MissingPermission")
    private fun makehomelocation() {
        mMap?. setMyLocationEnabled(true)
    }

    private fun setPoiClick(mMap: GoogleMap) {
        mMap.setOnPoiClickListener { poi ->
            removemarker()
            poiMarker = mMap.addMarker(
                MarkerOptions()
                    .position(poi.latLng)
                    .title(poi.name)
            )
            poiMarker?.showInfoWindow()
            _viewModel.selectedPOI.value=poi
        }
    }

    private fun removemarker() {
        poiMarker?.apply {
            poiMarker!!.remove()
        }
    }

    private fun setMapLongClick(mMap: GoogleMap) {
        mMap.setOnMapLongClickListener { latLng ->
            removemarker()
            val snippet = String.format(
                Locale.getDefault(),
                "Lat: %1$.5f, Long: %2$.5f",
                latLng.latitude,
                latLng.longitude
            )
            poiMarker= mMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(getString(R.string.dropped_pin))
                    .snippet(snippet)
            )
            val poi=PointOfInterest(poiMarker!!.position,poiMarker!!.id,poiMarker!!.title!!)
            _viewModel.selectedPOI.value=poi
        }
    }



    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.map.onSaveInstanceState(outState)
    }
}
