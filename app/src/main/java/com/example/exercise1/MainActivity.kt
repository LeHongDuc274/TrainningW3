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

        btn_increase?.setOnClickListener {
            viewModel.increase()
            onButtonClick()
        }
        btn_decrease?.setOnClickListener {
            viewModel.decrease()
            onButtonClick()
        }

        observeData()

        linear_layout.setOnTouchListener { _, event ->
            val action: Int = MotionEventCompat.getActionMasked(event)
            when (action) {
                MotionEvent.ACTION_DOWN -> {
                    oldY = event.y
                    viewModel.isChangeColer()
                    viewModel.state.set(false)
                    viewModel.lastTimeClick.set(0L)
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    val curY = event.y
                    val deltaY = curY.minus(oldY)
                    oldY = curY
                    viewModel._changeColor.value = false
                    if (deltaY > 5) {
                        viewModel.decrease()
                    } else if (deltaY < -5) {
                        viewModel.increase()
                    }
                    true
                }
                MotionEvent.ACTION_UP -> {
                    viewModel.lastTimeClick.set(System.currentTimeMillis())
                    true
                }
                else -> true
            }
        }
    }

    private fun observeData() {
        viewModel.num.observe(this, {
            tv.text = it.toString()
        })

        viewModel.changeColor.observe(this, {
            if (it) {
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
        })
    }

    private fun onButtonClick() {
        viewModel.state.set(false)
        viewModel.lastTimeClick.set(System.currentTimeMillis())
        viewModel.isChangeColer()
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

//    override fun onTouchEvent(event: MotionEvent?): Boolean {
//
//    }


}