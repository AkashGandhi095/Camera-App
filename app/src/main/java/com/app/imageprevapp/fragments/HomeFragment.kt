package com.app.imageprevapp.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.app.imageprevapp.R
import com.app.imageprevapp.databinding.FragmentHomeBinding
import com.app.imageprevapp.utils.onBackPressedCall
import com.app.imageprevapp.viewModel.MainViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import java.io.ByteArrayOutputStream


class HomeFragment : Fragment() {

    private lateinit var viewModel: MainViewModel
    private lateinit var homeBinding :FragmentHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[MainViewModel ::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        homeBinding = FragmentHomeBinding.inflate(inflater)
        return homeBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedCall()

        homeBinding.include.openCamera.setOnClickListener {
            it.findNavController().navigate(R.id.action_homeFragment_to_cameraFragment)
        }

        viewModel.viewBitMap.observe(viewLifecycleOwner , { mBitmap ->
            homeBinding.imgPrev.setImageBitmap(mBitmap)
        })


        homeBinding.include.fromGallery.setOnClickListener {
            getImageFromGallery()
        }
    }

    private fun getImageFromGallery() {
        val gallery =  Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(gallery, 101)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101 && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                Toast.makeText(requireContext() , "Something Went Wrong!!" , Toast.LENGTH_SHORT).show()
                return
            }
            val inputStream = data.data?.let { requireContext().contentResolver.openInputStream(it) }
            val bitmap = BitmapFactory.decodeStream(inputStream)
            viewModel.editBitMapLive.value = bitmap
            findNavController().navigate(R.id.action_homeFragment_to_editFragment)
        }
    }

}