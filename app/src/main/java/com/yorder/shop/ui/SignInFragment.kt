package com.yorder.shop.ui

import android.Manifest
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Paint
import android.graphics.Typeface
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.text.Html
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.hbb20.BuildConfig
import com.yorder.shop.R
import com.yorder.shop.apiService.APIService
import com.yorder.shop.const.Constant
import com.yorder.shop.databinding.FragmentSigninBinding
import com.yorder.shop.keyboard.HideKeyBoard
import com.yorder.shop.model.CollectionUser
import com.yorder.shop.model.NetworkUserModel
import com.yorder.shop.model.UserDTO
import com.yorder.shop.model.UserType
import com.yorder.shop.utils.Constants
import com.yorder.shop.utils.CustomEdge
import com.onesignal.OneSignal
import java.util.*


class SignInFragment : Fragment() {
    private lateinit var viewBinding: FragmentSigninBinding
    private var apiService: APIService? = null

    private val permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION

    )

    private val permissionRequestCode = 200
    private var hideKeyBoard: HideKeyBoard? = null

    // location
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    var locationManager: LocationManager? = null
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    var notificationTokenUser: String = ""
    private val deviceState = OneSignal.getDeviceState()
    var phon: String? = null
    lateinit var invalidUser:String
    private lateinit var invalidPassword:String
    lateinit var errorMessage:String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentSigninBinding.inflate(inflater, container, false)
       invalidUser="There is no user record corresponding to this identifier. The user may have been deleted."
        invalidPassword="The password is invalid or the user does not have a password."
        errorMessage ="An internal error has occurred. [ Unable to resolve host \"www.googleapis.com\":No address associated with hostname ]"

        Firebase.messaging.isAutoInitEnabled = true

        notificationTokenUser = deviceState?.userId.toString()
        val constant = context?.let { Constant(it) }

        constant?.setupID()
        hideKeyBoard = HideKeyBoard(requireContext())


        val loginStr = requireContext().resources.getString(R.string.login)
        val loginStyled: Spanned = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            Html.fromHtml(loginStr, Html.FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH)
        } else {
            Html.fromHtml(loginStr)
        }

        viewBinding.loginTV.text=loginStyled



        val forgotPasswordStr = requireContext().resources.getString(R.string.forgot_password)
        val forgotPasswordStyled: Spanned = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            Html.fromHtml(forgotPasswordStr, Html.FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH)
        } else {
            Html.fromHtml(forgotPasswordStr)
        }

        viewBinding.forgotPasswordTV.text=forgotPasswordStyled



        val loginWithOTPStr = requireContext().resources.getString(R.string.login_with_otp)
        val loginWithOtpStyled: Spanned = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            Html.fromHtml(loginWithOTPStr, Html.FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH)
        } else {
            Html.fromHtml(loginWithOTPStr)
        }




        val ss = SpannableString(loginWithOtpStyled)
        val boldSpan = StyleSpan(Typeface.BOLD)

        ss.setSpan(boldSpan, 12, 15, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        viewBinding.loginWithOtpGo.text=ss

        // app version
        viewBinding.tvAppVersion.text="App version: ${BuildConfig.VERSION_NAME}"


        viewBinding.forgotPasswordTV.setOnClickListener {
            findNavController().navigate(R.id.action_signInFragment_to_forgotPasswordFragment)
        }



        viewBinding.signUpTV.setOnClickListener {
            findNavController().navigate(R.id.action_signInFragment_to_registrationFragment)

        }


        viewBinding.loginWithOtpGo.setOnClickListener {
            findNavController().navigate(R.id.action_signInFragment_to_loginWithphoneFragment)

        }

        viewBinding.signInFAB.setOnClickListener {
            if (checkValidation()) {
                proceedSignIn()
            }
        }


        phon = arguments?.getString("phone")

        if (phon != null) {
            val bundle = Bundle()
            bundle.putString("phone", phon)
            bundle.putString("userId", Firebase.auth.currentUser?.uid)
            findNavController().navigate(R.id.action_signInFragment_to_verificationFragment, bundle)
        }

        // initilize

        setBottomEdge()


        return viewBinding.root
    }

    private fun setBottomEdge() {
        val arcRadius = Constants.getIntFromDp("95", requireContext()) / 2
        val sam = ShapeAppearanceModel.builder()
                .setAllCorners(CornerFamily.ROUNDED, 36f)
                .setBottomEdge(CustomEdge(arcRadius, 0))
                .build()
        val msd = MaterialShapeDrawable(sam)
        msd.setTint(ContextCompat.getColor(requireContext(), R.color.app_bg_white))
        msd.paintStyle = Paint.Style.FILL
        msd.elevation = 6f
        viewBinding.containerView.background = msd
    }


    override fun onStart() {
        super.onStart()
        locationManager = context?.getSystemService(LOCATION_SERVICE) as LocationManager?

        if (!locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            onGPS()
        } else {
           getGPS()
            //checkAndRequestPermission()

        }
    }


    private fun onGPS() {
        Constants.showAlertWithListeners(
                requireContext(),
                "",
                "Please enable GPS service from your Settings.",
                "Settings",
                { _, _ ->
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                },
                "",
                { dialog, _ ->
                    dialog.dismiss()
                }
        )
    }

    private fun checkValidation(): Boolean {
        var flag = true
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"

        if (viewBinding.emailTIL.editText!!.text.toString().trim().isNullOrBlank()) {
            viewBinding.emailTIL.error = "Required"
            flag = false
        } else if (!viewBinding.emailTIL.editText!!.text.toString().trim().matches(
                Regex(
                    emailPattern
                )
            )) {
            viewBinding.emailTIL.error = "Required valid email"
            flag = false
        } else {
            viewBinding.emailTIL.error = null
        }

        if (viewBinding.passwordTIL.editText!!.text.toString().trim().isNullOrBlank()) {
            viewBinding.passwordTIL.error = "Required"
            flag = false
        } else if (viewBinding.passwordTIL.editText!!.text.toString().trim().length<6) {
            viewBinding.passwordTIL.error = "Required valid password"
            flag = false
        } else {
            viewBinding.passwordTIL.error = null
        }

        return flag
    }



    private fun proceedSignIn() {
        val email: String = viewBinding.emailTIL.editText!!.text.toString().trim()
        val password: String = viewBinding.passwordTIL.editText!!.text.toString().trim()

        viewBinding.progressBar.visibility = View.VISIBLE

        hideKeyBoard!!.isKeyboardHide(viewBinding.passwordTIL.editText!!)
        hideKeyBoard!!.isKeyboardHide(viewBinding.passwordTIL.editText!!)

        Firebase.auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Firebase.firestore
                        .collection(CollectionUser.name)
                        .document(Firebase.auth.currentUser!!.uid)
                        .get()
                        .addOnSuccessListener { docSnap ->
                            val networkUser = docSnap.toObject(NetworkUserModel::class.java)
                            if (networkUser != null) {
                                val userDTO = UserDTO.create(networkUser)
                                if (userDTO != null) {
                                    if (userDTO.userType == UserType.CUSTOMER) {
                                        upDateDeviceId(
                                            Firebase.auth.currentUser!!.uid,
                                            notificationTokenUser
                                        )
                                        findNavController().navigate(R.id.action_global_sign_in)
                                    } else {
                                        Toast.makeText(
                                            requireContext(),
                                            "This credentials does not belong to Customer",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        Firebase.auth.signOut()
                                    }
                                } else {
                                    Toast.makeText(
                                        requireContext(),
                                        "Please fill user details in console",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    Firebase.auth.signOut()
                                }
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "Please fill user details in console",
                                    Toast.LENGTH_LONG
                                ).show()
                                Firebase.auth.signOut()
                            }
                        }
                        .addOnFailureListener { exception ->
                            Toast.makeText(
                                requireContext(), exception.message
                                    ?: "Unable to get user details", Toast.LENGTH_LONG
                            ).show()
                        }

                    viewBinding.progressBar.visibility = View.GONE

                    // Manthan 7-4-2021
                    // Update Notification Token in DB
                    /*val uid = Firebase.auth.currentUser!!.uid
                    val deviceState = OneSignal.getDeviceState()
                    val notificationToken = deviceState?.userId
                    if (notificationToken != null) {
                        upDateDeviceId(uid, notificationToken)
                    } else {
                        Toast.makeText(requireContext(), "Try again", Toast.LENGTH_LONG)
                                .show()
                    }*/
                } else {
                    viewBinding.progressBar.visibility  = View.GONE
                    Log.e("message", "${task.exception?.message}")
                    if (task.exception?.message ==invalidUser){
                        viewBinding.emailTIL.error="Invalid email"
                        viewBinding.passwordTIL.error="Invalid password"


                    }else if (task.exception?.message ==invalidPassword){
                        viewBinding.passwordTIL.error="Password is wrong"


                    } else if (task.exception?.message ==errorMessage){

                        Toast.makeText(requireContext(),"Please check internet connection!",Toast.LENGTH_SHORT).show()

                    }

                }
            }
    }



    private fun upDateDeviceId(userId: String, notificationToken: String) {
        val db = Firebase.firestore
        val updateValue = db.collection(CollectionUser.name).document(userId)
        val update = hashMapOf(
            CollectionUser.kNotificationToken to notificationToken,
            CollectionUser.kUserGeoLocation to GeoPoint(latitude, longitude)
        )
        updateValue.update(update as Map<String, Any>)
            .addOnSuccessListener {
                Log.e("upDateDeviceId", "upDated")
            }
            .addOnFailureListener { e ->
                Log.e("upDateDeviceId", "${e.message}")
            }
    }




    private fun getGPS() {

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(requireActivity(), permissions, permissionRequestCode)
        } else {

//            //getGPS()
            fusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(requireActivity())

            fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
                val location = task.result
                if (location != null) {

                    // initialize geocoder

                        try {
                            val geoCoder = Geocoder(context, Locale.getDefault())
                            val address: List<Address> =
                                    geoCoder.getFromLocation(location.latitude, location.longitude, 1)

                            latitude = address[0].latitude
                            longitude = address[0].longitude

                            Log.e(
                                "location",
                                "lat = $latitude log = $longitude locality =${address[0].locality}"
                            )

                        }catch (e:Exception){
                            e.printStackTrace()
                        }


                }
            }


        }
    }
    companion object {
        const val ONE_SIGNAL_APP_ID = "6e16afc4-052c-4fcf-ae4f-ab6dce00cf78"
    }
}
