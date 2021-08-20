package com.example.exercise1

import android.annotation.SuppressLint
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.MotionEvent
import androidx.core.content.ContextCompat
import androidx.core.view.MotionEventCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

const val MSG_UPDATE_TV = 1
const val MSG_CHANGCOLOR = 2

class MainActivity : AppCompatActivity() {
    private var oldY: Float = 0F
    private var num: Int = 0
    private val state = AtomicBoolean(false) //Shared mutable state and concurrency
    private val lastNumberChangeColer = AtomicInteger(0)
    private val changeColor: AtomicBoolean = AtomicBoolean(false)
    private var threadCd = Thread()

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn_increase?.setOnTouchListener { v, event ->
            val action: Int = MotionEventCompat.getActionMasked(event)
            when (action) {
                MotionEvent.ACTION_DOWN -> {
                    state.set(false)
                    stopThreadCoundownt()
                    increase()
                    tv.text = num.toString()
                    isChangeColer()
                    if (changeColor.get()) {
                        randomColor()
                    }
                    true
                }
                MotionEvent.ACTION_UP -> {
                    state.set(true)
                    startThreadCoundownt()
                    true
                }
                else -> true
            }
        }
        btn_decrease?.setOnTouchListener { v, event ->
            val action: Int = MotionEventCompat.getActionMasked(event)
            when (action) {
                MotionEvent.ACTION_DOWN -> {
                    state.set(false)
                    stopThreadCoundownt()
                    decrease()
                    tv.text = num.toString()
                    isChangeColer()
                    if (changeColor.get()) {
                        randomColor()
                    }
                    true
                }
                MotionEvent.ACTION_UP -> {
                    state.set(true)
                    startThreadCoundownt()
                    true
                }
                else -> true
            }
        }

        linear_layout.setOnTouchListener { _, event ->
            val action: Int = MotionEventCompat.getActionMasked(event)
            when (action) {
                MotionEvent.ACTION_DOWN -> {
                    state.set(false)
                    stopThreadCoundownt()
                    oldY = event.y
                    isChangeColer()
                    if (changeColor.get()) {
                        randomColor()
                    }
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    val curY = event.y
                    val deltaY = curY.minus(oldY)
                    oldY = curY
                    if (deltaY > 5) {
                        decrease()
                    } else if (deltaY < -5) {
                        increase()
                    }
                    tv.text = num.toString()
                    true
                }
                MotionEvent.ACTION_UP -> {
                    state.set(true)
                    startThreadCoundownt()
                    true
                }
                else -> true
            }
        }
    }

    val handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                MSG_UPDATE_TV -> {
                    tv.text = msg.arg1.toString()
                }
                MSG_CHANGCOLOR -> randomColor()
                else -> Unit
            }
        }
    }

    val runnable = Runnable {
        while (state.get() && num != 0) {
            try {
                Thread.sleep(50L)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            if (num < 0) increase()
            else if (num > 0) decrease()
            isChangeColer()
            if (changeColor.get()) {
                handler.sendMessage(handler.obtainMessage(MSG_CHANGCOLOR, null))
            }
            handler.sendMessage(handler.obtainMessage(MSG_UPDATE_TV, num, 0))
        }
    }

    fun stopThreadCoundownt() {
        handler.removeCallbacksAndMessages(null)
        if (threadCd.isInterrupted == false) threadCd.interrupt()
    }

    fun startThreadCoundownt() {
        threadCd = Thread(runnable)
        handler.postDelayed({ threadCd.start() }, 1000L)
    }

    fun isChangeColer() {
        if (Math.abs((num.minus(lastNumberChangeColer.get()))) > 10) {
            lastNumberChangeColer.set(num)
            changeColor.set(true)
        } else {
            changeColor.set(false)
        }
    }

    fun increase() {
        num = (num.plus(1))
    }

    fun decrease() {
        num = (num.minus(1))
    }

    fun randomColor() {
        var rand: Int = R.color.red
        while (ContextCompat.getColor(this, rand) == tv.currentTextColor) {
            rand = listColor.random().toInt()
        }
        try {
            tv.setTextColor(ContextCompat.getColor(this, rand))
        } catch (e: Resources.NotFoundException) {
            tv.setTextColor(ContextCompat.getColor(this, R.color.textColor5))
        }
    }
}

private val listColor: MutableList<Int> = mutableListOf(
    R.color.textColor1,
    R.color.textColor2,
    R.color.textColor3,
    R.color.textColor4,
    R.color.textColor5,
    R.color.textColor6,
    R.color.textColor7
)


