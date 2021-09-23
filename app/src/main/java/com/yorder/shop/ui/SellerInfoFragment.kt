package com.yorder.shop.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.yorder.shop.R
import com.yorder.shop.databinding.DialoagChangephonenumberBinding.inflate
import com.yorder.shop.databinding.FragmentSellerInfoBinding
import com.yorder.shop.model.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class SellerInfoFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    lateinit var sellerId:String
    lateinit var status:String
    lateinit var subId:String
    lateinit var viewBinding:FragmentSellerInfoBinding
    private var sellerDTO: UserDTO? = null
    private val queryListenerList: ArrayList<ListenerRegistration> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        sellerId = arguments?.getString("sellerId","").toString()
        status=arguments?.getString("status","").toString()
        subId=arguments?.getString("subId","").toString()


        Log.e("id",subId)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        viewBinding = FragmentSellerInfoBinding.inflate(inflater,container,false)
        val layoutManager= LinearLayoutManager(context)
        layoutManager.orientation =LinearLayoutManager.HORIZONTAL
        viewBinding.RvSellerInfo.layoutManager =layoutManager
        viewBinding.RvSellerInfo.setHasFixedSize(true)

        getSeller(sellerId)
        getSellerProduct()

        viewBinding.btnSIViewItems.setOnClickListener {
            val bundle =Bundle().apply { this.putString("sellerId",sellerId) }

            //findNavController().navigate(R.id.productsListFragment,bundle)
            findNavController().popBackStack()
        }



        if (status=="Approved"){
            viewBinding.tvSISubscribe.visibility= View.INVISIBLE
        }else if (status==SubscriptionStatus.NONE.value){
            viewBinding.tvSISubscribe.text="Subscribe"
            viewBinding.tvSISubscribe.visibility= View.VISIBLE


            viewBinding.tvSISubscribe.setTextColor(ContextCompat.getColor(viewBinding.tvSISubscribe.context, R.color.app_text_white))
            viewBinding.tvSISubscribe.background = ContextCompat.getDrawable(viewBinding.tvSISubscribe.context,R.drawable.drawable_subscribebutton)


        }else{
            viewBinding.tvSISubscribe.text=status
            viewBinding.tvSISubscribe.visibility= View.VISIBLE
        }


        viewBinding.tvSISubscribe.setOnClickListener {
            if (viewBinding.tvSISubscribe.text == "Subscribe"){
                viewBinding.progressBar!!.visibility = View.VISIBLE
                Firebase.firestore
                    .collection(CollectionSubscriptions.name)
                    .document()
                    .set(hashMapOf(
                        CollectionSubscriptions.kCustomerId to Firebase.auth.currentUser!!.uid,
                        CollectionSubscriptions.kSellerId to sellerId,
                        CollectionSubscriptions.kSubStatus to SubscriptionStatus.PENDING.value
                    )).addOnSuccessListener {
                        viewBinding.progressBar!!.visibility = View.GONE

                        viewBinding.tvSISubscribe.text=SubscriptionStatus.PENDING.value
                        viewBinding.tvSISubscribe.setTextColor(ContextCompat.getColor(viewBinding.tvSISubscribe.context, R.color.app_bg_blue))
                        viewBinding.tvSISubscribe.background = ContextCompat.getDrawable(viewBinding.tvSISubscribe.context,R.drawable.drawable_searchedit)



                    }
                    .addOnFailureListener { e ->
                        viewBinding.progressBar!!.visibility = View.GONE
                        Toast.makeText(requireContext(), e.localizedMessage
                            ?: "Unable to Subscribe", Toast.LENGTH_SHORT).show()
                    }
            }else if (viewBinding.tvSISubscribe.text==SubscriptionStatus.PENDING.value){
                    MaterialAlertDialogBuilder(requireContext(),R.style.AlertDialogTheme)
                        .setMessage("Are you sure to want to cancel the Subscription request?")
                        .setNegativeButton("No"){dialog,which->
                            dialog.dismiss()

                        }
                        .setPositiveButton("Yes"){dialog,which->
                            viewBinding.progressBar!!.visibility = View.VISIBLE
                            Firebase.firestore
                                .collection(CollectionSubscriptions.name)
                                .document(subId)
                                .delete()
                                .addOnSuccessListener {
                                    viewBinding.progressBar!!.visibility = View.GONE


                                   viewBinding.tvSISubscribe.text="Subscribe"

                                    viewBinding.tvSISubscribe.setTextColor(ContextCompat.getColor(viewBinding.tvSISubscribe.context, R.color.app_text_white))
                                    viewBinding.tvSISubscribe.background = ContextCompat.getDrawable(viewBinding.tvSISubscribe.context,R.drawable.drawable_subscribebutton)

                                    dialog.dismiss()


                                }
                                .addOnFailureListener { e ->
                                    viewBinding.progressBar!!.visibility = View.GONE
                                    Toast.makeText(requireContext(), e.localizedMessage
                                        ?: "Unable to Subscribe", Toast.LENGTH_SHORT).show()
                                }

                        }
                        .show()


            }
        }
        return viewBinding.root
    }

    private fun getSeller(sellerId:String){
        viewBinding.progressBar.visibility=View.VISIBLE
        val queryListener = Firebase.firestore
            .collection(CollectionUser.name)
            .document(sellerId)


            .addSnapshotListener { documentSnapshot, error ->
                viewBinding.progressBar.visibility=View.GONE

                Log.e("NST", "SellerProducts SellerDetails RealTime")
                if (error != null) {
                    Log.e("NST", "Error in SellerDetails DocumentSnapshot: $error")
                    Toast.makeText(
                        requireContext(),
                        error.localizedMessage ?: "Unable to get seller",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    if (documentSnapshot != null) {
                        val networkSeller = documentSnapshot.toObject(NetworkUserModel::class.java)
                        if (networkSeller != null) {
                            sellerDTO = UserDTO.create(networkSeller)


                            if (sellerDTO == null) {
                               // showAlertForSeller()
                            } else {
                               viewBinding.tvSIShopName.text =sellerDTO?.shopName
                                viewBinding.tvSIShopSmallInfo.text = sellerDTO?.shopSmallInfo
                                viewBinding.tvSIAddress.text =sellerDTO?.address
                                viewBinding.tvSIPhone.text = sellerDTO?.phoneNumber
                            }
                        }
                    } else {
                        //showAlertForSeller()
                    }
                }
            }


        queryListenerList.add(queryListener)
    }


    // get seller product

    fun getSellerProduct(){
        viewBinding.progressBar.visibility=View.VISIBLE

        val imageList = arrayListOf<ModelDiscover>()
        val db = Firebase.firestore.collection(CollectionProducts.name)
            .whereEqualTo(CollectionProducts.kProductSellerId,sellerId)
            db.get()
                .addOnSuccessListener {
                    for (document in it){
                        viewBinding.progressBar.visibility=View.GONE

                        val image = document[CollectionProducts.kProductImageUrl].toString()

                        val modelDiscover =ModelDiscover("",image)
                        imageList.add(modelDiscover)
                    }


                    val adapter = context?.let { it1 -> AdapterSellerInfo(it1,imageList) }
                    viewBinding.RvSellerInfo.adapter = adapter


                    Log.e("Image","$imageList")
                }
    }
    // end seller product
    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SellerInfoFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}

class AdapterSellerInfo(val context: Context,val list:ArrayList<ModelDiscover>):
    RecyclerView.Adapter<AdapterSellerInfo.ViewHolderImage>() {
    class ViewHolderImage(itemView:View) :RecyclerView.ViewHolder(itemView){

        val image = itemView.findViewById<ImageView>(R.id.image)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderImage {
        val view =LayoutInflater.from(context).inflate(R.layout.list_sellerinfo_image,parent,false)
        return ViewHolderImage(view)
    }

    override fun onBindViewHolder(holder: ViewHolderImage, position: Int) {

        with(holder){
            Glide.with(context).load(list[position].name)
                .placeholder(R.drawable.ic_placeholder)
                .into(image)
        }
    }

    override fun getItemCount(): Int {
      return  list.size
    }


}

