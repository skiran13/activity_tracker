package com.example.activityrecognition

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