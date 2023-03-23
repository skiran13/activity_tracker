package com.example.activityrecognition
//This class is represents an Activity instance and is used to store the attributes associated with an Activity so
// that they can be passed to the INSERT Command of SQLite
class Activity {
    var id: Int = 0
    var activity_name: String = ""
    var date: String = ""
    var start_time: String = ""
    var duration: String = ""

    constructor(activity_name: String, date: String, start_time:String, duration: String){
        this.activity_name = activity_name
        this.date = date
        this.start_time = start_time
        this.duration = duration
    }

    constructor(){

    }
}