package com.app.imageprevapp.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.Image
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.app.imageprevapp.R
import com.app.imageprevapp.databinding.FragmentCameraBinding
import com.app.imageprevapp.databinding.FragmentHomeBinding
import com.app.imageprevapp.utils.onBackPressedCall
import com.app.imageprevapp.viewModel.MainViewModel
import com.google.android.material.button.MaterialButton


class CameraFragment : Fragment() {

    private val CAMERAREQUEST = 100
    private lateinit var preview: Preview
    private lateinit var imageCapture: ImageCapture
    private lateinit var cameraBinding: FragmentCameraBinding
    private lateinit var viewModel: MainViewModel
    private val cameraExecutor by lazy { ContextCompat.getMainExecutor(requireContext()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        cameraBinding = FragmentCameraBinding.inflate(inflater)
        return cameraBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        requireActivity().onBackPressedCall(
            R.id.action_cameraFragment_to_homeFragment,
            findNavController()
        )


        checkForPermission()
        cameraBinding.captureImgButton.setOnClickListener {
            captureImage()
        }
    }

    private fun checkForPermission() {
        if (requireActivity().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), CAMERAREQUEST)
        } else {
            startCamera()
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        val cameraSelector =
            CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_FRONT).build()

        cameraProviderFuture.addListener({
            preview = Preview.Builder().apply {
                setTargetAspectRatio(AspectRatio.RATIO_16_9)
                setTargetRotation(cameraBinding.cameraPrevView.display.rotation)
            }.build()

            imageCapture = ImageCapture.Builder().apply {
                setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                setFlashMode(ImageCapture.FLASH_MODE_AUTO)
                setTargetAspectRatio(AspectRatio.RATIO_16_9)
            }.build()

            val provider = cameraProviderFuture.get()
            provider.bindToLifecycle(viewLifecycleOwner, cameraSelector, preview, imageCapture)
            cameraBinding.cameraPrevView.implementationMode =
                PreviewView.ImplementationMode.COMPATIBLE
            preview.setSurfaceProvider(cameraBinding.cameraPrevView.surfaceProvider)
        }, cameraExecutor)
    }

    private fun captureImage() {
        imageCapture.takePicture(cameraExecutor, object : ImageCapture.OnImageCapturedCallback() {
            @SuppressLint("UnsafeExperimentalUsageError")
            override fun onCaptureSuccess(image: ImageProxy) {
                val degree = image.imageInfo.rotationDegrees
                val bitmap = image.image?.toBitmap(degree)
                image.close()
                viewModel.editBitMapLive.value = bitmap
                findNavController().navigate(R.id.action_cameraFragment_to_editFragment)
                super.onCaptureSuccess(image)
            }

            override fun onError(exception: ImageCaptureException) {
                super.onError(exception)
                Log.d("ERROR", "onError: ")
            }
        })

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            findNavController().navigate(R.id.action_cameraFragment_to_homeFragment)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERAREQUEST) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(requireContext(), "Camera Permission Granted!!", Toast.LENGTH_SHORT)
                    .show()
                startCamera()
            } else {
                Toast.makeText(requireContext(), "Camera Permission Denied!!", Toast.LENGTH_SHORT)
                    .show()
                findNavController().navigate(R.id.action_cameraFragment_to_homeFragment)
            }

        }
    }


}



fun Image.toBitmap(rotationDegree :Int): Bitmap {
    val buffer = planes[0].buffer
    val bytes = ByteArray(buffer.remaining())
    buffer.get(bytes)
    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size, null)
    val matrix = Matrix()
    matrix.postRotate(rotationDegree.toFloat())
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}