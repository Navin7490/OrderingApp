package com.yorder.shop.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.hbb20.BuildConfig
import com.yorder.shop.R
import com.yorder.shop.databinding.FragmentHelpBinding

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class HelpFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    lateinit var viewbinding:FragmentHelpBinding
    var mailId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
          viewbinding = FragmentHelpBinding.inflate(inflater,container,false)
        mailId = requireArguments().getString("mailId", "") ?: ""
        viewbinding.appVersionTV.text = "App version: ${BuildConfig.VERSION_NAME}"

        val helpSupportStr = requireContext().resources.getString(R.string.help_support)
        val helpSupportStyled: Spanned = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            Html.fromHtml(helpSupportStr, Html.FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH)
        } else {
            Html.fromHtml(helpSupportStr)
        }

        ((requireActivity()) as AppCompatActivity).supportActionBar?.title = helpSupportStyled


        viewbinding.sendEmailButton.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_VIEW, Uri.parse("mailto:"))
            emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("info@nextsavy.com"))
            try { // the user can choose the email client
                startActivity(Intent.createChooser(emailIntent, "Choose an email client from..."))
            } catch (ex: ActivityNotFoundException) {
                Toast.makeText(activity, "No email client configured!", Toast.LENGTH_LONG).show()
            }
        }

        return viewbinding.root
    }

}