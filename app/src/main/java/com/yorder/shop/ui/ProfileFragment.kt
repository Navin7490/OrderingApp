package com.yorder.shop.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context.WINDOW_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.text.Html
import android.text.Spanned
import android.util.Log
import android.view.*
import android.widget.*
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.yorder.shop.BuildConfig
import com.yorder.shop.R
import com.yorder.shop.const.Constant
import com.yorder.shop.databinding.FragmentProfileBinding
import com.yorder.shop.model.*
import com.yorder.shop.utils.AppDelegate
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.dialog_update.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ProfileFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var viewBinding: FragmentProfileBinding
    lateinit var userDTO: UserDTO

    private var image: String? = null
    lateinit var userId: String
    private var auth: FirebaseAuth? = null
    private var imageP: CircleImageView? = null
    private var navController: NavController? = null

    var phonenumber: String = ""

    // image capture
    private val permissions =
        arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)
    private var galleryResultCode = 1
    private var cameraResultCode = 2
    private val permissionRequestCode = 200
    private var selectedMediaType: com.yorder.shop.const.MediaType =
        com.yorder.shop.const.MediaType.NONE
    private var storeImagePath: Uri? = null
    private var capturedBitmap: Bitmap? = null
    lateinit var viewModel: ProfileViewModel
    lateinit var editStyled: Spanned
    private lateinit var  packageName:String

    private var bitmap: Bitmap? = null
    private var qrgEncoder: QRGEncoder? = null
    private var imageUri: Uri? = null
    private var imageRef: StorageReference? = null
    private var uploadTask: UploadTask? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
    }


    @SuppressLint("SimpleDateFormat")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentProfileBinding.inflate(inflater, container, false)
        viewModel = ViewModelProviders.of(requireActivity()).get(ProfileViewModel::class.java)

        val view = viewBinding.root
        packageName=requireContext().packageName


        val editStr = requireContext().resources.getString(R.string.edit)
        editStyled = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            Html.fromHtml(editStr, Html.FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH)
        } else {
            Html.fromHtml(editStr)
        }
        // viewBinding.ImButtonEditProfile.text=editStyled


        // qr code under line
        val qrCodeStr = requireContext().resources.getString(R.string.viewQRCode)
        val qrCodeStyled = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            Html.fromHtml(qrCodeStr, Html.FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH)
        } else {
            Html.fromHtml(qrCodeStr)
        }

        viewBinding.tvViewQRCode.text=qrCodeStyled


        viewBinding.tvViewQRCode.setOnClickListener {

            viewBinding.progressbar.visibility=View.VISIBLE
            if (userDTO.customerQRCodeUrl.isEmpty()){
               // viewBinding.progressbar.visibility=View.GONE
                viewBinding.progressbar.visibility=View.VISIBLE

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


                imageRef = storage.child("customerQRCodeImage/"  + System.currentTimeMillis().toString() + ".jpg")

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

                        val updateQR= hashMapOf<String,Any>(
                                CollectionUser.kCustomerQRCodeImage to task.result.toString()
                        )

                        Firebase.firestore.collection(CollectionUser.name).document(userDTO.id)
                                .update(updateQR)
                                .addOnCompleteListener {
                                    if (it.isSuccessful){
                                       getUserProfile()
                                    }
                                }

                    }
                }
                       // Toast.makeText(requireContext(),"QR code not found",Toast.LENGTH_SHORT).show()


            }else{
                viewBinding.progressbar.visibility=View.GONE
                val dialog =Dialog(requireContext(),R.style.DialogTheme)
                dialog.window?.setBackgroundDrawable( ColorDrawable(Color.TRANSPARENT));
                dialog.setContentView(R.layout.dialog_update)
                val image=dialog.findViewById<ImageView>(R.id.imQRCode)

                Glide.with(requireContext()).load(userDTO.customerQRCodeUrl)
                        .placeholder(R.drawable.ic_placeholder).into(image)
                dialog.show()

            }



        }


        viewBinding.tvappVersion.text = "App version: ${BuildConfig.VERSION_NAME}"


        val helpAndSupportStr = requireContext().resources.getString(R.string.help_support)
        val helpAndSupportStyled: Spanned =
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                Html.fromHtml(helpAndSupportStr, Html.FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH)
            } else {
                Html.fromHtml(helpAndSupportStr)
            }

        viewBinding.tvHelpAndSupport.text = helpAndSupportStyled

      // viewBinding.ccp.registerCarrierNumberEditText(viewBinding.etPhone)
        val rateTheAppStr = requireContext().resources.getString(R.string.apprate)
        val rateTheAppStyled: Spanned =
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                Html.fromHtml(rateTheAppStr, Html.FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH)
            } else {
                Html.fromHtml(rateTheAppStr)
            }

        viewBinding.tvRate.text = rateTheAppStyled

        // start event edit button
        viewModel.isEditing.observe(viewLifecycleOwner) {
            if (it) {
                // viewBinding.title.text="Edit Profile"
                viewBinding.profileViewFlipper.displayedChild = 1

                ((requireActivity()) as AppCompatActivity).supportActionBar?.title = "Edit Profile"
            } else {
                // viewBinding.title.text="Profile"
                viewBinding.profileViewFlipper.displayedChild = 0

                ((requireActivity()) as AppCompatActivity).supportActionBar?.title = "Profile"

            }
        }

//        viewBinding.ImButtonEditProfile.setOnClickListener {
//            // findNavController().navigate(R.id.profileFragment)
//            val flag = viewModel.isEditing.value ?: false
//            viewModel.setEditing(!flag)
//        }
        // end event edit button

        // start help click event


        viewBinding.tvHelpAndSupport.setOnClickListener {
            val bundle = Bundle().apply {
                this.putString("mailId", userDTO.email)
            }
            findNavController().navigate(R.id.helpFragment, bundle)
        }
        // end help click event

        // start app rate event
        viewBinding.tvRate.setOnClickListener {
            appRate()
        }
        // end app rate event


        auth = FirebaseAuth.getInstance()
        //tvUserId = view.findViewById(R.id.TvPUserId)
        //tvName = view.findViewById(R.id.tvPName)

        // btnLogout = view.findViewById(R.id.btnLogout)
        imageP = view.findViewById(R.id.ImProfileE)
        setHasOptionsMenu(true)


        // start get Data In Bundle

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)

        }




        getUserProfile()


        // select image event

        viewBinding.ImProfileE.setOnClickListener {

            val items = arrayListOf<String>("Camera", "Select from Gallery")
            if ((capturedBitmap != null || storeImagePath != null || userDTO.profileImageUrl.isNotEmpty())) {
                items.add("Remove Photo")
            }
            MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogThemeProfileImage)
                .setTitle("Choose profile image")
                .setItems(items.toTypedArray()) { dialog, which ->
                    when (which) {
                        0 -> {
                            openCamera()
                        }
                        1 -> {
                            openGallery()
                        }
                        2 -> {
                            removeProfileImage()
                        }
                    }
                }
                .show()


        }
        // end select image event


        viewBinding.btnUpdateProfile.setOnClickListener {

           // val mobile = viewBinding.ccp.fullNumberWithPlus.replace("", "")


            if (checkValidation()) {


                if (phonenumber != "null") {

                    Log.e("image", "$storeImagePath")

                    if (storeImagePath != null) {
                        viewBinding.progressbar.visibility = View.VISIBLE

                        val storage = FirebaseStorage.getInstance().getReference("images")
                        Log.e("RegistrationFragment", "upLoadFile")
                        imageRef =
                            storage.child(
                                "profileImage/" + System.currentTimeMillis().toString() + ".jpg"
                            )
                        uploadTask = imageRef!!.putFile(storeImagePath!!)

                        uploadTask!!.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                            if (!task.isSuccessful) {
                                task.exception?.let {
                                    throw it
                                }
                            }
                            return@Continuation imageRef!!.downloadUrl

                        })
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {

                                    val image = task.result.toString()

                                    val updateUserInfo = hashMapOf<String, Any>(

                                        CollectionUser.kCreateAt to Timestamp.now(),
                                        CollectionUser.kUpdateAt to Timestamp.now(),
                                        CollectionUser.kCreateBy to userId,
                                        CollectionUser.kUpdateBy to userId,
                                        CollectionUser.kProfileImage to image,
                                        CollectionUser.kFirstName to viewBinding.etFirstname.text.toString()
                                            .trim(),
                                        CollectionUser.kLastName to viewBinding.etLastName.text.toString()
                                            .trim(),
                                        CollectionUser.kHouseNumber to viewBinding.etHouseNo.text.toString()
                                            .trim(),
                                        CollectionUser.kSocietyName to viewBinding.etSocietyAppartment.text.toString()
                                            .trim(),
                                        CollectionUser.kArea to viewBinding.etArea.text.toString()
                                            .trim(),
                                        CollectionUser.kCity to viewBinding.etCityState.text.toString()
                                            .trim(),
                                        CollectionUser.kPincode to viewBinding.etPinCode.text.toString()
                                            .trim()
                                    )
                                    Firebase.firestore
                                        .collection(CollectionUser.name)
                                        .document(Firebase.auth.currentUser!!.uid)
                                        .update(updateUserInfo as Map<String, Any>)
                                        .addOnCompleteListener {

                                            if (it.isSuccessful) {
                                                viewBinding.progressbar.visibility = View.INVISIBLE
                                                Toast.makeText(
                                                    context,
                                                    "Profile updated",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                viewModel.setEditing(false)
                                                getUserProfile()
                                            }
                                        }
                                        .addOnFailureListener { e ->
                                            viewBinding.progressbar.visibility = View.INVISIBLE

                                            Toast.makeText(
                                                context,
                                                "${e.message}",
                                                Toast.LENGTH_SHORT
                                            )
                                                .show()


                                        }


                                } else {
                                    viewBinding.progressbar.visibility = View.INVISIBLE

                                    Toast.makeText(
                                        requireContext(),
                                        "Something wrong in UpLoadFile Function",
                                        Toast.LENGTH_LONG
                                    )
                                        .show()
                                }

                            }
                            .addOnFailureListener { e ->

                                viewBinding.progressbar.visibility = View.INVISIBLE

                                Toast.makeText(requireContext(), "${e.message}", Toast.LENGTH_LONG)
                                    .show()

                            }
                    } else {
                        viewBinding.progressbar.visibility = View.VISIBLE

                        val updateUserInfo = hashMapOf<String, Any>(
                            CollectionUser.kCreateAt to Timestamp.now(),
                            CollectionUser.kUpdateAt to Timestamp.now(),
                            CollectionUser.kCreateBy to userId,
                            CollectionUser.kUpdateBy to userId,
                            CollectionUser.kFirstName to viewBinding.etFirstname.text.toString()
                                .trim(),
                            CollectionUser.kLastName to viewBinding.etLastName.text.toString()
                                .trim(),
                            CollectionUser.kHouseNumber to viewBinding.etHouseNo.text.toString()
                                .trim(),
                            CollectionUser.kSocietyName to viewBinding.etSocietyAppartment.text.toString()
                                .trim(),
                            CollectionUser.kArea to viewBinding.etArea.text.toString().trim(),
                            CollectionUser.kCity to viewBinding.etCityState.text.toString().trim(),
                            CollectionUser.kPincode to viewBinding.etPinCode.text.toString().trim()
                        )
                        Firebase.firestore
                            .collection(CollectionUser.name)
                            .document(Firebase.auth.currentUser!!.uid)
                            .update(updateUserInfo as Map<String, Any>)
                            .addOnCompleteListener {

                                if (it.isSuccessful) {
                                    viewBinding.progressbar.visibility = View.INVISIBLE

                                    Toast.makeText(context, "Profile updated", Toast.LENGTH_SHORT)
                                        .show()
                                    viewModel.setEditing(false)
                                    getUserProfile()
                                }
                            }
                            .addOnFailureListener { e ->
                                viewBinding.progressbar.visibility = View.INVISIBLE

                                Toast.makeText(context, "${e.message}", Toast.LENGTH_SHORT).show()


                            }
                    }

                } else {

                    if (viewBinding.etPhone.text.toString().trim().length < 10
                    ) {
                        Toast.makeText(context, "Required valid phone number", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        val db = Firebase.firestore
                        db.collection(CollectionUser.name)
                            .whereEqualTo(
                                CollectionUser.kPhoneNumber,
                                "+91${viewBinding.TILPhoneNumber.editText?.text.toString().trim()}"
                            )
                            .get()
                            .addOnSuccessListener { document ->

                                if (document.documents.isNotEmpty()) {

                                    viewBinding.TILPhoneNumber.error="Phone number already exist"
                                } else {

                                    if (storeImagePath != null) {
                                        viewBinding.progressbar.visibility = View.VISIBLE

                                        val storage =
                                            FirebaseStorage.getInstance().getReference("images")
                                        Log.e("RegistrationFragment", "upLoadFile")
                                        imageRef =
                                            storage.child(
                                                "profileImage/" + System.currentTimeMillis()
                                                    .toString() + ".jpg"
                                            )
                                        uploadTask = imageRef!!.putFile(storeImagePath!!)

                                        uploadTask!!.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                                            if (!task.isSuccessful) {
                                                task.exception?.let {
                                                    throw it
                                                }
                                            }
                                            return@Continuation imageRef!!.downloadUrl

                                        })
                                            .addOnCompleteListener { task ->
                                                if (task.isSuccessful) {

                                                    val image = task.result.toString()

                                                    val updateUserInfo = hashMapOf<String, Any>(


                                                        CollectionUser.kCreateAt to Timestamp.now(),
                                                        CollectionUser.kUpdateAt to Timestamp.now(),
                                                        CollectionUser.kCreateBy to userId,
                                                        CollectionUser.kUpdateBy to userId,
                                                        CollectionUser.kProfileImage to image,
                                                        CollectionUser.kFirstName to viewBinding.etFirstname.text.toString()
                                                            .trim(),
                                                        CollectionUser.kLastName to viewBinding.etLastName.text.toString()
                                                            .trim(),
                                                        CollectionUser.kHouseNumber to viewBinding.etHouseNo.text.toString()
                                                            .trim(),
                                                        CollectionUser.kSocietyName to viewBinding.etSocietyAppartment.text.toString()
                                                            .trim(),
                                                        CollectionUser.kArea to viewBinding.etArea.text.toString()
                                                            .trim(),
                                                        CollectionUser.kCity to viewBinding.etCityState.text.toString()
                                                            .trim(),
                                                        CollectionUser.kPincode to viewBinding.etPinCode.text.toString()
                                                            .trim()

                                                    )
                                                    Firebase.firestore
                                                        .collection(CollectionUser.name)
                                                        .document(Firebase.auth.currentUser!!.uid)
                                                        .update(updateUserInfo as Map<String, Any>)
                                                        .addOnCompleteListener {

                                                            if (it.isSuccessful) {
                                                                viewBinding.progressbar.visibility =
                                                                    View.INVISIBLE

                                                                getUserProfile()
                                                                val bundle = Bundle()
                                                                bundle.putString(
                                                                    "phone",
                                                                    viewBinding.etPhone.text.toString()
                                                                        .trim()
                                                                )
                                                                bundle.putString(
                                                                    "userId",
                                                                    Firebase.auth.currentUser?.uid.toString()
                                                                )
                                                                findNavController().navigate(
                                                                    R.id.action_verify,
                                                                    bundle
                                                                )
                                                            }
                                                        }
                                                        .addOnFailureListener { e ->
                                                            Toast.makeText(
                                                                context,
                                                                "${e.message}",
                                                                Toast.LENGTH_SHORT
                                                            ).show()


                                                        }


                                                } else {
                                                    viewBinding.progressbar.visibility =
                                                        View.INVISIBLE

                                                    Toast.makeText(
                                                        requireContext(),
                                                        "Something wrong in UpLoadFile Function",
                                                        Toast.LENGTH_LONG
                                                    )
                                                        .show()
                                                }

                                            }
                                            .addOnFailureListener { e ->

                                                viewBinding.progressbar.visibility = View.INVISIBLE

                                                Toast.makeText(
                                                    requireContext(),
                                                    "${e.message}",
                                                    Toast.LENGTH_LONG
                                                ).show()

                                            }
                                    } else {
                                        viewBinding.progressbar.visibility = View.VISIBLE

                                        val updateUserInfo = hashMapOf<String, Any>(

                                            CollectionUser.kCreateAt to Timestamp.now(),
                                            CollectionUser.kUpdateAt to Timestamp.now(),
                                            CollectionUser.kCreateBy to userId,
                                            CollectionUser.kUpdateBy to userId,
                                            CollectionUser.kFirstName to viewBinding.etFirstname.text.toString()
                                                .trim(),
                                            CollectionUser.kLastName to viewBinding.etLastName.text.toString()
                                                .trim(),
                                            CollectionUser.kHouseNumber to viewBinding.etHouseNo.text.toString()
                                                .trim(),
                                            CollectionUser.kSocietyName to viewBinding.etSocietyAppartment.text.toString()
                                                .trim(),
                                            CollectionUser.kArea to viewBinding.etArea.text.toString()
                                                .trim(),
                                            CollectionUser.kCity to viewBinding.etCityState.text.toString()
                                                .trim(),
                                            CollectionUser.kPincode to viewBinding.etPinCode.text.toString()
                                                .trim()

                                        )
                                        Firebase.firestore
                                            .collection(CollectionUser.name)
                                            .document(Firebase.auth.currentUser!!.uid)
                                            .update(updateUserInfo as Map<String, Any>)
                                            .addOnCompleteListener {

                                                if (it.isSuccessful) {
                                                    viewBinding.progressbar.visibility =
                                                        View.INVISIBLE

                                                    getUserProfile()
                                                    val bundle = Bundle()
                                                    bundle.putString(
                                                        "phone",
                                                        viewBinding.etPhone.text.toString().trim()
                                                    )
                                                    bundle.putString(
                                                        "userId",
                                                        Firebase.auth.currentUser?.uid.toString()
                                                    )

                                                    findNavController().navigate(
                                                        R.id.action_verify,
                                                        bundle
                                                    )
                                                }
                                            }
                                            .addOnFailureListener { e ->
                                                viewBinding.progressbar.visibility = View.INVISIBLE

                                                Toast.makeText(
                                                    context,
                                                    "${e.message}",
                                                    Toast.LENGTH_SHORT
                                                ).show()


                                            }
                                    }

                                }

                            }

                    }

                }
            }


        }

        return viewBinding.root
    }

    //  start app rate fun
    fun appRate() {
        val uri: Uri = Uri.parse("market://details?id=$packageName")
        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(
            Intent.FLAG_ACTIVITY_NO_HISTORY or
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK
        )
        try {
            startActivity(goToMarket)
        } catch (e: ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=$packageName")
                )
            )
        }
    }
    // end app rate fun

    private fun checkValidation(): Boolean {

        var flag = true

        if (viewBinding.TILFirstName.editText?.text.toString().trim().isNullOrEmpty()) {
            viewBinding.TILFirstName.error = "Required"
            flag = false
        } else if (!viewBinding.TILFirstName.editText?.text.toString().trim()
                .matches(Regex("[a-z A-Z]*"))
        ) {
            viewBinding.TILFirstName.error = "Required valid name"
            flag = false
        } else {
            viewBinding.TILFirstName.error = null

        }

        if (viewBinding.TILLastName.editText?.text.toString().trim().isNullOrEmpty()) {
            viewBinding.TILLastName.error = "Required"
            flag = false
        } else if (!viewBinding.TILLastName.editText?.text.toString().trim()
                .matches(Regex("[a-z A-Z]*"))
        ) {
            viewBinding.TILLastName.error = "Required valid name"
            flag = false
        } else {
            viewBinding.TILLastName.error = null

        }

        if (viewBinding.TILPhoneNumber.editText?.text.toString().trim().isNullOrEmpty()) {
            viewBinding.TILPhoneNumber.error = "Required"
            flag = false
        } else if (viewBinding.TILPhoneNumber.editText?.text.toString().trim().length < 10) {
            viewBinding.TILPhoneNumber.error = "Required valid phone"
            flag = false
        } else {
            viewBinding.TILPhoneNumber.error = null

        }


        if (viewBinding.TILHouseNo.editText?.text.toString().trim().isNullOrEmpty()) {
            viewBinding.TILHouseNo.error = "Required"
            flag = false
        } else {
            viewBinding.TILHouseNo.error = null

        }

        if (viewBinding.TILSocietyAppartment.editText?.text.toString().trim().isNullOrEmpty()) {
            viewBinding.TILSocietyAppartment.error = "Required"
            flag = false
        } else {
            viewBinding.TILSocietyAppartment.error = null

        }

        if (viewBinding.TILArea.editText?.text.toString().trim().isNullOrEmpty()) {
            viewBinding.TILArea.error = "Required"
            flag = false
        } else if (!viewBinding.TILArea.editText?.text.toString().trim()
                .matches(Regex("[a-z A-Z]*"))
        ) {
            viewBinding.TILArea.error = "Required valid area name"
            flag = false
        } else {
            viewBinding.TILArea.error = null

        }


        if (viewBinding.TILCityState.editText?.text.toString().trim().isNullOrEmpty()) {
            viewBinding.TILCityState.error = "Required"
            flag = false
        } else if (!viewBinding.TILCityState.editText?.text.toString().trim()
                .matches(Regex("[a-z A-Z]*"))
        ) {
            viewBinding.TILCityState.error = "Required valid city name"
            flag = false
        } else {
            viewBinding.TILCityState.error = null
        }



        if (viewBinding.TILPinCode.editText?.text.toString().trim().isNullOrEmpty() ||
            viewBinding.TILPinCode.editText?.text.toString().trim().length < 6
        ) {
            viewBinding.TILPinCode.error = "Required 6 digit pincode"
            flag = false
        } else {
            viewBinding.TILPinCode.error = null
        }


        return flag

    }


    override fun onDestroyView() {
        viewModel.setEditing(false)
        super.onDestroyView()
    }


    private fun logoutUser() {
        viewBinding.progressbar.visibility = View.VISIBLE
        Firebase.firestore
            .collection(CollectionUser.name)
            .document(Firebase.auth.currentUser!!.uid)
            .update(mapOf(CollectionUser.kNotificationToken to ""))
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    viewBinding.progressbar.visibility = View.GONE

                    Firebase.auth.signOut()

                }
            }
            .addOnFailureListener {
                viewBinding.progressbar.visibility = View.GONE

            }
    }


    private fun openCamera() {
        selectedMediaType = com.yorder.shop.const.MediaType.CAMERA
        checkAndRequestPermission()
    }

    private fun openGallery() {

        selectedMediaType = com.yorder.shop.const.MediaType.GALLERY
        checkAndRequestPermission()
    }

    private fun removeProfileImage() {

        Firebase.firestore.collection(CollectionUser.name).document(Firebase.auth.uid.toString())
            .update(hashMapOf(CollectionUser.kProfileImage to "") as Map<String, Any>)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    viewBinding.ImProfileE.setImageDrawable(null)
                    viewBinding.ImProfileE.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.mipmap.ic_action_profile_placeholder
                        )
                    )
                    capturedBitmap = null
                    storeImagePath = null
                    image = ""

                    viewBinding.ImProfile.setImageDrawable(null)
                    viewBinding.ImProfile.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.mipmap.ic_action_profile_placeholder
                        )
                    )
                    capturedBitmap = null
                    storeImagePath = null
                    image = ""
                    getUserProfile()
                }
            }


    }

    private fun checkAndRequestPermission() {
        val listPermissionNeeded = arrayListOf<String>()

        permissions.forEach { permission ->
            if (!checkPermissionFor(permission)) {
                listPermissionNeeded.add(permission)
            }
        }

        if (listPermissionNeeded.isNotEmpty()) {
            val arr = listPermissionNeeded.toTypedArray()
            requestPermissions(arr, permissionRequestCode)
        } else {
            if (selectedMediaType == com.yorder.shop.const.MediaType.GALLERY) {
                val galleryIntent = Intent(Intent.ACTION_GET_CONTENT)
                galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
                galleryIntent.type = "image/*"
                startActivityForResult(galleryIntent, galleryResultCode)
            } else if (selectedMediaType == com.yorder.shop.const.MediaType.CAMERA) {
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(cameraIntent, cameraResultCode)
            }
        }
    }

    private fun checkPermissionFor(param: String): Boolean {
        return ContextCompat.checkSelfPermission(
            requireActivity(),
            param
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == galleryResultCode) {
                if (data != null && data.data != null) {
                    Log.e("NST", data.data.toString())
                    storeImagePath = data.data

                    // binding.shopImageView.setImageURI(data.data)
                    viewBinding.ImProfileE.setImageURI(data.data)
                    selectedMediaType = com.yorder.shop.const.MediaType.GALLERY
                } else {
                    selectedMediaType = com.yorder.shop.const.MediaType.NONE
                }
            } else if (requestCode == cameraResultCode) {
                if (data != null) {

                    capturedBitmap = data.extras?.get("data") as Bitmap
                    // binding.shopImageView.setImageBitmap(capturedBitmap)
                    viewBinding.ImProfileE.setImageBitmap(capturedBitmap)


                    val file = File(context?.cacheDir, "CUSTOM NAME") //Get Access to a local file.
                    file.delete() // Delete the File, just in Case, that there was still another File
                    file.createNewFile()

                    val fileOutputStream = file.outputStream()
                    val byteArrayOutputStream = ByteArrayOutputStream()
                    capturedBitmap!!.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
                    val bytearray = byteArrayOutputStream.toByteArray()
                    fileOutputStream.write(bytearray)
                    fileOutputStream.flush()
                    fileOutputStream.close()
                    byteArrayOutputStream.close()
                    val url = file.toURI()
                    storeImagePath = url.toString().toUri()


                    selectedMediaType = com.yorder.shop.const.MediaType.CAMERA
                } else {
                    Log.e("NST", "Intent is null")
                    selectedMediaType = com.yorder.shop.const.MediaType.NONE
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == permissionRequestCode) {
            val permissionResult = hashMapOf<String, Int>()
            var deniedCount = 0

            for (index in grantResults.indices) {
                if (grantResults[index] == PackageManager.PERMISSION_DENIED) {
                    permissionResult[permissions[index]] = grantResults[index]
                    deniedCount += 1
                }
            }
            if (deniedCount > 0) {
                for ((permission, _) in permissionResult) {
                    if (shouldShowRequestPermissionRationale(permission)) {
                        Constant.showAlertWithListeners(requireContext(),
                            "",
                            "You have denied some permissions. Allow all permission to work without any problems.",
                            "Yes, Grant permissions",
                            { _, _ ->
                                checkAndRequestPermission()
                            },
                            "No, Exit app",
                            { dialog, _ ->
                                dialog.dismiss()
                                requireActivity().finish()
                            }
                        )
                        break
                    } else {
                        Constant.showAlertWithListeners(requireContext(),
                            "",
                            "You have denied some permissions. Allow all permission at [Settings] > [Permissions]",
                            "Go to Settings",
                            { _, _ ->
                                val intent = Intent(
                                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.fromParts("package", requireActivity().packageName, null)
                                )
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                requireActivity().startActivity(intent)
                            },
                            "No, Exit app",
                            { dialog, _ ->
                                dialog.dismiss()
                                requireActivity().finish()
                            }
                        )
                        break
                    }
                }
            } else {
                checkAndRequestPermission()
            }
        }
    }

    private fun getUserProfile() {
        Log.e("NST", "getuserInfo")
        viewBinding.progressbar.visibility = View.VISIBLE
        Firebase.firestore
            .collection(CollectionUser.name)
            .document(Firebase.auth.currentUser!!.uid)
            .get()
            .addOnSuccessListener {
                viewBinding.progressbar.visibility = View.GONE

                val networkUser = it.toObject<NetworkUserModel>(NetworkUserModel::class.java)

                networkUser?.let {
                    userDTO = UserDTO.create(networkUser)!!
                    viewBinding.areaProfile.visibility = View.VISIBLE
                    AppDelegate.applicationContext().userDTO=userDTO

                    userDTO.let {
                        userId = userDTO.id.toString()
                        viewBinding.etFirstname.setText(userDTO.firstName)
                        viewBinding.etLastName.setText(userDTO.lastName)
                        viewBinding.etEmailId.setText(userDTO?.email)
                        viewBinding.etHouseNo.setText(userDTO?.houseNumber)
                        viewBinding.etSocietyAppartment.setText(userDTO?.societyName)
                        viewBinding.etArea.setText(userDTO?.area)
                        viewBinding.etCityState.setText(userDTO?.city)
                        viewBinding.etPinCode.setText(userDTO?.pincode)
                        phonenumber = userDTO.phoneNumber.toString()
                        phonenumber = userDTO.phoneNumber.toString()

                        if (userDTO?.phoneNumber != "null") {
                            viewBinding.ccp.visibility = View.GONE
                            viewBinding.etPhone.isClickable = false
                            viewBinding.etPhone.isFocusable = false
                            viewBinding.etPhone.isLongClickable = false
                            viewBinding.etPhone.setText(userDTO?.phoneNumber)

                        } else {
                            viewBinding.ccp.visibility = View.GONE
                            viewBinding.TILPhoneNumber.hint = "Phone number"

                            viewBinding.etPhone.setText("")


                        }


                        if (!userDTO.profileImageUrl.isEmpty()) {

                            context?.let { it1 ->
                                Glide.with(it1).load(userDTO?.profileImageUrl)
                                    .placeholder(R.mipmap.ic_action_profile_placeholder)
                                    .into(viewBinding.ImProfileE)
                            }

                        }


                        viewBinding.tvfirstName.text = it.firstName
                        viewBinding.tvLastName.text = it.lastName
                        viewBinding.tvEmail.text = it.email


                        if (it.address.isNotEmpty()) {
                            viewBinding.tvAddress.text = it.address
                            viewBinding.tvAddress.visibility = View.VISIBLE
                            viewBinding.kAddressrTV.visibility = View.VISIBLE


                        }

                        if (it.phoneNumber != "null") {
                            viewBinding.tvPhone.text = it.phoneNumber
                            viewBinding.tvPhone.visibility = View.VISIBLE
                            viewBinding.kPhoneNumberTV.visibility = View.VISIBLE

                        }



                        if (!userDTO.profileImageUrl.isEmpty()) {
                            context?.let { it1 ->
                                Glide.with(it1).load(userDTO.profileImageUrl)
                                    .placeholder(R.mipmap.ic_action_profile_placeholder)
                                    .into(viewBinding.ImProfile)
                            }

                        }
                    }
                }
            }
            .addOnFailureListener {
                viewBinding.progressbar.visibility = View.GONE

                Log.e("NSTreoo", it.toString())
            }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_profile, menu)

        val m = menu.findItem(R.id.edit)


        viewModel.isEditing.observe(viewLifecycleOwner) {
            if (it) {
                // viewBinding.title.text="Edit Profile"
                viewBinding.profileViewFlipper.displayedChild = 1
                m.title = "Done"
            } else {
                // viewBinding.title.text="Profile"
                viewBinding.profileViewFlipper.displayedChild = 0
                m.title = "Edit"

                // m.title = editStyled


            }
        }

        // viewBinding.toolbar.inflateMenu(R.menu.menu_save)

        super.onCreateOptionsMenu(menu, inflater)


    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.edit -> {
                val flag = viewModel.isEditing.value ?: false
                viewModel.setEditing(!flag)

            }
            R.id.logout -> {
                    MaterialAlertDialogBuilder(requireContext(),R.style.AlertDialogTheme)
                        .setMessage("Are you sure to want to Logout")
                        .setCancelable(false)
                        .setNegativeButton("No") { dialog, which ->
                            dialog.dismiss()

                        }
                        .setPositiveButton("Yes") { dialog, which ->
//                        val user = FirebaseAuth.getInstance()
//                        user.signOut()
                            logoutUser()
                            dialog.dismiss()
                        }
                        .show()


            }
        }

        return super.onOptionsItemSelected(item)


    }


}