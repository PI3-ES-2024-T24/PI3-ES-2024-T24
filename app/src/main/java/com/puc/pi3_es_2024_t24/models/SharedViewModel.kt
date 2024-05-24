package com.puc.pi3_es_2024_t24.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    private val _clientName = MutableLiveData<String>()
    private val _unityId = MutableLiveData<String>()
    private val _time = MutableLiveData<Number>()
    val clientName: LiveData<String> get() = _clientName
    val unityId : LiveData<String> get() = _unityId
    val time : LiveData<Number> get() = _time

    fun setClientName(name: String) {
        _clientName.value = name
    }

    fun setUnityId(id: String) {
        _unityId.value = id
    }

    fun setTime(time: Number) {
        _time.value = time
    }

}