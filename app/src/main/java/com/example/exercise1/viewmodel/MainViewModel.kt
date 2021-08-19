package com.example.exercise1.viewmodel

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import java.lang.Thread.currentThread
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong

class MainViewModel : ViewModel() {
    private val job1 = Job()
    private val job2 = Job()
    private val _num: MutableLiveData<Int> = MutableLiveData(0)
    val num: LiveData<Int> = _num

    // countdown state
    var state = AtomicBoolean(false) //Shared mutable state and concurrency
    var lastTimeClick = AtomicLong(0L)

    var lastNumberChangeColer: Int = 0

    val _changeColor: MutableLiveData<Boolean> = MutableLiveData(false)
    val changeColor: LiveData<Boolean> = _changeColor

    var threadCd = Thread()
    val handler = Handler(Looper.getMainLooper())

    val runnable = Runnable {
        while (state.get()) {
            try {
                Thread.sleep(50L)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            if (num.value!! < 0) increase()
            else if (num.value!! > 0) decrease()
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
        if (Math.abs((num.value?.minus(lastNumberChangeColer))!!) > 100) {
//            Log.e("tag", "${lastNumberChangeColer}")
            lastNumberChangeColer = num.value!!
            _changeColor.postValue(true)
        } else {
            _changeColor.postValue(false)
        }
    }

    fun increase() {
        _num.postValue(_num.value?.plus(1))
    }

    fun decrease() {
        _num.postValue(_num.value?.minus(1))
    }

}