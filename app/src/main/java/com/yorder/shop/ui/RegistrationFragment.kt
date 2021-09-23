package com.yorder.shop.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.WINDOW_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.Point
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.Html
import android.text.Spanned
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.hbb20.BuildConfig

import com.yorder.shop.R
import com.yorder.shop.const.Constant
import com.yorder.shop.databinding.FragmentRegistrationBinding
import com.yorder.shop.model.CollectionUser
import com.yorder.shop.utils.Constants
import com.yorder.shop.utils.CustomEdge
import com.onesignal.OneSignal
import com.onesignal.OneSignal.PostNotificationResponseHandler
import kotlinx.android.synthetic.main.fragment_registration.*
import kotlinx.android.synthetic.main.fragment_registration.view.*
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*




class RegistrationFragment : Fragment() {
    lateinit var viewBinding: FragmentRegistrationBinding
    lateinit var auth: FirebaseAuth
    private var bitmap: Bitmap? = null
    private var qrgEncoder: QRGEncoder? = null
    private var imageUri: Uri? = null
    private var imageRef: StorageReference? = null
    private var uploadTask: UploadTask? = null
    private var navController: NavController? = null
    private var userId: String? = null

    // location

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val REQUEST_LOCATION: Int = 1
    var locationManager: LocationManager? = null
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    lateinit var birthDate: java.sql.Timestamp

    lateinit var emailExist: String
    lateinit var errorMessage:String
    override fun onStart() {
        super.onStart()
        locationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager?

        locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (!locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            onGPS()
        } else {
            getGPS()

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

    }

    /*
    916505552124 sm
    916505552121 sn*/


    @SuppressLint("LongLogTag", "SimpleDateFormat")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // View Binding
        viewBinding = FragmentRegistrationBinding.inflate(inflater, container, false)

        emailExist = "The email address is already in use by another account."
        errorMessage ="An internal error has occurred. [ Unable to resolve host \"www.googleapis.com\":No address associated with hostname ]"
        auth = FirebaseAuth.getInstance()


        viewBinding.tvAppVersion.text = "App version: ${BuildConfig.VERSION_NAME}"


        setBottomEdge()

        val constant = Constant(requireContext())
        constant.setupID()


        val signUpStr = requireContext().resources.getString(R.string.signup)
        val signUpStyled: Spanned =
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                Html.fromHtml(signUpStr, Html.FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH)
            } else {
                Html.fromHtml(signUpStr)
            }

        viewBinding.signUpTV.text = signUpStyled



        viewBinding.loginTV.setOnClickListener {
            findNavController().popBackStack()
        }


        viewBinding.btnSignUp.setOnClickListener {

            val firstName: String = viewBinding.firstNameTIL.editText?.text.toString().trim()
            val lastName: String = viewBinding.lastNameTIL.editText?.text.toString().trim()
            val email: String = viewBinding.emailTIL.editText?.text.toString().trim()
            val mobile: String = viewBinding.phoneTIL.editText?.text.toString().trim()

            val password: String = viewBinding.passwordTIL.editText?.text.toString().trim()
            val passwordConfirm: String =
                viewBinding.passwordReEnterTIL.editText?.text.toString().trim()
            // val mobile = ccp!!.fullNumberWithPlus.replace("", "")
            val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"

            Log.e("phone", "")


            val selectedDate = "13-09-2011"


            val formatter: DateFormat = SimpleDateFormat("dd-MM-yyyy")
            val date = formatter.parse(selectedDate) as Date
            birthDate = java.sql.Timestamp(date.time)


            // Check condition
            if (checkValidation()) {


                //create user to email and password
                viewBinding.progressBar.visibility = View.VISIBLE


                val db = Firebase.firestore
                db.collection(CollectionUser.name)
                    .whereEqualTo(CollectionUser.kPhoneNumber, "+91$mobile")
                    .get()
                    .addOnSuccessListener { document ->

                        if (document.documents.isNotEmpty()) {

                            viewBinding.progressBar.visibility = View.GONE
                            viewBinding.phoneTIL.error = "Phone number already exist"

                        } else {

                            auth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Log.e(
                                            "RegistrationFragment",
                                            "createUserWithEmailAndPassword"
                                        )
                                        auth.signInWithEmailAndPassword(email, password)
                                            .addOnCompleteListener { task ->
                                                if (task.isSuccessful) {
                                                    viewBinding.progressBar.visibility = View.GONE
                                                    Log.e(
                                                        "RegistrationFragment",
                                                        "signInWithEmailAndPassword"
                                                    )

                                                    userId = auth.uid.toString()
                                                    val deviceState = OneSignal.getDeviceState()
                                                    val notificationToken = deviceState?.userId
                                                    Log.e(
                                                        "deviceId",
                                                        "$notificationToken"
                                                    )

                                                    if (notificationToken != null) {
                                                            insertData(
                                                                notificationToken,
                                                                userId!!,
                                                                firstName,
                                                                lastName,
                                                                email,
                                                                mobile,
                                                                birthDate)
                                                        } else {
                                                            Toast.makeText(
                                                                requireContext(),
                                                                "Please Try again",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        }

                                                }


                                            }.addOnFailureListener { e ->

                                                Toast.makeText(
                                                    requireContext(),
                                                    "${e.message}",
                                                    Toast.LENGTH_SHORT
                                                ).show()

                                            }

                                        // sendEmailVerification()
                                    }


                                }
                                .addOnFailureListener { e ->
                                    viewBinding.progressBar.visibility = View.GONE
                                    if (e.message == emailExist){
                                        viewBinding.emailTIL.error="email already exist"

                                    }else if (e.message == errorMessage){
                                        Toast.makeText(context,"Please check Internet.",Toast.LENGTH_SHORT).show()
                                    }
                                }
                        }

                    }
                    .addOnFailureListener { e ->
                        Log.e("error", "${e.message}")

                        Toast.makeText(context, "${e.message}", Toast.LENGTH_SHORT).show()
                    }


            }
        }


        return viewBinding.root
    }


    // start InsertData
    private fun insertData(
            notificationToken: String,
            userId: String,
            fName: String,
            lName: String,
            email: String,
            phone: String,
            birthDate: java.sql.Timestamp
    ) {

        progressBar!!.visibility = View.VISIBLE


        if (userId.isNotEmpty()){


            val manager = context?.getSystemService(WINDOW_SERVICE) as WindowManager

            val display = manager.defaultDisplay

            val point = Point()

            display.getSize(point)


            val width = point.x
            val height = point.y

            var smallerDimension = if (width < height) width else height
            smallerDimension = smallerDimension * 3 / 4



            qrgEncoder = QRGEncoder(
                    userId, null,
                    QRGContents.Type.TEXT,
                    smallerDimension
            )

            try {
                bitmap = qrgEncoder?.encodeAsBitmap()
                //qrImage?.setImageBitmap(bitmap)



            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
        val file = File(context?.cacheDir, "CUSTOM NAME") //Get Access to a local file.
        file.delete() // Delete the File, just in Case, that there was still another File
        file.createNewFile()

        val fileOutputStream = file.outputStream()
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap!!.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val bytearray = byteArrayOutputStream.toByteArray()
        fileOutputStream.write(bytearray)
        fileOutputStream.flush()
        fileOutputStream.close()
        byteArrayOutputStream.close()
        val url = file.toURI()
        imageUri = url.toString().toUri()
        val storage = FirebaseStorage.getInstance().getReference("images")


        imageRef = storage.child("customerQRCodeImage/" + fName + System.currentTimeMillis().toString() + ".jpg")

        uploadTask = imageRef!!.putFile(imageUri!!)

        uploadTask!!.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            return@Continuation imageRef!!.downloadUrl

        }).addOnCompleteListener { task ->
            if (task.isSuccessful) {


                val user = hashMapOf(
                        CollectionUser.kUserType to "customer",
                        CollectionUser.kCreateAt to Timestamp.now(),
                        CollectionUser.kUpdateAt to Timestamp.now(),
                        CollectionUser.kCreateBy to userId,
                        CollectionUser.kUpdateBy to userId,
                        CollectionUser.kNotificationToken to notificationToken,
                        CollectionUser.kUserGeoLocation to GeoPoint(latitude, longitude),
                        CollectionUser.kFirstName to fName,
                        CollectionUser.kLastName to lName,
                        CollectionUser.kEmail to email,
                        CollectionUser.kPhoneNumber to "null",
                        CollectionUser.kBirthDate to birthDate,
                        CollectionUser.kHouseNumber to "",
                        CollectionUser.kSocietyName to "",
                        CollectionUser.kStreet to "",
                        CollectionUser.kArea to "",
                        CollectionUser.kCity to "",
                        CollectionUser.kState to "",
                        CollectionUser.kCountry to "",
                        CollectionUser.kPincode to "",
                        CollectionUser.kPhoneVerified to false,
                        CollectionUser.kProfileImage to "",
                        CollectionUser.kCustomerQRCodeImage to task.result.toString()

                )
                val db = Firebase.firestore

                val dat = db.collection(CollectionUser.name).document(userId)
                dat.set(user)
                        .addOnSuccessListener {
                            progressBar!!.visibility = View.GONE

                            val bundle = Bundle()
                            bundle.putString("userId", userId)
                            bundle.putString("phone", phone)

                            sendNotification()

                            findNavController().navigate(
                                    R.id.action_registrationFragment_to_verificationFragment,
                                    bundle
                            )
                        }
                        .addOnFailureListener { e ->
                            progressBar!!.visibility = View.GONE
                            Toast.makeText(requireContext(), "${e.message}fail", Toast.LENGTH_SHORT).show()
                        }


            }
        }


    }
    // end InsertData



    // start validation fun

    private fun checkValidation(): Boolean {
        var flag = true
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        if (viewBinding.firstNameTIL.editText?.text.toString().trim().isNullOrEmpty()) {
            flag = false
            viewBinding.firstNameTIL.error = "Required"
        } else if (!viewBinding.firstNameTIL.editText?.text.toString().trim()
                .matches(Regex("[a-z A-Z]*"))
        ) {
            flag = false
            viewBinding.firstNameTIL.error = "Required valid name"
        } else {
            viewBinding.firstNameTIL.error = null

        }

        if (viewBinding.lastNameTIL.editText?.text.toString().trim().isNullOrEmpty()) {
            flag = false
            viewBinding.lastNameTIL.error = "Required"
        } else if (!viewBinding.lastNameTIL.editText?.text.toString().trim()
                .matches(Regex("[a-z A-Z]*"))
        ) {
            flag = false
            viewBinding.lastNameTIL.error = "Required valid name"
        } else {
            viewBinding.lastNameTIL.error = null

        }


        if (viewBinding.emailTIL.editText?.text.toString().trim().isNullOrEmpty()) {
            flag = false
            viewBinding.emailTIL.error = "Required"
        } else if (!viewBinding.emailTIL.editText?.text.toString().trim()
                .matches(Regex(emailPattern))
        ) {
            flag = false
            viewBinding.emailTIL.error = "Required valid email"
        } else {
            viewBinding.emailTIL.error = null

        }

        if (viewBinding.phoneTIL.editText?.text.toString().trim().isNullOrEmpty()) {
            flag = false
            viewBinding.phoneTIL.error = "Required"
        } else if (viewBinding.phoneTIL.editText?.text.toString().trim().length < 10) {
            flag = false
            viewBinding.phoneTIL.error = "Required valid phone"
        } else {
            viewBinding.phoneTIL.error = null

        }

        if (viewBinding.passwordTIL.editText?.text.toString().trim().isNullOrEmpty()) {
            flag = false
            viewBinding.passwordTIL.error = "Required"
        } else if (viewBinding.passwordTIL.editText?.text.toString().trim().length < 6) {
            flag = false
            viewBinding.passwordTIL.error = "Required 6 digit password"
        } else {
            viewBinding.passwordTIL.error = null

        }
        if (viewBinding.passwordReEnterTIL.editText?.text.toString().trim().isNullOrEmpty()) {
            flag = false
            viewBinding.passwordReEnterTIL.error = "Required"
        } else if (!viewBinding.passwordReEnterTIL.editText?.text.toString().trim()
                .matches(Regex(viewBinding.passwordTIL.editText?.text.toString().trim()))
        ) {
            flag = false
            viewBinding.passwordReEnterTIL.error = "Confirm password not match"
        } else {
            viewBinding.passwordReEnterTIL.error = null

        }
        return flag
    }
    // end validation fun

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


    private fun sendNotification() {

        Thread {
            val deviceState = OneSignal.getDeviceState()
            val userIdDevice = deviceState?.userId
            try {

                val jsonObject = JSONObject(
                    "{'include_player_ids':['$userIdDevice']," +
                            "'headings':{'en':'Dear customer welcome to ${resources.getString(R.string.app_name)}'}," +
                            "'contents':{'en':'Your registration has been  successFully'}," +
                            "'android_group':'123'," +
                            "'android_led_color':'313F55'," +
                            "'android_accent_color':'313F55'}"
                )
                OneSignal.postNotification(jsonObject,
                    object : PostNotificationResponseHandler {
                        override fun onSuccess(response: JSONObject?) {

                            Log.e("onSuccess", response.toString())

                        }

                        override fun onFailure(response: JSONObject?) {
                            Log.e("onFailure", response.toString())

                        }
                    })

            } catch (e: JSONException) {
                Log.e("JSONException", "${e.message}")
            }

        }.start()


    }

    // enable GPS
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
    // end GPS

    // start  getLocation
    private fun getGPS() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ), REQUEST_LOCATION
            )
        } else {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(
                requireActivity()
            )


            fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
                val location = task.result
                if (location != null) {


                    try {

                        // initialize geoCoder
                        val geoCoder = Geocoder(requireContext(), Locale.getDefault())
                        val address: List<Address> = geoCoder.getFromLocation(
                            location.latitude,
                            location.longitude,
                            1
                        )

                        latitude = address[0].latitude
                        longitude = address[0].longitude

                        Log.e(
                            "location",
                            "lat = $latitude log = $longitude locality =${address[0].locality}"
                        )

                    }catch (e:Exception){
                       e.printStackTrace()
                    }

                    // Toast.makeText(requireContext(), "Your Location: \nLatitude: $latitude\nLongitude: $longitude}", Toast.LENGTH_SHORT).show()

                }
            }

        }
    }

    // end  getLocation


}