package com.example.pomodoro.adapters

import android.content.res.Resources
import android.graphics.drawable.AnimationDrawable
import android.os.CountDownTimer
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import com.example.pomodoro.R
import com.example.pomodoro.StopwatchListener
import com.example.pomodoro.databinding.PomodoroItemBinding
import com.example.pomodoro.entity.Stopwatch
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class StopwatchViewHolder(
    private val binding: PomodoroItemBinding,
    private val listener: StopwatchListener,
    private val resources: Resources
) : RecyclerView.ViewHolder(binding.root) {

    private var timer: CountDownTimer? = null

    fun bind(stopwatch: Stopwatch) {
        binding.tvTimerItem.text = stopwatch.currentMs.displayTime()

        if (stopwatch.isStarted) {
            startTimer(stopwatch)
        } else {
            stopTimer(stopwatch)
        }

        initButtonsListeners(stopwatch)
    }

    private fun initButtonsListeners(stopwatch: Stopwatch) {
        binding.btnStartPauseItem.setOnClickListener {
            if (stopwatch.isStarted) {
                listener.stop(stopwatch.id, stopwatch.currentMs)
            } else {
                listener.start(stopwatch.id, stopwatch.currentMs)
            }
        }

        binding.ibDeleteItem.setOnClickListener { listener.delete(stopwatch.id) }
    }

    private fun startTimer(stopwatch: Stopwatch) {
        timer?.cancel()
        timer = getCountDownTimer(stopwatch)
        timer?.start()

        binding.apply {
            btnStartPauseItem.text = resources.getString(R.string.stop)
            ivIndicatorItem.apply {
                isInvisible = false
                (background as? AnimationDrawable)?.start()
            }
            dialView.setPeriod(1000L * 30)
            var current = 0L

            GlobalScope.launch {
                while (current < PERIOD * 10) {
                    current += 100L
                    binding.dialView.setCurrent(current)
                    delay(100L)
                }
            }
        }
    }

    private fun stopTimer(stopwatch: Stopwatch) {
        timer?.cancel()

        binding.apply {
            btnStartPauseItem.text = resources.getString(R.string.start)
            ivIndicatorItem.apply {
                isInvisible = true
                (background as? AnimationDrawable?)?.stop()
            }
        }
    }

    private fun getCountDownTimer(stopwatch: Stopwatch): CountDownTimer {
        return object : CountDownTimer(stopwatch.currentMs, UNIT_TEN_MS) {
            val interval = UNIT_TEN_MS

            override fun onTick(millisUntilFinished: Long) {
                stopwatch.currentMs -= interval
                binding.tvTimerItem.text = stopwatch.currentMs.displayTime()
            }

            override fun onFinish() {
                binding.tvTimerItem.text = stopwatch.currentMs.displayTime()
            }
        }
    }

    private fun Long.displayTime(): String {
        if (this <= 0L) {
            return START_TIME
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

    private companion object {

        private const val START_TIME = "00:00:00"
        private const val UNIT_TEN_MS = 1000L
        private const val PERIOD = 1000L * 60L * 60L * 24L // Day
    }
}