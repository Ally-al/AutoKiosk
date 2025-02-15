package com.example.autokiosk.presentation

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.autokiosk.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainContainerFragment : Fragment(R.layout.container_main) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = childFragmentManager.findFragmentById(R.id.fragment_container)?.findNavController()


        val bottomNav = view.findViewById<BottomNavigationView>(R.id.bottom_navigation)
        navController?.let { bottomNav.setupWithNavController(it) }

    }
}
