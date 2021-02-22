package com.app.imageprevapp.utils

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.core.content.contentValuesOf
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import java.io.File
import java.io.File.separator
import java.io.FileOutputStream
import java.io.OutputStream
import kotlin.random.Random

fun Bitmap.saveToStorage(context: Context) {
    val fileName = "myImage${Random.nextInt(1000)}.jpeg"

    var outStream :OutputStream? = null

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        context.contentResolver?.also { resolver ->
            val values = contentValuesOf().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            }
            val imgUri :Uri? =  resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            outStream = imgUri?.let { resolver.openOutputStream(it) }
        }
    } else {
        val root = Environment.getExternalStorageDirectory().absolutePath.toString()
        val file = File(root, fileName);
        if (file.exists()) file.delete();
        outStream = FileOutputStream(file)
    }




    outStream?.use {
        this.compress(Bitmap.CompressFormat.JPEG,100 , it)
        Log.d("SaveToGallery", "saveToStorage: success!!")
        Toast.makeText(context , "Image Saved!!" , Toast.LENGTH_SHORT).show()
        it.close()
        it.flush()
    }

}


fun FragmentActivity.onBackPressedCall(id :Int , navController: NavController) {
    val backPressCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            navController.navigate(id)
        }
    }

    this.onBackPressedDispatcher.addCallback(backPressCallback)
}

fun FragmentActivity.onBackPressedCall() {
    val backPressCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            finish()
        }
    }

    this.onBackPressedDispatcher.addCallback(backPressCallback)
}