package com.example.pomodoro

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pomodoro.adapters.StopwatchAdapter
import com.example.pomodoro.common.ForegroundService
import com.example.pomodoro.databinding.ActivityMainBinding
import com.example.pomodoro.entity.Stopwatch

class MainActivity : AppCompatActivity(), StopwatchListener, LifecycleObserver {

    private lateinit var binding: ActivityMainBinding

    private val stopwatches = mutableListOf<Stopwatch>()
    private var stopwatchAdapter = StopwatchAdapter(this)
    private var nextId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = stopwatchAdapter
        }

        binding.apply {
            btnAddStopwatch.setOnClickListener {
                val timeInput = edtTimeInput.text.toString().toLong() * 60000L
                stopwatches.add(Stopwatch(nextId++, timeInput, false, timeInput))
                stopwatchAdapter.submitList(stopwatches.toList())
            }
        }
    }

    private fun changeStopwatch(id: Int, currentMs: Long?, isStarted: Boolean, totalTime: Long) {
        val newTimers = mutableListOf<Stopwatch>()
        stopwatches.forEach {
            if (it.id == id) {
                newTimers.add(Stopwatch(it.id, currentMs ?: it.currentMs, isStarted, totalTime))
            } else {
                val temp = Stopwatch(it.id, it.currentMs, false, it.totalTime)
                newTimers.add(temp)
            }
        }
        stopwatchAdapter.submitList(newTimers)
        stopwatches.clear()
        stopwatches.addAll(newTimers)
    }

    override fun start(id: Int, currentMs: Long, totalTime: Long) {
        changeStopwatch(id, currentMs, true, totalTime)
    }

    override fun stop(id: Int, currentMs: Long, totalTime: Long) {
        changeStopwatch(id, currentMs, false, totalTime)
    }

    override fun delete(id: Int) {
        stopwatches.remove(stopwatches.find { it.id == id })
        stopwatchAdapter.submitList(stopwatches.toList())
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {
        val startIntent = Intent(this, ForegroundService::class.java)
        startIntent.putExtra(COMMAND_ID, COMMAND_START)
        startIntent.putExtra(STARTED_TIMER_TIME_MS, startTime)
        startService(startIntent)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForegrounded() {
        val stopIntent = Intent(this, ForegroundService::class.java)
        stopIntent.putExtra(COMMAND_ID, COMMAND_STOP)
        startService(stopIntent)
    }
}