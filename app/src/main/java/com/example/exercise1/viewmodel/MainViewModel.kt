package com.example.exercise1.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong

class MainViewModel : ViewModel() {
    private val job1 = Job()
    private val job2 = Job()
    private val _num: MutableLiveData<Int> = MutableLiveData(0)
    val num: LiveData<Int> = _num

    // countdown state
    var state= AtomicBoolean(false) //Shared mutable state and concurrency
    var lastTimeClick = AtomicLong(0L)

    var lastNumberChangeColer : Int =0

    val _changeColor : MutableLiveData<Boolean> = MutableLiveData(false)
    val changeColor : LiveData<Boolean> = _changeColor

    init {
        viewModelScope.launch(Dispatchers.Default + job1) {
            while (isActive) {
                delay(50L)
                if (lastTimeClick.get() != 0L  &&
                    ((System.currentTimeMillis() - lastTimeClick.get()) >= 900L))
                    state.set(true)
            }
        }

        viewModelScope.launch(Dispatchers.Default + job2) {
            while (isActive) {
                delay(50L)
                if (state.get()) {
                    if (_num.value!! > 0) {
                        decrease()
                    } else if (_num.value!! < 0) {
                        increase()
                    }
                    isChangeColer()
                }
            }
        }
    }
    fun isChangeColer(){
        if (Math.abs((num.value?.minus(lastNumberChangeColer))!!) > 100) {
            Log.e("tag","${lastNumberChangeColer}")
            lastNumberChangeColer = num.value!!
            _changeColor.postValue(true)
        } else{
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