package com.yorder.shop.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.location.LocationManagerCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.yorder.shop.R
import com.yorder.shop.adaptore.*
import com.yorder.shop.databinding.FragmentCartBinding
import com.yorder.shop.interfaces.CartInterFace
import com.yorder.shop.interfaces.SellerProductItemInterface
import com.yorder.shop.model.*
import com.yorder.shop.utils.AppDelegate
import com.yorder.shop.utils.Constants
import com.onesignal.OneSignal
import kotlinx.android.synthetic.main.fragment_cart.*
import kotlinx.android.synthetic.main.fragment_cart.view.*
import kotlinx.android.synthetic.main.item_product_variants_adapter.*
import org.json.JSONException
import org.json.JSONObject
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class CartFragment : Fragment(), SellerProductItemInterface {
    private var param1: String? = null
    private var param2: String? = null
    lateinit var viewBinding: FragmentCartBinding
    lateinit var listCart: ArrayList<ModelCart>
    lateinit var listCreateOrder: ArrayList<CreateOrder>
    lateinit var adaptorCart: AdaptorCart
    var selleIdCart: String = ""
    var cartModel: CartModel? = null
    private var notificationTokenSeller: String = ""
    val currentUser = FirebaseAuth.getInstance().currentUser
    private var cartAllTotalPrice = 0
    var id = "null"

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    var sellerInRange = false

    lateinit var cartInterFace: CartInterFace
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        id = arguments?.getString("sellerId", "null").toString()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // View Binding
        viewBinding = FragmentCartBinding.inflate(inflater, container, false)
        sellerInRange = false
        cartInterFace.onSetUserId(id)
        viewBinding.recyclerViewcart.layoutManager = LinearLayoutManager(requireContext())
        viewBinding.recyclerViewcart.setHasFixedSize(true)

        adaptorCart = AdaptorCart(requireContext(), arrayListOf(), cartModel, this)
        viewBinding.recyclerViewcart.adapter = adaptorCart

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())

        // start order button click event
        viewBinding.btnCartProceedToPayFloating.setOnClickListener {
            val userDTO = AppDelegate.applicationContext().userDTO

            if (sellerInRange) {
                if (userDTO != null) {
                    Log.e("User", "$userDTO")
                    if (userDTO.address.isNotEmpty()) {
                        addToOrder()

                    } else {
                        Constants.showAlertWithListeners(
                            requireContext(),
                            "Update profile",
                            "Please update  your profile.",
                            "Update",
                            { dialog, _ ->

                                findNavController().navigate(R.id.profileFragment)

                            }, "No",
                            { dialog, _ ->
                                dialog.dismiss()

                            }
                        )
                    }
                }

            } else {
                Constants.showAlertWithListeners(
                    requireContext(),
                    "Sorry",
                    "Your order not place.please try to other location.",
                    "ok",
                    { dialog, _ ->
                        dialog.dismiss()

                    }, "", null
                )
            }

        }
        // end order button click event

        // start continue shopping button click event
        viewBinding.btnCartContinueShopping.setOnClickListener {
            findNavController().navigate(R.id.sellersFragment)
        }
        // end continue shopping button click event

        // start add product button click event
        viewBinding.addButton.setOnClickListener {


            if (sellerInRange) {

                val bundle = Bundle().apply {
                    this.putString("sellerId", selleIdCart)
                }

                findNavController().navigate(R.id.productsListFragment, bundle)
            }

        }
        // end add product button click event

        productRealTimeUpdate()
        cartRealTimeUpdate()

        return viewBinding.root


    }





    @SuppressLint("MissingPermission")
    override fun onResume() {
        super.onResume()
        listCart = ArrayList()
        listCreateOrder = ArrayList()
        if (isLocationEnable()) {

            viewBinding.progressbar.visibility = View.VISIBLE
            fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
                val location = task.result
                if (location != null) {

                    try {
                        viewBinding.progressbar.visibility = View.GONE

                        val geoCoder = Geocoder(requireContext(), Locale.getDefault())
                        val addressList: List<Address>? =
                            geoCoder.getFromLocation(location.latitude, location.longitude, 1)

                        if (addressList != null && addressList.isNotEmpty()) {
                            val address = addressList.first()
                            if (address.hasLatitude() && address.hasLongitude()) {
                                latitude = address.latitude
                                longitude = address.longitude
                                getMyCart()
                                Log.e("NST", address.toString())


                            }
                        } else {
                            Log.e("NST", "AddressList Null or Empty")
                        }
                    } catch (e: Exception) {
                        viewBinding.progressbar.visibility = View.GONE

                        e.printStackTrace()
                        Constants.showAlertWithListeners(
                            requireContext(),
                            "Error",
                            e.localizedMessage ?: "Unknown exception in GeoCoder",
                            "",
                            null,
                            "",
                            null
                        )
                    }
                }
            }
        } else {
            // Enable gps service from your settings
            Log.e("NST", "Enable GPS in Settings")
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

    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        cartInterFace = context as CartInterFace
    }


    private fun isLocationEnable(): Boolean {
        val locationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return LocationManagerCompat.isLocationEnabled(locationManager)
    }

    // start addToOrder  fun

    private fun addToOrder() {

        val customerId = FirebaseAuth.getInstance().currentUser?.uid.toString()
        viewBinding.progressbar.visibility = View.VISIBLE
        btnCartProceedToPayFloating?.isEnabled = false

        val insertOrder = hashMapOf<Any, Any>(
            CollectionSubscriptions.kCustomerId to customerId,
            CollectionSubscriptions.kSellerId to selleIdCart,
            CollectionOrders.kCreatedAt to Timestamp.now(),
            CollectionOrders.kCreatedBy to customerId,
            "product_list" to listCreateOrder,
            CollectionOrders.kTotalPrice to cartAllTotalPrice.toString(),
            CollectionOrders.kProductInstruction to viewBinding.etAddSpecialInstruction.text.toString()
                .trim(),
            CollectionOrders.kOrderStatus to "New",
            CollectionOrders.kUpdatedAt to Timestamp.now(),
            CollectionOrders.kUpdatedBy to customerId
        )
        //insertOrder[keyProductList] = product

        Firebase.firestore.collection(CollectionOrders.name)
            .add(insertOrder)
            .addOnSuccessListener {
                viewBinding.progressbar.visibility = View.GONE
                btnCartProceedToPayFloating?.visibility = View.GONE
                val toast = Toast.makeText(context, "Order successful", Toast.LENGTH_LONG)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()

                val sb = StringBuffer()
                for (product in listCreateOrder) {
                    val name = product.productName

                    sb.append(name)
                    sb.append(". ")
                }
                sendNotification(notificationTokenSeller, sb.toString())
                deleteCart(cartModel!!.id)

            }
            .addOnFailureListener { e ->
                viewBinding.progressbar.visibility = View.GONE
                btnCartProceedToPayFloating?.isEnabled = true

                Toast.makeText(context, "${e.message}", Toast.LENGTH_LONG).show()

            }
    }
    // end addToOrder  fun


    // start get my cart fun
    private fun getMyCart() {
        viewBinding.progressbar.visibility = View.VISIBLE

        val center = GeoLocation(latitude, longitude)
        val radiusInM = (10 * 1000).toDouble()
        Firebase.firestore.collection(CollectionCart.name)
            .whereEqualTo(CollectionSubscriptions.kCustomerId, Firebase.auth.uid)
            .get()
            .addOnSuccessListener { document ->
                viewBinding.progressbar.visibility = View.GONE

                if (document.documents.isNullOrEmpty()) {
                    viewBinding.tvCartIsEmpty.visibility = View.VISIBLE
                    viewBinding.btnCartContinueShopping.visibility = View.VISIBLE
                    viewBinding.imCartEmpty.visibility = View.VISIBLE

                } else {
                    val documentSnapshot = document.documents
                    documentSnapshot.map {
                        val networkCartModel = it.toObject(NetworkCartModel::class.java)
                        if (networkCartModel != null) {
                            val cartModel = CartModel.create(networkCartModel)
                            selleIdCart = cartModel?.sellerId.toString()
                            cartModel?.productList?.map { cart ->

                                getMyProduct(cart)
                            }

                        }
                    }
                   Firebase.firestore.collection(CollectionUser.name).document(selleIdCart)
                        .get()
                        .addOnSuccessListener {
                            val networkUserModel = it.toObject(NetworkUserModel::class.java)
                            val sellerDTO = UserDTO.create(networkUserModel!!)
                            if (sellerDTO != null) {
                                viewBinding.tvCarShopName.text = sellerDTO.shopName
                                viewBinding.tvCartShopDetail.text = sellerDTO.shopSmallInfo
                                notificationTokenSeller = sellerDTO.notificationToken.toString()
                                Glide.with(requireContext()).load(sellerDTO.shopBannerImageUrl)
                                    .placeholder(R.drawable.ic_placeholder)
                                    .into(viewBinding.ImCartShopLogo)

                                val distance = GeoFireUtils.getDistanceBetween(
                                    GeoLocation(
                                        sellerDTO.latitude,
                                        sellerDTO.longitude
                                    ), center
                                )

                                if (distance <= radiusInM) {
                                    sellerInRange = true
                                    Log.e("NST", "Matching Radius; $distance")
                                } else {
                                    sellerInRange = false
                                    Log.e("NST", "Not Matching Radius; $distance")
                                }

                            }


                        }

                }

            }
            .addOnFailureListener { e ->
                viewBinding.progressbar.visibility = View.GONE
            }


    }
    // end get my cart fun


    fun cart(){
        val center = GeoLocation(latitude, longitude)
        val radiusInM = (10 * 1000).toDouble()
     val query = Firebase.firestore.collection(CollectionCart.name)
            .whereEqualTo(CollectionCart.kCustomerId, Firebase.auth.uid)
        Tasks.whenAllComplete(query.get())
            .addOnCompleteListener {
                if (it.isSuccessful){
                  val querySnapshot=  it.result as QuerySnapshot
                   val networkCartModel = querySnapshot.toObjects(NetworkCartModel::class.java)
                  val cartModel=  networkCartModel.mapNotNull { CartModel.create(it) }
                  for (cart in cartModel) {
                      selleIdCart = cart.sellerId
                      for (product in cart.productList){

                          val productTask = Firebase.firestore.collection(CollectionProducts.name).document(product.productId)
                           Tasks.whenAllComplete(productTask.get())
                               .addOnCompleteListener {
                                     val documentSnapShot = it.result as DocumentSnapshot
                                 val networkProductModel=  documentSnapShot.toObject(NetworkProductModel::class.java)
                                   if (networkProductModel != null) {
                                    val productModel=   ProductModel.create(networkProductModel)
                                       if (productModel != null) {
                                           val modelCart = ModelCart.create(productModel, product)
                                           listCart.add(modelCart)
                                           listCreateOrder.add(CreateOrder.create(modelCart))

                                           var sum = 0

                                           listCart.forEach {

                                               sum += it.productPrice.toInt()
                                           }

                                           visibleUI()
                                           tvCartAllTotalPrice?.text =
                                               getCurrencyPrice(tvCartAllTotalPrice.context, (sum).toString())
                                           cartAllTotalPrice = sum

                                           if (productModel.id == product.productId && productModel.isAvailable != true) {

                                               deleteCart(cart.id) { _, _ -> }
                                           }
                                       }

                                   }


                               }
                      }

                    val sellerTask=  Firebase.firestore.collection(CollectionUser.name).document(selleIdCart).get()
                          Tasks.whenAllComplete(sellerTask).addOnCompleteListener {
                              if (it.isSuccessful){
                                  val documentSnapshot= it.result as DocumentSnapshot

                                  val networkUserModel = documentSnapshot.toObject(NetworkUserModel::class.java)
                                  val sellerDTO = UserDTO.create(networkUserModel!!)
                                  if (sellerDTO != null) {
                                      viewBinding.tvCarShopName.text = sellerDTO.shopName
                                      viewBinding.tvCartShopDetail.text = sellerDTO.shopSmallInfo
                                      notificationTokenSeller = sellerDTO.notificationToken.toString()
                                      Glide.with(requireContext()).load(sellerDTO.shopBannerImageUrl)
                                          .placeholder(R.drawable.ic_placeholder)
                                          .into(viewBinding.ImCartShopLogo)

                                      val distance = GeoFireUtils.getDistanceBetween(
                                          GeoLocation(
                                              sellerDTO.latitude,
                                              sellerDTO.longitude
                                          ), center
                                      )

                                      if (distance <= radiusInM) {
                                          sellerInRange = true
                                          Log.e("NST", "Matching Radius; $distance")
                                      } else {
                                          sellerInRange = false
                                          Log.e("NST", "Not Matching Radius; $distance")
                                      }

                                  }
                              }
                          }


                  }
                    adaptorCart.upDateCart(listCart)

                }

            }

    }


    // start get product fun
    private fun getMyProduct(cartProductModel: CartProductModel) {
        viewBinding.progressbar.visibility = View.VISIBLE

        Firebase.firestore.collection(CollectionProducts.name).document(cartProductModel.productId)
            .get()
            .addOnSuccessListener { document ->
                viewBinding.progressbar.visibility = View.GONE

                val networkProductModel = document.toObject(NetworkProductModel::class.java)
                val productModel = ProductModel.create(networkProductModel!!)
                if (productModel != null) {
                    val modelCart = ModelCart.create(productModel, cartProductModel)
                    listCart.add(modelCart)
                    listCreateOrder.add(CreateOrder.create(modelCart))

                    var sum = 0

                    listCart.forEach {

                        sum += it.productPrice.toInt()
                    }

                    visibleUI()
                    tvCartAllTotalPrice?.text =
                        getCurrencyPrice(tvCartAllTotalPrice.context, (sum).toString())
                    cartAllTotalPrice = sum

                    if (productModel.id == cartProductModel.productId && productModel.isAvailable != true) {

                        deleteCart(cartModel!!.id) { _, _ -> }

                    }
                }
                adaptorCart.upDateCart(listCart)
            }
    }
    // end get product fun


    // start visibleUi fun
    private fun visibleUI() {
        viewBinding.btnCartProceedToPayFloating.visibility = View.VISIBLE
        viewBinding.total.visibility = View.VISIBLE
        viewBinding.tvCartAllTotalPrice.visibility = View.VISIBLE
        viewBinding.tvCarShopName.visibility = View.VISIBLE
        viewBinding.tvCartShopDetail.visibility = View.VISIBLE
        viewBinding.ImCartShopLogo.visibility = View.VISIBLE
        viewBinding.addButton.visibility = View.VISIBLE
        viewBinding.kSpecialInstructionTV.visibility = View.VISIBLE
        viewBinding.etAddSpecialInstruction.visibility = View.VISIBLE
    }
    // end visibleUi fun


    // start send notification  fun
    private fun sendNotification(deviceId: String, items: String) {
        try {
            val jsonObject = JSONObject(
                "{'include_player_ids':['$deviceId']," +
                        "'headings':{'en':'You have a new order'}," +
                        "'contents':{'en':'Order items is: $items'}," +
                        "'android_led_color':'313F55'," +
                        "'android_accent_color':'313F55'}"
            )

            OneSignal.postNotification(jsonObject,
                object : OneSignal.PostNotificationResponseHandler {
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

    }
    // end send notification  fun

    // start delete cart fun
    private fun deleteCart(customerId: String) {
        viewBinding.progressbar.visibility = View.VISIBLE
        val db = Firebase.firestore
        val deleteData = db.collection("Cart").document(customerId)
        deleteData.delete()
            .addOnSuccessListener {
                viewBinding.progressbar.visibility = View.GONE

                viewBinding.ImCartShopLogo.visibility = View.GONE
                viewBinding.tvCarShopName.visibility = View.GONE
                viewBinding.tvCartShopDetail.visibility = View.GONE
                viewBinding.addButton.visibility = View.GONE
                viewBinding.total.visibility = View.GONE
                viewBinding.tvCartAllTotalPrice.visibility = View.GONE
                viewBinding.kSpecialInstructionTV.visibility = View.GONE
                viewBinding.etAddSpecialInstruction.visibility = View.GONE
                viewBinding.btnCartProceedToPayFloating.visibility = View.GONE

                listCart.clear()
                viewBinding.recyclerViewcart.visibility = View.GONE
                adaptorCart.notifyDataSetChanged()
                viewBinding.tvCartIsEmpty.visibility = View.VISIBLE
                viewBinding.btnCartContinueShopping.visibility = View.VISIBLE
                viewBinding.imCartEmpty.visibility = View.VISIBLE


                Log.e("delete", "Delete")


            }
            .addOnFailureListener { e ->
                Log.e("delete", "${e.message}")
                viewBinding.progressbar.visibility = View.GONE


            }
    }
    // end delete cart fun


    // start delete cart fun

    private fun deleteCart(customerId: String, callback: (Boolean, Exception?) -> Unit) {
        val db = Firebase.firestore
        val deleteData = db.collection(CollectionCart.name).document(customerId)
        deleteData.delete()
            .addOnSuccessListener {
                Log.e("NST", "Cart Deleted")
                callback(true, null)
            }
            .addOnFailureListener { exception ->
                Log.e("NST", "Error in Cart Delete: ${exception.toString()}")
                callback(false, exception)
            }
    }

    // start delete cart fun


    // start cart real time update fun
    private fun cartRealTimeUpdate() {
        FirebaseAuth.getInstance().currentUser?.let { currentUser ->
            val cartSnapRef = Firebase.firestore
                .collection(CollectionCart.name)
                .whereEqualTo(CollectionCart.kCustomerId, currentUser.uid)

            cartSnapRef.addSnapshotListener { querySnapShot, error ->
                if (error != null) {
                    Log.e("NST", "Error in Cart SnapShot: ${error.message}")
                } else {
                    Log.e("NST", "Cart SnapShot Listener called")
                    val docSnapShot = querySnapShot!!.documents.firstOrNull()
                    docSnapShot?.let {
                        val networkCartModel: NetworkCartModel? =
                            docSnapShot.toObject(NetworkCartModel::class.java)
                        if (networkCartModel != null) {
                            cartModel = CartModel.create(networkCartModel)

                            Log.e("NST", cartModel.toString())
                        } else {
                            Log.e("NST", "NetworkCartModel null in Update")
                        }
                    }
                    adaptorCart.updateCartModel(cartModel)
                    Log.e("NST", "CartModel Updated")
                }
            }
        }
    }
    // end cart real time update fun

    // start product real time update fun
    private fun productRealTimeUpdate() {
        Firebase.firestore
            .collection(CollectionProducts.name)
            .whereEqualTo(CollectionProducts.kProductSellerId, selleIdCart)
            .addSnapshotListener { querySnapShot, error ->
                if (error != null) {
                    Log.e("NST", "Error in Cart SnapShot: ${error.message}")
                } else {
                    //listCart!!.clear()
                    // listCreateOrder!!.clear()

                    val docSnapShot = querySnapShot!!.documents.firstOrNull()
                    if (docSnapShot != null) {
                        // getMyCart(customerId)

                    }
                }
            }
    }
    // start product real time update fun


    // start currency format fun
    private fun getCurrencyPrice(context: Context, price: String): String {
        val numberFormatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

        val amount = numberFormatter.format(price.toDouble())
        return context.resources.getString(R.string.amount, amount)
    }

    // start currency format fun

    override fun plusButtonTapped(productModel: ProductModel) {
        //repeatProduct(position)
    }

    override fun minusButtonTapped(productModel: ProductModel) {

    }


    override fun addButtonTapped(productModel: ProductModel) {

    }

    override fun onViewCartUpdated(totalPrice: Int, size: Int) {

    }
}





