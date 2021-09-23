package com.yorder.shop.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.yorder.shop.R
import com.yorder.shop.databinding.FragmentSplashBinding

class SplashFragment : Fragment() {
    private lateinit var binding: FragmentSplashBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSplashBinding.inflate(inflater, container, false)
        Log.e("NST", "Vijay Cross Raod: ${GeoFireUtils.getGeoHashForLocation(GeoLocation(23.0426847, 72.5469402))}")

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        if (Firebase.auth.currentUser != null){
            Handler(Looper.getMainLooper()).postDelayed({
                findNavController().navigate(R.id.action_splashFragment_to_sellersFragment)
            }, 3000)
        }

    }
}