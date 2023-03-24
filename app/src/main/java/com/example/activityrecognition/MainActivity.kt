package com.example.activityrecognition

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import pl.droidsonroids.gif.GifImageView
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private val TAG = MainActivity::class.java.simpleName
    internal lateinit var broadcastReceiver: BroadcastReceiver

    private lateinit var txtActivity: TextView
    private lateinit var txtQuote: TextView
    private lateinit var activityGif: GifImageView
    private lateinit var btnStartTrcking: Button
    private lateinit var btnStopTracking: Button

    private lateinit var audio: Intent
    private lateinit var handler: Handler
    private val activityData = ArrayList<String>(5)

    private val patternTime = "HH:mm:ss"
    private val patternDate = "dd/MM/yyyy"
    private lateinit var timeFormatter: SimpleDateFormat
    private lateinit var dateFormatter: SimpleDateFormat
    private lateinit var db: DatabaseHandler
    private var lastActivity = "" // TO keep track of the last activity that the user performed

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        txtActivity = findViewById(R.id.txt_activity)
        txtQuote = findViewById(R.id.txt_quote)
        activityGif = findViewById(R.id.activitygif)
        btnStartTrcking = findViewById(R.id.btn_start_tracking)
        btnStopTracking = findViewById(R.id.btn_stop_tracking)

        val context = this
        db = DatabaseHandler(context)
        audio = Intent(this, BackgroundMusicService::class.java)

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

//Once the activity type is detected,  the respective images for that activity are displayed and the actions associated with that activities are performed

        if (label != "Unknown") {
            activityGif.visibility = View.INVISIBLE
            when(type) {
                "RUNNING" -> {
                    startService(audio)  // Music starts playing when the user is running
                    activityGif.setBackgroundResource(R.drawable.border)
                    activityGif.setImageResource(R.drawable.run)
                    activityGif.visibility = View.VISIBLE
                    if (txtQuote.visibility==View.VISIBLE){
                        txtQuote.visibility = View.INVISIBLE
                    }

                    if (lastActivity == "" || lastActivity!="Running") {
                        onActivityChange(activityData, "Running")
                        lastActivity = "Running"
                    }

                }
                "STILL" -> {
                    stopService(audio)
                    activityGif.setBackgroundResource(R.drawable.border)
                    activityGif.setImageResource(R.drawable.standing)
                    activityGif.visibility = View.VISIBLE
                    if (txtQuote.visibility == View.VISIBLE) {
                        txtQuote.visibility = View.INVISIBLE
                    }

                    if (lastActivity == "" || lastActivity!="Still") {
                        onActivityChange(activityData, "Still")
                        lastActivity = "Still"
                    }
                }
               "WALKING" -> {
                   stopService(audio)
                   activityGif.setBackgroundResource(R.drawable.border)
                   activityGif.setImageResource(R.drawable.walk)

                   activityGif.visibility = View.VISIBLE
                   txtQuote.visibility = View.VISIBLE

                   if (lastActivity == "" || lastActivity!="Walking") {
                       onActivityChange(activityData, "Walking")
                       lastActivity = "Walking"
                   }

                }
                "IN_VEHICLE" -> {
                    stopService(audio)
                    activityGif.setBackgroundResource(0)
                    activityGif.setImageResource(R.drawable.drive)
                    activityGif.visibility = View.VISIBLE
                    if (txtQuote.visibility==View.VISIBLE){
                        txtQuote.visibility = View.INVISIBLE
                    }

                    if (lastActivity == "" || lastActivity!="In_Vehicle") {
                        onActivityChange(activityData, "In_Vehicle")
                        lastActivity = "In_Vehicle"
                    }
                }
            }
            txtActivity?.text = label
        }
    }

//    This method is called every time the user activity changes
//    The duration of the previous activity is calculated, the activity details are fetched from the activityData Array List and inserted into the sqlite table
//    The duration of the last activity is displayed in the toast
//    The details of the current activity are added to the ActivityData List
    private fun onActivityChange(activityData: ArrayList<String>, activity_name: String, stop: Boolean = false) {
        val current = Calendar.getInstance()
        timeFormatter = SimpleDateFormat(patternTime, Locale.getDefault())
        dateFormatter = SimpleDateFormat(patternDate, Locale.getDefault())
        val date = dateFormatter.format(current.time)
        val time = timeFormatter.format(current.time)


        if (activityData.isNotEmpty()) {
            val act = activityData[0]

            val durationMillis = current.timeInMillis - activityData[3].toLong()
            val durationMinutes = durationMillis / (60 * 1000)
            val remainingSeconds = ((durationMillis / 1000) - (60 * durationMinutes))
            Toast.makeText(
                this,
                "You were $act for $durationMinutes mins, $remainingSeconds seconds",
                Toast.LENGTH_SHORT
            ).show()

            var activity = Activity(
                activityData[0].toString(),
                activityData[1].toString(),
                activityData[2].toString(),
                "$durationMinutes minutes, $remainingSeconds seconds"
            )
            db.insertData(activity)
            activityData.clear()
        }
        if (stop != true) {
            activityData.add(activity_name)
            activityData.add(date)
            activityData.add(time)
            activityData.add(current.timeInMillis.toString())
        }

    }
//Every time the app resumes, we listen for the broadcast from other activities
    override fun onResume() {
        super.onResume()

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
            IntentFilter(BROADCAST_DETECTED_ACTIVITY)
        )
    }
//Every time the app pauses, we stop listening for the broadcast from other activities
    override fun onPause() {
        super.onPause()

        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
    }
    // The device starts tracking the user's motion once this method is called
    private fun startTracking() {
        val intent = Intent(this, BackgroundDetectedActivitiesService::class.java)
        startService(intent)
        Log.d("STAT","tracking started")
    }
    // The device stops tracking the user's motion once this method is called and saves the last activity's data in the table
    private fun stopTracking() {
        val intent = Intent(this, BackgroundDetectedActivitiesService::class.java)
        stopService(intent)
        Log.d("STAT","tracking stopped")
        stopService(audio)
        onActivityChange(activityData, "", stop = true)
    }

    companion object {

        val BROADCAST_DETECTED_ACTIVITY = "activity_intent"

        internal val DETECTION_INTERVAL_IN_MILLISECONDS: Long = 4000

    }
}