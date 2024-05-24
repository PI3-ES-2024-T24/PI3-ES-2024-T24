package com.puc.pi3_es_2024_t24.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    private val _clientName = MutableLiveData<String>()
    val clientName: LiveData<String> get() = _clientName

    fun setClientName(name: String) {
        _clientName.value = name
    }
}