package com.laba.user.common

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.view.animation.LinearInterpolator
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.google.firebase.firestore.GeoPoint
import com.laba.user.R
import kotlin.jvm.internal.Intrinsics
import kotlin.math.abs
import kotlin.math.atan


object MarkerCarUtils {

    fun updateCarLocation(
        context: Context,
        googleMap: GoogleMap,
        driverId: Int,
        driverLocation: LatLng,
        providersMarker: HashMap<Int, Marker>
    ) {
        if (!providersMarker.containsKey(driverId) || providersMarker[driverId] == null) {
            val cabMarker = addCarMarkerAndGet(context,googleMap,driverLocation)
            cabMarker.position = driverLocation
            cabMarker.setAnchor(0.5f, 0.5f)
            providersMarker.put(driverId,cabMarker)
        }else{
            val cabMarker = providersMarker[driverId]!!
            val previousLatLng = cabMarker.position

            val startPoint = Location("origin")
            startPoint.latitude = driverLocation!!.latitude
            startPoint.longitude = driverLocation!!.longitude

            val endPoint = Location("locationA")
            endPoint.latitude = previousLatLng!!.latitude
            endPoint.longitude = previousLatLng!!.longitude

            if (startPoint.distanceTo(endPoint) > 4) {
                val valueAnimator = carAnimator()
                valueAnimator.addUpdateListener { va ->
                    if (driverLocation != null && previousLatLng != null) {
                        val multiplier = va.animatedFraction
                        val nextLocation = LatLng(
                            multiplier * driverLocation.latitude + (1 - multiplier) * previousLatLng.latitude,
                            multiplier * driverLocation.longitude + (1 - multiplier) * previousLatLng.longitude
                        )
                        cabMarker.position = nextLocation
                        val rotation = getRotation(previousLatLng, nextLocation)
                        if (!rotation.isNaN()) {
                            cabMarker.rotation = rotation
                        }
                        cabMarker.setAnchor(0.5f, 0.5f)
                        // animateCamera(nextLocation)
                    }
                }
                valueAnimator.start()
            }

        }
    }

    private fun addCarMarkerAndGet(
        context: Context,
        googleMap: GoogleMap,
        latLng: LatLng
    ): Marker {

        return googleMap.addMarker(
            MarkerOptions().position(latLng).flat(true).icon(getCarBitmapDescriptor(context))
        )
    }

    fun getCarBitmapDescriptor(context: Context): BitmapDescriptor {
        return    BitmapDescriptorFactory.fromBitmap(getCarBitmap(context))
    }

    private fun getCarBitmap(context: Context): Bitmap {
        val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.car_icon_2)
        return Bitmap.createScaledBitmap(bitmap, 65, 98, false)
    }

    fun getRotation(start: LatLng, end: LatLng): Float {
        val latDifference: Double = abs(start.latitude - end.latitude)
        val lngDifference: Double = abs(start.longitude - end.longitude)
        var rotation = -1F
        when {
            start.latitude < end.latitude && start.longitude < end.longitude -> {
                rotation = Math.toDegrees(atan(lngDifference / latDifference)).toFloat()
            }
            start.latitude >= end.latitude && start.longitude < end.longitude -> {
                rotation = (90 - Math.toDegrees(atan(lngDifference / latDifference)) + 90).toFloat()
            }
            start.latitude >= end.latitude && start.longitude >= end.longitude -> {
                rotation = (Math.toDegrees(atan(lngDifference / latDifference)) + 180).toFloat()
            }
            start.latitude < end.latitude && start.longitude >= end.longitude -> {
                rotation =
                    (90 - Math.toDegrees(atan(lngDifference / latDifference)) + 270).toFloat()
            }
        }
        return rotation
    }
    fun carAnimator(): ValueAnimator {
        val valueAnimator = ValueAnimator.ofFloat(0f, 1f)
        valueAnimator.duration = 1000
        valueAnimator.interpolator = LinearInterpolator()
        return valueAnimator
    }
}