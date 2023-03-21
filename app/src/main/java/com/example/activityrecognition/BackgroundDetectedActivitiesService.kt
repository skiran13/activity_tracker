package com.example.activityrecognition


import android.app.Service
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import kotlin.math.sqrt


@Suppress("DEPRECATION", "UNREACHABLE_CODE")
class BackgroundDetectedActivitiesService :Service(), SensorEventListener {

    private var sensorReading:Float = 0f
    private var handler = Handler()

    var handlerBool : Boolean = false
    lateinit var mSensorManager: SensorManager
    lateinit var mAccelerometer: Sensor
    inner class MyBinder : Binder() {
        fun getService() : BackgroundDetectedActivitiesService? {
            return this@BackgroundDetectedActivitiesService
        }
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("BDAS","created")
        mSensorManager = applicationContext.getSystemService(SENSOR_SERVICE) as SensorManager
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        requestActivityUpdatesButtonHandler()
        handlerBool = true
        broadcastActivity()


    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return START_STICKY
    }

    override fun onBind(p0: Intent?): IBinder = MyBinder()

    private fun requestActivityUpdatesButtonHandler() {

        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        Toast.makeText(
            applicationContext,
            "Successfully requested activity updates",
            Toast.LENGTH_SHORT
        )
            .show()
    }

    private fun removeActivityUpdatesButtonHandler() {
        mSensorManager.unregisterListener(this)
        handlerBool = false
        Toast.makeText(
            applicationContext,
            "Removed activity updates successfully!",
            Toast.LENGTH_SHORT
        ).show()
        handler.removeCallbacksAndMessages(null)
    }

    override fun onDestroy() {
        super.onDestroy()
        removeActivityUpdatesButtonHandler()
    }

    fun broadcastActivity() {
        var activityType:String
        if(sensorReading - 9.8 > 2 && sensorReading - 9.8 < 7) {
            activityType = "WALKING"
        } else if(sensorReading - 9.8 >= 7) {
            activityType = "RUNNING"
        } else {
            activityType = "STILL"
        }
        val intent = Intent(MainActivity.BROADCAST_DETECTED_ACTIVITY)
        intent.putExtra("type", activityType)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
        Log.d("Broadcast", activityType)
        handler.postDelayed({
            broadcastActivity()
        }, MainActivity.DETECTION_INTERVAL_IN_MILLISECONDS)
    }
    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null) {
            sensorReading = sqrt(Math.pow(event.values[0].toDouble(), 2.0).toFloat()
                    + Math.pow(event.values[1].toDouble(), 2.0).toFloat()
                    + Math.pow(event.values[2].toDouble(), 2.0).toFloat())
        }
    }
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }
}

