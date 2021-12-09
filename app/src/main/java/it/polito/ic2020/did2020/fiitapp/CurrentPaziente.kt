package it.polito.ic2020.did2020.fiitapp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CurrentPaziente: ViewModel(){

    private val currentId = MutableLiveData<String>()

    fun currentId(id: String) {
        currentId.value = id
    }

    fun getId(): String {
        return currentId.value ?: "null"
    }
}