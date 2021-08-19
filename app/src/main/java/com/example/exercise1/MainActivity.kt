package com.example.exercise1

import android.annotation.SuppressLint
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.MotionEventCompat
import com.example.exercise1.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*


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
                    viewModel.state.set(false)
                    viewModel.stopThreadCoundownt()
                    viewModel.increase()
                    viewModel.isChangeColer()
                    true
                }
                MotionEvent.ACTION_UP -> {
                    viewModel.state.set(true)
                    viewModel.startThreadCoundownt()
                    true
                }
                else -> true
            }
        }
        btn_decrease?.setOnTouchListener { v, event ->
            val action: Int = MotionEventCompat.getActionMasked(event)
            when (action) {
                MotionEvent.ACTION_DOWN -> {
                    viewModel.stopThreadCoundownt()
                    viewModel.decrease()
                    viewModel.isChangeColer()
                    viewModel.state.set(false)
                    true
                }
                MotionEvent.ACTION_UP -> {
                    viewModel.state.set(true)
                    viewModel.startThreadCoundownt()
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
                    viewModel.isChangeColer()
                    viewModel.state.set(false)
                    viewModel.stopThreadCoundownt()
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    val curY = event.y
                    val deltaY = curY.minus(oldY)
                    oldY = curY
                    if (deltaY > 5) {
                        viewModel.decrease()
                    } else if (deltaY < -5) {
                        viewModel.increase()
                    }
                    true
                }
                MotionEvent.ACTION_UP -> {
                    viewModel.state.set(true)
                    viewModel.startThreadCoundownt()
                    true
                }
                else -> true
            }
        }
        obsever()
    }

    private fun obsever() {
        viewModel.num.observe(this, {
            tv.text = it.toString()
            if (it == 0){
                viewModel.state.set(false)
                viewModel.stopThreadCoundownt()
            }
        })
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


    private val listColor: MutableList<Int> = mutableListOf(
        R.color.textColor1,
        R.color.textColor2,
        R.color.textColor3,
        R.color.textColor4,
        R.color.textColor5,
        R.color.textColor6,
        R.color.textColor7
    )


}