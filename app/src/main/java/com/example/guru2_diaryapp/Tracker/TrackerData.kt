package com.example.guru2_diaryapp.Tracker


class TrackerData {
    var habit: String = ""
    var reportingDate = ArrayList<Int>(31)
    var checkResult = ArrayList<Int>(31)

    constructor(habit: String, reportingDate: ArrayList<Int>, checkResult: ArrayList<Int>) {
        this.habit = habit
        this.reportingDate = reportingDate
        this.checkResult = checkResult
    }
}