package com.yorder.shop.ui

import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.hbb20.BuildConfig
import com.yorder.shop.R
import com.yorder.shop.databinding.FragmentLoginWithphoneBinding
import com.yorder.shop.model.CollectionUser
import com.yorder.shop.utils.Constants
import com.yorder.shop.utils.CustomEdge
import com.yorder.shop.utils.UserType


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class LoginWithphoneFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    lateinit var viewbinding:FragmentLoginWithphoneBinding


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
        viewbinding = FragmentLoginWithphoneBinding.inflate(inflater,container,false)

        viewbinding.getOTPButton.setOnClickListener {
          val phone=  viewbinding.phoneTIL.editText?.text.toString().trim()
            if (phone.isEmpty()||phone.length<10 ||phone.length>10 ){
                viewbinding.phoneTIL.error="Required valid phone number"
            }else{
                viewbinding.phoneTIL.error=null

                getPhone("+91$phone")
            }
        }
        setBottomEdge()

        viewbinding.tvAppVersion.text="App version: ${BuildConfig.VERSION_NAME}"
        return viewbinding.root
    }


    private fun getPhone(phone:String){
        Log.e("Phone","${phone}")

        val db=  Firebase.firestore
        viewbinding.progressBar.visibility=View.VISIBLE
        db.collection(CollectionUser.name)
            .whereEqualTo(CollectionUser.kPhoneNumber,phone)
            .whereEqualTo(CollectionUser.kUserType,UserType.CUSTOMER.value)
            .get()
            .addOnSuccessListener {document->


               val number= viewbinding.phoneTIL.editText?.text.toString().trim()
                viewbinding.progressBar.visibility=View.GONE

                if (document.documents.isNotEmpty()){
                    val bundle=Bundle()
                    bundle.putString("phone",number)
                    findNavController().navigate(R.id.action_loginWithphoneFragment_to_verificationFragment,bundle)
                }else{
                    viewbinding.phoneTIL.error="Enter your register phone number"


                }




            }
            .addOnFailureListener {

            }
    }

    private fun setBottomEdge() {
        val arcRadius = Constants.getIntFromDp("95", requireContext()) / 2

        val sam = ShapeAppearanceModel.builder()
            .setAllCorners(CornerFamily.ROUNDED, 36f)
            .setBottomEdge(CustomEdge(arcRadius, 0))
            .build()
        val msd = MaterialShapeDrawable(sam)
        msd.setTint(ContextCompat.getColor(requireContext(), R.color.app_text_white))
        msd.paintStyle = Paint.Style.FILL
        msd.elevation = 6f
        viewbinding.containerView.background = msd
    }
    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LoginWithphoneFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}