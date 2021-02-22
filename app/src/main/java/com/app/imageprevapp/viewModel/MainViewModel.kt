package com.app.imageprevapp.viewModel

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.imageprevapp.utils.ImageState

private const val TAG = "MainViewModel"
class MainViewModel : ViewModel() {


    init {
        Log.d(TAG, ": viewModelInit....)")
    }

    val editBitMapLive = MutableLiveData<Bitmap>()
    val viewBitMap = MutableLiveData<Bitmap>()

    val editState = MutableLiveData<ImageState>()

}