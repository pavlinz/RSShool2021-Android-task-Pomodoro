package com.example.pomodoro

interface StopwatchListener {

    fun start(id: Int, currentMs: Long, totalTime: Long)

    fun stop(id: Int, currentMs: Long, totalTime: Long)

    fun delete(id: Int)
}