package com.yorder.shop.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.yorder.shop.R
import kotlinx.android.synthetic.main.fragment_privacy.view.*

class PrivacyFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

       val viewBinding = inflater.inflate(R.layout.fragment_privacy, container, false)
        viewBinding.webView.loadUrl("https://www.nextsavy.com/privacy-policy/")
        viewBinding.webView.settings.javaScriptEnabled = true
        viewBinding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        return viewBinding
    }


}