package com.udacity.project4.utils

import java.util.concurrent.TimeUnit

object Constants {
    val SignCode=1001
    private val REQUEST_LOCATION_PERMISSION = 1
    val fine_course_background=34
    val fine_course = 33
    val REQUEST_TURN_DEVICE_LOCATION_ON = 29
    val ACTION_GEOFENCE_EVENT="Geofenceproject.action.ACTION_GEOFENCE_EVENT"
    val TAG_Geofence = "Geofence"
    val AUTHTAG ="Auth_Activity"
    val Broadcast ="Auth_Activity"
    val GEOFENCE_RADIUS_IN_METERS = 100f
    val GEOFENCE_EXPIRATION_IN_MILLISECONDS: Long = TimeUnit.HOURS.toMillis(1)

    enum class UserLogINState {
        LoggingIN, UNLoggingIN
    }

}