package com.example.exercise1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.util.Log
import android.view.MotionEvent
import androidx.activity.viewModels
import androidx.core.view.MotionEventCompat
import com.example.exercise1.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    var oldY: Float = 0F
    private val viewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_increase?.setOnClickListener {
            viewModel.increase()
            viewModel.state = false
            viewModel.lastTimeClick = System.currentTimeMillis()
        }
        btn_decrease?.setOnClickListener {
            viewModel.decrease()
            viewModel.state = false
            viewModel.lastTimeClick = System.currentTimeMillis()
        }
        viewModel.num.observe(this, {
            tv.text = it.toString()
        })

    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val action: Int = MotionEventCompat.getActionMasked(event)


        return when (action) {
            MotionEvent.ACTION_DOWN -> {
                oldY = event!!.y
                viewModel.state = false
                viewModel.lastTimeClick = null
                Log.d("Tagd", "Action was DOWN ${viewModel.lastTimeClick}")
                true
            }
            MotionEvent.ACTION_MOVE -> {
                Log.d("Tagd", "Action was move ${viewModel.lastTimeClick} ${viewModel.state}")
                val curY = event?.y
                val deltaY = curY?.minus(oldY)
                if (curY != null) {
                    oldY = curY
                }
                Log.d("Tagd", "Action was MOVE cur-$curY old $oldY deta $deltaY")

                if (deltaY!! > 5) {
                    viewModel.decrease()
                } else if (deltaY < -5) {
                    viewModel.increase()
                }

                true
            }
            MotionEvent.ACTION_UP -> {
                viewModel.lastTimeClick = System.currentTimeMillis()
                Log.d("Tagd", "Action was up ${viewModel.lastTimeClick}  ${viewModel.state}")
                true
            }

            else -> super.onTouchEvent(event)
        }
    }


}