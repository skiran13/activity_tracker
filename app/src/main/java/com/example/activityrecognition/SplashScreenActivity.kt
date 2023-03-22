package com.example.activityrecognition

import android.app.Service
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

//This is the entry point of the app from where we can press the button to Launch Game
class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen)

// This button lets us navigate to MainMenu screen
        val current = Calendar.getInstance().time
//        val pattern = "yyyy/MM/dd HH:mm:ss"
        val pattern = "MMMM d, yyyy  HH:mm:ss"
        val formatter = SimpleDateFormat(pattern, Locale.getDefault())
        val dateTime = formatter.format(current)
        var txtDateTime:TextView = findViewById(R.id.dateTime_text)


        txtDateTime.text = dateTime
//        val button : Button = findViewById(R.id.launch_game)

        Handler().postDelayed(Runnable  {
            val i = Intent(this, MainActivity::class.java)
            startActivity(i)
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out,)
            finish()
        }, 8000)

    }
}