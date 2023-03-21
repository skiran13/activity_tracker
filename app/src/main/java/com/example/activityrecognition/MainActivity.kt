package com.example.activityrecognition

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import pl.droidsonroids.gif.GifImageView

class MainActivity : AppCompatActivity() {

    private val TAG = MainActivity::class.java.simpleName
    internal lateinit var broadcastReceiver: BroadcastReceiver

    private lateinit var txtActivity: TextView
    private lateinit var txtConfidence: TextView
    private lateinit var activityGif: GifImageView
    private lateinit var btnStartTrcking: Button
    private lateinit var btnStopTracking: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        txtActivity = findViewById(R.id.txt_activity)
        txtConfidence = findViewById(R.id.txt_confidence)
        activityGif = findViewById(R.id.activitygif)
        btnStartTrcking = findViewById(R.id.btn_start_tracking)
        btnStopTracking = findViewById(R.id.btn_stop_tracking)

        btnStartTrcking?.setOnClickListener { startTracking() }

        btnStopTracking?.setOnClickListener { stopTracking() }

        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action == MainActivity.BROADCAST_DETECTED_ACTIVITY) {
                    val type = intent.getStringExtra("type")
                    if (type != null) {
                        handleUserActivity(type)
                    }
                }
            }
        }

    }

    private fun handleUserActivity(type: String) {
        var label = getString(R.string.activity_unknown)

        when (type) {
            "IN_VEHICLE" -> {
                label = "You are in Vehicle"
            }

            "RUNNING" -> {
                label = "You are Running"
            }
            "STILL" -> {
                label = "You are Still"
            }
            "WALKING" -> {
                label = "You are Walking"
            }
        }

        Log.d(TAG, "User activity: $label")

        if (label != "Unknown") {
            activityGif.visibility = View.INVISIBLE
            when(type) {
                "RUNNING" -> {
                    activityGif.setImageResource(R.drawable.run)
                    activityGif.visibility = View.VISIBLE
                }
                "STILL" -> {
                    activityGif.setImageResource(R.drawable.standing)
                    activityGif.visibility = View.VISIBLE
                }
               "WALKING" -> {
                    activityGif.setImageResource(R.drawable.walk)
                    activityGif.visibility = View.VISIBLE
                }
            }
            txtActivity?.text = label
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

        internal val DETECTION_INTERVAL_IN_MILLISECONDS: Long = 4000

    }
}