package com.app.imageprevapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.NavHostFragment
import com.app.imageprevapp.databinding.ActivityMainBinding
import com.google.android.material.appbar.MaterialToolbar

class MainActivity : AppCompatActivity() {

    private lateinit var activityBinding :ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityBinding.root)

        activityBinding.toolbar.apply { title = "Image App" }.run { setSupportActionBar(this) }

        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close_24)

        val navHost = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        navHost.navController.addOnDestinationChangedListener { _, destination, _ ->
            activityBinding.toolbar.apply {
                title = when (destination.id) {
                    R.id.cameraFragment -> {
                        visibility = View.VISIBLE
                        supportActionBar?.setDisplayHomeAsUpEnabled(true)
                        "Camera"
                    }
                    R.id.editFragment -> {
                        visibility = View.GONE
                        ""
                    }
                    else -> {
                        supportActionBar?.setDisplayHomeAsUpEnabled(false)
                        visibility = View.VISIBLE
                        "Image App"
                    }
                }
            }
        }
    }
}