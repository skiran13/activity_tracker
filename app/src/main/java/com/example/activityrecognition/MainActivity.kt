package com.example.activityrecognition

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.DetectedActivity

class MainActivity : AppCompatActivity() {

    private val TAG = MainActivity::class.java.simpleName
    internal lateinit var broadcastReceiver: BroadcastReceiver

    private lateinit var txtActivity: TextView
    private lateinit var txtConfidence: TextView
    private lateinit var btnStartTrcking: Button
    private lateinit var btnStopTracking: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        txtActivity = findViewById(R.id.txt_activity)
        txtConfidence = findViewById(R.id.txt_confidence)
        btnStartTrcking = findViewById(R.id.btn_start_tracking)
        btnStopTracking = findViewById(R.id.btn_stop_tracking)
        requestActivityRecognitionPermission()

        btnStartTrcking?.setOnClickListener { startTracking() }

        btnStopTracking?.setOnClickListener { stopTracking() }

        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action == MainActivity.BROADCAST_DETECTED_ACTIVITY) {
                    val type = intent.getIntExtra("type", -1)
                    val confidence = intent.getIntExtra("confidence", 0)
                    handleUserActivity(type, confidence)
                }
            }
        }

    }

    private fun handleUserActivity(type: Int, confidence: Int) {
        var label = getString(R.string.activity_unknown)

        when (type) {
            DetectedActivity.IN_VEHICLE -> {
                label = "You are in Vehicle"
            }
            DetectedActivity.ON_BICYCLE -> {
                label = "You are on Bicycle"
            }
            DetectedActivity.ON_FOOT -> {
                label = "You are on Foot"
            }
            DetectedActivity.RUNNING -> {
                label = "You are Running"
            }
            DetectedActivity.STILL -> {
                label = "You are Still"
            }
            DetectedActivity.TILTING -> {
                label = "Your phone is Tilted"
            }
            DetectedActivity.WALKING -> {
                label = "You are Walking"
            }
            DetectedActivity.UNKNOWN -> {
                label = "Unkown Activity"
            }
        }

        Log.e(TAG, "User activity: $label, Confidence: $confidence")

        if (confidence > MainActivity.CONFIDENCE) {
            txtActivity?.text = label
            txtConfidence?.text = "Confidence: $confidence"
        }
    }

    override fun onResume() {
        super.onResume()

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
            IntentFilter(BROADCAST_DETECTED_ACTIVITY)
        )
    }

    override fun onPause() {
        super.onPause()

        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
    }

    private fun startTracking() {
        val intent = Intent(this, BackgroundDetectedActivitiesService::class.java)
        startService(intent)
        Log.d("STAT","tracking started")
    }

    private fun stopTracking() {
        val intent = Intent(this, BackgroundDetectedActivitiesService::class.java)
        stopService(intent)
        Log.d("STAT","tracking stopped")
    }

    companion object {

        val BROADCAST_DETECTED_ACTIVITY = "activity_intent"

        internal val DETECTION_INTERVAL_IN_MILLISECONDS: Long = 1000

        val CONFIDENCE = 70
    }

    private fun requestActivityRecognitionPermission(){
        if (ContextCompat.checkSelfPermission(this@MainActivity,
                android.Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this@MainActivity,
                arrayOf(android.Manifest.permission.ACTIVITY_RECOGNITION), 101)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            101 -> {
                if (grantResults.isNotEmpty()) {
                    for (i in grantResults.indices) {
                        val permission = permissions[i]
                        if (android.Manifest.permission.ACTIVITY_RECOGNITION.equals(permission,
                                ignoreCase = true)) {
                            if (grantResults[i] === PackageManager.PERMISSION_GRANTED) {
                                // you now have permission
                            }
                        }
                    }
                }
            }
        }
    }
}