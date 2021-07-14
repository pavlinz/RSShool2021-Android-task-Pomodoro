package com.example.pomodoro.common

import com.example.pomodoro.adapters.StopwatchViewHolder

private fun Long.displayTime(): String {
    if (this <= 0L) {
        return StopwatchViewHolder.START_TIME
    }
    val h = this / 1000 / 3600
    val m = this / 1000 % 3600 / 60
    val s = this / 1000 % 60

    return "${displaySlot(h)}:${displaySlot(m)}:${displaySlot(s)}"
}

private fun displaySlot(count: Long): String {
    return if (count / 10L > 0) {
        "$count"
    } else {
        "0$count"
    }
}

private const val START_TIME = "00:00:00"