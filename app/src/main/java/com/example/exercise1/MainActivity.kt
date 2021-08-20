package com.example.exercise1

import android.annotation.SuppressLint
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.MotionEvent
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.MotionEventCompat
import com.example.exercise1.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger


class MainActivity : AppCompatActivity() {
    private var oldY: Float = 0F
    private val viewModel: MainViewModel by viewModels()




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
                   isChangeColer()
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
                    stopThreadCoundownt()
                    decrease()
                    isChangeColer()
                   state.set(false)
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
                    oldY = event.y
                    isChangeColer()
                    state.set(false)
                    stopThreadCoundownt()
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

        viewModel.changeColor.observe(this, {
            if (it) {
                var rand: Int = R.color.red
                while (ContextCompat.getColor(this, rand) == tv.currentTextColor) {
                    rand = listColor.random().toInt()
                }
                try {
                    tv.setTextColor(ContextCompat.getColor(this, rand))
                } catch (e : Resources.NotFoundException){
                    tv.setTextColor(ContextCompat.getColor(this, R.color.textColor5))
                }
            }
        })
    }

    var num : Int =0

    var state = AtomicBoolean(false) //Shared mutable state and concurrency

    var lastNumberChangeColer = AtomicInteger(0)

    val changeColor: AtomicBoolean = AtomicBoolean(false)

    var threadCd = Thread()
    val handler =object : Handler(Looper.getMainLooper()){
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when(msg.what){
                MSG_UPDATE_TV ->{
                    tv.text = msg.arg1.toString()
                }
                else -> Unit
            }
        }

    }

    val runnable = Runnable {
        while (state.get()) {
            try {
                Thread.sleep(50L)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            if (num < 0) increase()
            else if (num > 0) decrease()
            isChangeColer()
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
        if (Math.abs((num.minus(lastNumberChangeColer.get()))) > 100) {
//            Log.e("tag", "${lastNumberChangeColer}")
            lastNumberChangeColer = num
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

    private val listColor: MutableList<Int> = mutableListOf(
        R.color.textColor1,
        R.color.textColor2,
        R.color.textColor3,
        R.color.textColor4,
        R.color.textColor5,
        R.color.textColor6,
        R.color.textColor7
    )
companion object{
    const val MSG_UPDATE_TV = 1
    const val MSG_CHANGCOLOR = 2
}

}