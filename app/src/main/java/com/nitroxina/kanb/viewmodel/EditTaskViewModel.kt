package com.nitroxina.kanb.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nitroxina.kanb.model.Task

class EditTaskViewModel : ViewModel() {
    var dataTask: MutableLiveData<Task> = MutableLiveData()
}