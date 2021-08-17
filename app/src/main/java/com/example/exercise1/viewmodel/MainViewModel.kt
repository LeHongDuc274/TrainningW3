package com.example.exercise1.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*

class MainViewModel : ViewModel() {
    private val job1 = Job()
    private val job2 = Job()
    private val _num: MutableLiveData<Int> = MutableLiveData(0)
    val num: LiveData<Int> = _num

    var state: Boolean = false
    var lastTimeClick: Long? = null

    init {
        viewModelScope.launch(Dispatchers.Default + job1) {
            while (isActive) {
                delay(100)
                if (lastTimeClick != null && ((System.currentTimeMillis() - lastTimeClick!!) >= 900))
                    state = true
            }
        }

        viewModelScope.launch(Dispatchers.Default + job2) {
            while (isActive) {
                delay(50L)
                if (state) {
                    if (_num.value!! > 0) {
                        decrease()
                    } else if (_num.value!! < 0) {
                        increase()
                    }
                    // delay(100)
                }
            }
        }
    }

    fun increase() {
        _num.postValue(_num.value?.plus(1))
    }

    fun decrease() {
        _num.postValue(_num.value?.minus(1))
    }


}