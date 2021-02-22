package com.app.imageprevapp.utils

sealed class ImageState {
    object EditState :ImageState()
    object ViewState :ImageState()
}
