package com.yorder.shop.ui

import android.graphics.Paint
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import com.google.firebase.auth.FirebaseAuth
import com.yorder.shop.R
import com.yorder.shop.databinding.FragmentForgotpasswordBinding
import com.yorder.shop.keyboard.HideKeyBoard
import com.yorder.shop.utils.Constants
import com.yorder.shop.utils.CustomEdge

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class ForgotPasswordFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    lateinit var viewBinding: FragmentForgotpasswordBinding
    private var etfEmail: String? = null
    private var btnForgot: Button? = null
    private var auth: FirebaseAuth? = null
    private var navController: NavController? = null
    private var hideKeyBoard: HideKeyBoard? = null
    private var emailNotExist:String =""
    private var emailNotFormated:String =""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // View Binding
        viewBinding = FragmentForgotpasswordBinding.inflate(inflater, container, false)
        val view = viewBinding!!.root

        auth = FirebaseAuth.getInstance()

        setBottomEdge()
        hideKeyBoard = HideKeyBoard(requireContext())
        emailNotExist ="There is no user record corresponding to this identifier. The user may have been deleted."
        emailNotFormated ="The email address is badly formatted."



        val loginStr = requireContext().resources.getString(R.string.log_in)
        val loginStyled: Spanned = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            Html.fromHtml(loginStr, Html.FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH)
        } else {
            Html.fromHtml(loginStr)
        }

        viewBinding.goBackLogin.text=loginStyled

       viewBinding.goBackLogin.setOnClickListener {
           findNavController().popBackStack()

       }
        viewBinding.getPasswordButton!!.setOnClickListener {
            //materialDialog2()
           val  email = viewBinding.emailIdTIL.editText?.text.toString().trim()
            val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"

            if (email.isEmpty()) {
                viewBinding.emailIdTIL.error="Required email"
            } else if (!email.matches(Regex(emailPattern))){
                viewBinding.emailIdTIL.error="Required valid email"

            }else {
                viewBinding.emailIdTIL.error=null

                viewBinding.progressBar!!.visibility = View.VISIBLE
                hideKeyBoard!!.isKeyboardHide(viewBinding.emailIdTIL)
                auth!!.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            viewBinding. progressBar!!.visibility = View.GONE

                            materialDialog()
                        }

                    }
                    .addOnFailureListener { e ->
                        viewBinding. progressBar!!.visibility = View.GONE
                        if (e.message ==emailNotExist){
                           // Toast.makeText(context, "Please enter your Registered email Id", Toast.LENGTH_LONG).show()
                            viewBinding.emailIdTIL.error="Enter your register email id"

                        }else if (e.message == emailNotFormated){
                            //Toast.makeText(context, "Please enter valid email", Toast.LENGTH_LONG).show()
                            viewBinding.emailIdTIL.error="Required valid email"

                        }

                        Log.e("ForgetPassword","${e.message}")

                    }
            }
        }

        return view
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

    // Start material dialog

    private fun materialDialog() {

            MaterialAlertDialogBuilder(requireContext(),R.style.AlertDialogTheme)
                .setTitle("")
                .setMessage("We have sent you the password reset link.")
                .setCancelable(false)
                .setPositiveButton("Ok"){dialog,which->
                    findNavController().popBackStack()

                }

                .show()

    }

    // ENd material dialog





    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ForgotPasswordFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}