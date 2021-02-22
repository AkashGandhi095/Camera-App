package com.app.imageprevapp.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.core.view.setPadding
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.app.imageprevapp.R
import com.app.imageprevapp.databinding.FragmentEditBinding
import com.app.imageprevapp.utils.ImageState
import com.app.imageprevapp.utils.onBackPressedCall
import com.app.imageprevapp.utils.saveToStorage
import com.app.imageprevapp.viewModel.MainViewModel
import com.google.android.material.appbar.AppBarLayout
import com.theartofdev.edmodo.cropper.CropImageView


class EditFragment : Fragment() {
    companion object {
        private const val TAG = "EditFragmentScreen"
    }
    private val WRITECODE = 200
    private lateinit var editBinding: FragmentEditBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var editBitmap: Bitmap


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        editBinding = FragmentEditBinding.inflate(inflater)
        return editBinding.root
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedCall(R.id.action_editFragment_to_homeFragment , findNavController())


        viewModel.editBitMapLive.observe(viewLifecycleOwner, { bitmap ->
            Log.d(TAG, "onViewCreated: $bitmap")
            editBitmap = bitmap
            viewModel.editState.value = ImageState.ViewState
        })

        viewModel.editState.observe(viewLifecycleOwner, { imageState ->
            Log.d(TAG, "onViewCreated: editStateObserver")
            when (imageState) {
                is ImageState.ViewState -> {
                    // for viewOnly
                    editBinding.cropImageView.visibility = View.GONE
                    editBinding.imageView.apply {
                        visibility = View.VISIBLE
                        setImageBitmap(editBitmap)
                    }
                    editBinding.topAction.saveView.visibility = View.GONE

                }
                is ImageState.EditState -> {
                    // for editing
                    editBinding.imageView.visibility = View.GONE
                    editBinding.cropImageView.apply {
                        visibility = View.VISIBLE
                        setImageBitmap(editBitmap)
                    }
                    editBinding.topAction.saveView.visibility = View.VISIBLE




                }
            }
        })

        editBinding.actionView.cropButton.setOnClickListener {
            if (viewModel.editState.value != ImageState.EditState)
                viewModel.editState.value = ImageState.EditState
        }

        editBinding.actionView.rotateButton.setOnClickListener {
            if (viewModel.editState.value != ImageState.EditState)
                viewModel.editState.value = ImageState.EditState

            editBinding.cropImageView.rotateImage(90)

        }

        editBinding.topAction.undoView.setOnClickListener {
            editBinding.cropImageView.resetCropRect()
        }

        editBinding.topAction.saveView.setOnClickListener {

            editBinding.appBar.visibility = View.GONE
            val bitmap = editBinding.cropImageView.croppedImage

            checkForStoragePermission(bitmap)
            viewModel.viewBitMap.value = bitmap
        }

        editBinding.topAction.homeView.setOnClickListener {
            editBinding.appBar.visibility = View.GONE
            it.findNavController().navigate(R.id.action_editFragment_to_homeFragment)
        }

    }

    private fun checkForStoragePermission(bitmap: Bitmap) {
        if (requireActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE) ,WRITECODE )
        }
        else {
            bitmap.saveToStorage(requireContext())
            findNavController().navigate(R.id.action_editFragment_to_homeFragment)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == WRITECODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                editBinding.cropImageView.croppedImage.saveToStorage(requireContext())
                findNavController().navigate(R.id.action_editFragment_to_homeFragment)
                Toast.makeText(requireContext() , "Storage Permission Granted!!" , Toast.LENGTH_SHORT).show()
            }
            else {
                Toast.makeText(requireContext() , "Storage Permission Denied!!" , Toast.LENGTH_SHORT).show()

            }
        }
    }

}