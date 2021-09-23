package com.yorder.shop.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Paint
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Html
import android.text.Spanned
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.hbb20.CountryCodePicker
import com.hbb20.BuildConfig
import com.yorder.shop.R
import com.yorder.shop.databinding.FragmentVerificationBinding
import com.yorder.shop.keyboard.HideKeyBoard
import com.yorder.shop.model.CollectionUser
import com.yorder.shop.model.LoginDetail
import com.yorder.shop.utils.Constants
import com.yorder.shop.utils.CustomEdge
import com.onesignal.OneSignal
import java.util.concurrent.TimeUnit


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class VerificationFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    lateinit var viewBinding: FragmentVerificationBinding
    private var verifyIdForRegistration: String? = null



    lateinit var auth: FirebaseAuth


    private var phone: String? = null
    private var firstName: String? = null
    private var lastName: String? = null
    private var email: String? = null
    private var password: String? = null
    private var image: String? = null
    private var userId: String= "null"
    private var uId: String? = null
    private val db = Firebase.firestore

    // dialog

    private var loginDetail: LoginDetail? = null
    private var phoneExist: String? = null
    private var phoneExistR: String? = null
    private var phoneOTPExpired: String? = null
    private var otpExpired: String? = null

    private var wrongOtp: String? = null
    private var resendOTPToken: PhoneAuthProvider.ForceResendingToken? = null
    private var smsCode: String? = null
    private val firebaseAuth = Firebase.auth
    private var waitTimer: CountDownTimer? = null

    private var navController: NavController? = null

    private var userDeleted: String? = null


    private var hideKeyBoard: HideKeyBoard? = null
    lateinit var resendOtpStyled:Spanned

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        userId = arguments?.getString("userId").toString()
        firstName = arguments?.getString("firstName").toString()
        lastName = arguments?.getString("lastName").toString()
        email = arguments?.getString("email").toString()
        phone = arguments?.getString("phone").toString()
        password = arguments?.getString("password").toString()
        image = arguments?.getString("image").toString()


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

    }

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // View Binding
        viewBinding = FragmentVerificationBinding.inflate(inflater, container, false)
        verifyIdForRegistration=null
        resendOTPToken = null
      setBottomEdge()
        //etOtp = view.findViewById(R.id.tvOTP)
       // btnVerify = view.findViewById(R.id.btnVerifyOTP)
//        tvOTPSentTo = view.findViewById(R.id.tvVPhone)
//        tvPhoneEdit = view.findViewById(R.id.tvVPhoneEdit)

       // tvResendOTP = view.findViewById(R.id.tvVResendOTP)
       // tvTimer = view.findViewById(R.id.tvVTimer)
       // snackbar = view.findViewById(R.id.layoutRelativeVerify)


        // dialog
        val resendOtpStr = requireContext().resources.getString(R.string.resend_otp)
        resendOtpStyled = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            Html.fromHtml(resendOtpStr, Html.FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH)
        } else {
            Html.fromHtml(resendOtpStr)
        }

        viewBinding.tvAppVersion.text="App version: ${BuildConfig.VERSION_NAME}"

        sendVerificationCodeForRegistration(phone!!)

        userDeleted =
            "There is no user record corresponding to this identifier. The user may have been deleted."

        phoneExist = "This credential is already associated with a different user account."
        phoneExistR = "User has already been linked to the given provider."

        wrongOtp =
            "The sms verification code used to create the phone auth credential is invalid. Please resend the verification code sms and be sure use the verification code provided by the user."
        otpExpired = "The sms code has expired. Please re-send the verification code to try again."


        loginDetail = LoginDetail(requireContext())
        hideKeyBoard = HideKeyBoard(requireContext())


        val firebaseAuthSettings = firebaseAuth.firebaseAuthSettings
        // Configure faking the auto-retrieval with the whitelisted numbers.
        firebaseAuthSettings.setAutoRetrievedSmsCodeForPhoneNumber(phone, smsCode)



        auth = FirebaseAuth.getInstance()

      viewBinding.btnVerifyOTP.setOnClickListener {
            Log.e("onClick on verifyButton", "verify clicked")
            //val etOTp = etOtp!!.text.toString().trim()
         val etOTp=  viewBinding.otpTIL.editText?.text.toString().trim()

            Log.e("OTP", "$")

            if (etOTp.isEmpty()) {
                viewBinding.otpTIL.error = "Required OTP"

            } else if (etOTp.length < 6) {
                viewBinding.otpTIL.error = "Please Enter 6 Digit OTP"

            } else if (verifyIdForRegistration != null) {
                viewBinding.otpTIL.error =null

                val credential: PhoneAuthCredential =
                    PhoneAuthProvider.getCredential(verifyIdForRegistration!!, etOTp)
                if (userId == "null") {

                    // when come to login screen , call this function
                    hideKeyBoard!!.isKeyboardHide(viewBinding.otpTIL.editText!!)
                    signInWithPhoneAuthCredential(credential)

                } else {
                    // when come to Registration OR Profile screen , call this function

                    // link phone number
                    viewBinding.btnVerifyOTP.isEnabled = false
                    hideKeyBoard!!.isKeyboardHide(viewBinding.otpTIL.editText!!)

                    auth.currentUser?.linkWithCredential(credential)

                        ?.addOnCompleteListener { task ->

                            if (task.isSuccessful) {
                                Log.e("mobile", "Success")

                                val deviceState = OneSignal.getDeviceState()
                                val notificationToken = deviceState?.userId
                                Log.e("deviceID", "$notificationToken")


                                upDatePhone(phone!!, userId, notificationToken!!)
                               // getData(userId!!)

                            }

                        }
                        ?.addOnFailureListener { e ->

                            Log.e("mobile error", "${e.message}")
                            val otp = e.message
                            // when phone number is exist
                            if (otp == phoneExist || otp == phoneExistR) {

                                Toast.makeText(context,"Phone number is exist please try to other phone number",Toast.LENGTH_LONG).show()



                              viewBinding.tvResendButton.visibility = View.GONE


                            } else if (otp == userDeleted) {
                                Toast.makeText(context,"Your account have been deleted please create new account",Toast.LENGTH_LONG).show()



                            } else {

                                //Toast.makeText(context,"OTP is wrong please enter valid OTP",Toast.LENGTH_LONG).show()
                                viewBinding.otpTIL.error="Required valid OTP"
                                viewBinding.btnVerifyOTP.isEnabled = true

                            }


                            viewBinding.progressBar.visibility = View.GONE

                        }


                }

            }

        }
        return viewBinding.root
    }


    // start resend verification code

    private fun reSendVerificationCode(
        phone: String,
        resendingToken: PhoneAuthProvider.ForceResendingToken
    ) {
        viewBinding.progressBar.visibility = View.VISIBLE

        val option = PhoneAuthOptions.newBuilder(Firebase.auth)
            .setPhoneNumber("+91$phone")
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setCallbacks(mCallback)
            .setForceResendingToken(resendingToken)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(option)
    }
    // end resend verification code


    // start sendVerificationCodeForRegistration
    private fun sendVerificationCodeForRegistration(phone: String) {
        Log.e("sendVerificationCode", "sendVerificationCodeForRegistration")
        viewBinding.progressBar.visibility = View.VISIBLE


        val option = PhoneAuthOptions.newBuilder(Firebase.auth)
            .setPhoneNumber("+91$phone")
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setCallbacks(mCallback)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(option)

    }


    private var mCallback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {




        @SuppressLint("SetTextI18n")
        override fun onCodeSent(
            s: String,
            forceResendingToken: PhoneAuthProvider.ForceResendingToken
        ) {
            super.onCodeSent(s, forceResendingToken)
            Log.e("sendVerificationCode", "onCodeSent")


            viewBinding.progressBar.visibility = View.GONE

            //storing the verification id that is sent to the user
            verifyIdForRegistration = s
            resendOTPToken = forceResendingToken

           viewBinding.tvOTPSendOn.text = "OTP sent to ${phone!!}"
//
            viewBinding.tvOTPSendOn.visibility = View.VISIBLE
            viewBinding.btnVerifyOTP.isEnabled = true
            viewBinding.tvTimer.visibility = View.VISIBLE
            viewBinding.tvResendButton.visibility = View.GONE


            // start Resend OTP  time start
            waitTimer = object : CountDownTimer(60000, 1000) {
                @SuppressLint("SetTextI18n")
                override fun onTick(millisUntilFinished: Long) {

                    viewBinding.tvTimer.text = "resend in " + millisUntilFinished / 1000 +"s."

                }

                // end Resend OTP  time start

                // start Resend OTP  time finish

                override fun onFinish() {

                   if (userId == "null") {
                        viewBinding.tvTimer.visibility = View.INVISIBLE
                        viewBinding.tvResendButton.visibility = View.VISIBLE
                       viewBinding.tvResendButton.text=resendOtpStyled
                        viewBinding.btnVerifyOTP.isEnabled = false
                        viewBinding.otpTIL.editText!!.text = null

//
                   } else {
                       viewBinding.tvTimer.visibility = View.INVISIBLE
                       viewBinding.tvResendButton.visibility = View.VISIBLE
                       viewBinding.tvResendButton.text=resendOtpStyled
                       viewBinding.btnVerifyOTP.isEnabled = false
                       viewBinding.otpTIL.editText!!.text = null
//
//
                   }
                    // click event on resend OTP
                   viewBinding.tvResendButton.setOnClickListener {

//                        tvOTPSentTo!!.visibility = View.GONE
                        viewBinding.btnVerifyOTP.isEnabled = false
                       viewBinding.tvTimer.visibility = View.INVISIBLE
                       viewBinding.tvResendButton.visibility = View.INVISIBLE
                       viewBinding.otpTIL.editText!!.text = null

                        reSendVerificationCode(phone!!, resendOTPToken!!)
                        //sendVerificationCodeForRegistration(phone!!)


                    }


                }

                // end Resend OTP  time finish

            }.start()


        }

        @RequiresApi(Build.VERSION_CODES.M)
        override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
            Log.e("sendVerificationCode", "onVerificationCompleted")
            viewBinding.otpTIL.editText!!.setText(phoneAuthCredential.smsCode)


            if (userId == "null") {
                // when come to login screen , call this function
                hideKeyBoard!!.isKeyboardHide(viewBinding.otpTIL.editText!!)
                signInWithPhoneAuthCredential(phoneAuthCredential)

            } else {
                // when come to Registration OR Profile screen , call this function

                // link phone number
                viewBinding.btnVerifyOTP.isEnabled  = false
                hideKeyBoard!!.isKeyboardHide(viewBinding.otpTIL.editText!!)


                auth.currentUser?.linkWithCredential(phoneAuthCredential)

                    ?.addOnCompleteListener { task ->
                        viewBinding.btnVerifyOTP.isEnabled  = false

                        if (task.isSuccessful) {
                            Log.e("mobile", "Success")
                            val deviceState = OneSignal.getDeviceState()
                            val notificationToken = deviceState?.userId
                            Log.e("deviceID", "$notificationToken")

                            if (notificationToken != null) {
                                upDatePhone(phone!!, userId, notificationToken)
                               // getData(userId!!)

                            } else {
                                Toast.makeText(requireContext(), "Try again", Toast.LENGTH_LONG)
                                    .show()
                            }

                        }

                    }
                    ?.addOnFailureListener { e ->
                        viewBinding.btnVerifyOTP.isEnabled  = true

                        Log.e("mobile error", "${e.message}")
                        val otp = e.message
                        // when phone number is exist
                        if (otp == phoneExist || otp == phoneExistR) {

                           Toast.makeText(context,"Phone number is exist please try to other phone number",Toast.LENGTH_SHORT).show()

                            viewBinding.tvResendButton.visibility = View.GONE


                        } else if (otp == userDeleted) {
                            Toast.makeText(context,"Your account have been deleted please create new account",Toast.LENGTH_SHORT).show()



                        } else {


                           // Toast.makeText(context,"OTP is wrong please enter valid OTP",Toast.LENGTH_SHORT).show()
                            viewBinding.otpTIL.error="Required valid OTP"
                            viewBinding.btnVerifyOTP.isEnabled = true



                        }

                        // Toast.makeText(requireContext(),"OTP wrong OR Phone number exist",Toast.LENGTH_LONG).show()

                        viewBinding.progressBar.visibility = View.GONE

                    }


            }


        }

        override fun onVerificationFailed(e: FirebaseException) {
            Log.e("sendVerificationCode", "onVerificationFailed")

            viewBinding.progressBar.visibility = View.GONE

            context.let {
                Toast.makeText(
                    it,
                    "${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }


        }


    }


    // end sendVerificationCodeForRegistration


    // start signIn with phone number
    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("LongLogTag")
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        Log.e("signInWithPhoneAuthCredential", "signInWithPhoneAuthCredential")
        viewBinding.progressBar.visibility = View.VISIBLE
        viewBinding.btnVerifyOTP.isEnabled = false

        auth.signInWithCredential(credential)
            .addOnSuccessListener { result ->
                viewBinding.btnVerifyOTP.isEnabled  = false

                val uid = result.user?.uid
                viewBinding.progressBar.visibility = View.GONE


                if (uid != null) {
                    val deviceState = OneSignal.getDeviceState()
                    val notificationToken = deviceState?.userId.toString()
                    Log.e("deviceID", "$notificationToken")

                    upDatePhone(phone!!, uid, notificationToken)
                   // getData(uid)

                }


            }
            .addOnFailureListener { e ->
                viewBinding.progressBar.visibility = View.GONE
                viewBinding.btnVerifyOTP.isEnabled  = true

                val message = e.message
                if (message == otpExpired) {


                   // Toast.makeText(context,"OTP is invalid please resend OTP",Toast.LENGTH_SHORT).show()
                    viewBinding.otpTIL.error="Required valid OTP"
                    viewBinding.btnVerifyOTP.isEnabled = true


                } else {

                  // Toast.makeText(context,"OTP is wrong please enter valid OTP",Toast.LENGTH_SHORT).show()
                    viewBinding.otpTIL.error="Required valid OTP"
                    viewBinding.btnVerifyOTP.isEnabled = true


                }
                Log.e("signInWithPhoneAuthCredential", " error=${e.message}")


            }

    }

    // end signIn with phone number


    // start upDate Number by Id
    private fun upDatePhone(phone: String, uid: String, notificationToken: String) {

        val db = Firebase.firestore
        val updateValues = db.collection(CollectionUser.name).document(uid)

        val update = hashMapOf(
            CollectionUser.kPhoneNumber to "+91$phone",
            CollectionUser.kPhoneVerified to true,
            CollectionUser.kNotificationToken to notificationToken
        )

        updateValues.update(update as Map<String, Any>)
            .addOnCompleteListener {
                if (it.isSuccessful){
                    findNavController().navigate(R.id.action_global_sign_in)

                }

                Log.e("verificationFragment", "upDatePhone")

                // Toast.makeText(requireContext(), "Update", Toast.LENGTH_LONG).show()

            }
            .addOnFailureListener { e ->
                // Toast.makeText(context, "${e.message}:update error", Toast.LENGTH_LONG)
                //  .show()
                Log.e("error", "${e.message}")

            }
    }
    // end upDate Phone Number



    private fun getNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel: NotificationChannel = NotificationChannel(
                "MyNotification", "MyNotification", NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager: NotificationManager =
                requireActivity().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            manager.createNotificationChannel(channel)
        }



        FirebaseMessaging.getInstance().subscribeToTopic("general")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    Log.e("message", "success")
                }
            }
            .addOnFailureListener { e ->
                Log.e("message", "fail${e.message}")

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
        viewBinding.containerView.background = msd
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            VerificationFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}

