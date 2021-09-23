package com.yorder.shop.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.yorder.shop.R
import com.yorder.shop.adaptore.AdapterDiscover
import com.yorder.shop.adaptore.SellerAdapter
import com.yorder.shop.adaptore.SellerCategoryInterface
import com.yorder.shop.const.Constant
import com.yorder.shop.databinding.FragmentSellersBinding
import com.yorder.shop.model.*
import com.yorder.shop.utils.AppDelegate
import com.yorder.shop.utils.Constants
import com.onesignal.OneSignal
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList


interface SubscriptionStateChange<T> : RecyclerViewPagingInterface<T> {
    fun subscribeTapped(sellerDTO: UserDTO) // accept-ignore
}

class SellersFragment : Fragment(), AdapterView.OnItemSelectedListener,
    SubscriptionStateChange<UserDTO>,
    SellerCategoryInterface {

    private var searchText: String = ""
    lateinit var viewBinding: FragmentSellersBinding
    private var listSellers: ArrayList<ModelSellers>? = null

    private var progressBar: CardView? = null
    private var customerId: String? = null

    // location
    private val permissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    private var locationText = ""

    private val permissionRequestCode = 200
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    private lateinit var selectedCategory: ModelDiscover
    private val queryListenerList: ArrayList<ListenerRegistration> = arrayListOf()
    var lastDocument: DocumentSnapshot? = null
    var reachedEnd = false
    var pageSize: Long = 10
    private lateinit var sellerAdapter: SellerAdapter
    val user = FirebaseAuth.getInstance()
    lateinit var userDTOArray: ArrayList<UserDTO>

    // Search Handler
    var searchHandler: Handler? = null




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.e("A-LC", "SellerList onCreateView")
        viewBinding = FragmentSellersBinding.inflate(inflater, container, false)

        val constant = context?.let { Constant(it) }
        constant?.setupID()
        customerId = user.currentUser?.uid.toString()
        Log.e("idNavin","$customerId")

        sellerAdapter = SellerAdapter(arrayListOf(), this)

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())

        progressBar = viewBinding.progressbar

        viewBinding.sellerRV.layoutManager = LinearLayoutManager(requireContext())
        viewBinding.sellerRV.setHasFixedSize(false)
        viewBinding.sellerRV.adapter = sellerAdapter

        listSellers = ArrayList()

        val listDiscover: ArrayList<ModelDiscover> = ArrayList()
        listDiscover.add(ModelDiscover("1", "All"))
        listDiscover.add(ModelDiscover("2","Apparel"))
        listDiscover.add(ModelDiscover("3", "Fashion"))
        listDiscover.add(ModelDiscover("4", "Food"))
        listDiscover.add(ModelDiscover("5", "Jewellery"))
        listDiscover.add(ModelDiscover("6", "Snacks"))
        listDiscover.add(ModelDiscover("7", "Sweet"))
        selectedCategory = AppDelegate.applicationContext().selectedCategory

        // Seller Category RecyclerView
        val mLayoutManager = LinearLayoutManager(requireContext())
        mLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        viewBinding.sellerCategoryRV.layoutManager = mLayoutManager
        viewBinding.sellerCategoryRV.setHasFixedSize(true)

        val adapterDiscover = AdapterDiscover(
            listDiscover,
            this,
            selectedCategory
        )
        viewBinding.sellerCategoryRV.adapter = adapterDiscover
        //

        reachedEnd = false
        lastDocument = null
        userDTOArray = ArrayList()

        //getSellerList()


        realTimeUpdateSubscription()

        viewBinding.etSearchView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrBlank()) {
                    if (searchText.isNotBlank()) {
                        searchHandler?.removeCallbacksAndMessages(null)
                        searchHandler = Handler(Looper.getMainLooper())
                        searchHandler?.postDelayed({
                            // Set SearchText to empty in query result
                            reachedEnd = false
                            lastDocument = null
                            getSellerListByCategory(selectedCategory, "")
                        }, 1000)
                    }
                } else {
                    searchHandler?.removeCallbacksAndMessages(null)
                    searchHandler = Handler(Looper.getMainLooper())
                    searchHandler?.postDelayed({
                        // Update SearchText in query result
                        reachedEnd = false
                        lastDocument = null
                        Log.e("NST-S", "Search Text: $s")
                        getSellerListByCategory(selectedCategory, s.toString())
                    }, 1000)
                }
            }
        })

        searchText = ""
        viewBinding.etSearchView.setText("")

        viewBinding.etSearchView.setOnEditorActionListener { v, actionId, event ->
            if (((event?.action ?: -1) == KeyEvent.ACTION_DOWN) ||
                actionId == EditorInfo.IME_ACTION_SEARCH
            ) {
                val inputMethodManager =
                    activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(v.windowToken, 0)
                v.clearFocus()
                Log.e("NST-S", "Search Pressed")
                return@setOnEditorActionListener true
            }
            Log.e("NST-S", "Returning without Search Pressed")
            return@setOnEditorActionListener false
        }

//        getSellerListByCategory(selectedCategory, "")

        return viewBinding.root
    }

    override fun onResume() {
        Log.e("A-LC", "SellerList onResume")
        super.onResume()
        if (latitude == 0.0 || longitude == 0.0) {
            checkAndRequestPermission()
        } else {
            viewBinding.tvSellerNear.text = locationText
            getSellerListByCategory(selectedCategory, searchText)
        }
    }

    override fun onDestroyView() {
        Log.e("A-LC", "SellerList onDestroyView")
        queryListenerList.forEach {
            it.remove()
        }
        queryListenerList.clear()
        super.onDestroyView()
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
            locationPermissionGranted()
        }
    }

    private fun checkPermissionFor(param: String): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            param
        ) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    private fun locationPermissionGranted() {
        if (isLocationEnable()) {

           viewBinding.progressbar.visibility =View.VISIBLE
            fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
                val location = task.result
                if (location != null) {
                    viewBinding.progressbar.visibility =View.GONE

                    try {

                        val geoCoder = Geocoder(requireContext(), Locale.getDefault())
                        val addressList: List<Address>? =
                                geoCoder.getFromLocation(location.latitude, location.longitude, 1)

                        if (addressList != null && addressList.isNotEmpty()) {
                            val address = addressList.first()
                            if (address.hasLatitude() && address.hasLongitude()) {
                                if (AppDelegate.applicationContext().userDTO != null && AppDelegate.applicationContext().userDTO!!.id == "ERlZWW72fgWYQ1bv8TLXJTE8sNp1") {
                                    latitude = 23.0426847
                                    longitude = 72.5469402
                                } else {
                                    latitude = address.latitude
                                    longitude = address.longitude
                                }

                                updateUserLocation()
                                Log.e("NST", address.toString())



                                if(address.subLocality.isNullOrEmpty()){
                                    locationText = address.locality
                                    viewBinding.tvSellerNear.text = locationText
                                }else{
                                    locationText = "${address.subLocality}, ${address.locality}"
                                    viewBinding.tvSellerNear.text = locationText
                                }



                                getSellerListByCategory(selectedCategory, searchText)
                            }
                        } else {
                            Log.e("NST", "AddressList Null or Empty")
                        }
                    } catch (e: Exception) {
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

    private fun isLocationEnable(): Boolean {
        val locationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return LocationManagerCompat.isLocationEnabled(locationManager)
    }

    private fun getSellerListByCategory(category: ModelDiscover, textToSearch: String) {
        viewBinding.progressbar.visibility = View.VISIBLE

        if (!reachedEnd) {
            val center = GeoLocation(latitude, longitude)
            val radiusInM = (10 * 1000).toDouble()
            val bounds = GeoFireUtils.getGeoHashQueryBounds(center, radiusInM)
            val sellerGeoQuery: ArrayList<Task<QuerySnapshot>> = ArrayList()
            Log.e("NST-S", "TextToSearch $textToSearch")
            for (bound in bounds) {
                var query = Firebase.firestore
                    .collection(CollectionUser.name)
                    .orderBy(CollectionUser.kGeoHash)
                    .whereEqualTo(CollectionUser.kUserType, UserType.SELLER.value)

                if (category.name != "All" && textToSearch.isBlank()) {
                    query = query.whereArrayContains(CollectionUser.kShopCategory, category.name)
                }

                if (textToSearch.isNotBlank()) {
                    //query = query.whereEqualTo(CollectionUser.kShopName, textToSearch)
                    query = query.whereArrayContains(
                        CollectionUser.kSearchKeywords,
                        textToSearch.toLowerCase())
                }

                query = query.startAt(bound.startHash).endAt(bound.endHash)

                if (lastDocument != null) {
                    query = query.startAfter(lastDocument!!)
                }

                query = query.limit(pageSize)

                sellerGeoQuery.add(query.get())
            }


            Tasks.whenAllComplete(sellerGeoQuery)
                .addOnCompleteListener { response ->
                    if (response.isSuccessful) {
                        val resultTasks = response.result
                        val sellerDTOList = ArrayList<UserDTO>()

                        var docsCount = 0
                        for (resultTask in resultTasks) {

                            val querySnapshot: QuerySnapshot = resultTask.result as QuerySnapshot

                            docsCount += querySnapshot.documents.size
                            lastDocument = querySnapshot.documents.lastOrNull()

                            for (docSnapShot in querySnapshot.documents) {
                                val networkSeller =
                                    docSnapShot.toObject(NetworkUserModel::class.java)
                                if (networkSeller != null) {
                                    val sellerDTO = UserDTO.create(networkSeller)
                                    if (sellerDTO != null) {

                                        if (category.name != "All" && textToSearch.isNotBlank()) {
                                            if (sellerDTO.shopCategory.contains(category.name)) {
                                                val distanceInM = GeoFireUtils.getDistanceBetween(
                                                    GeoLocation(
                                                        sellerDTO.latitude,
                                                        sellerDTO.longitude
                                                    ),
                                                    center
                                                )
                                                if (distanceInM <= radiusInM) {
                                                    Log.e(
                                                        "NST",
                                                        "Matching Radius; $distanceInM")
                                                    sellerDTOList.add(sellerDTO)

                                                } else {

                                                    Log.e(
                                                        "NST",
                                                        "Not Matching Radius; $distanceInM"
                                                    )
                                                }
                                            } else {
                                                continue
                                            }
                                        } else {
                                            val distanceInM = GeoFireUtils.getDistanceBetween(
                                                GeoLocation(
                                                    sellerDTO.latitude,
                                                    sellerDTO.longitude
                                                ),
                                                center
                                            )
                                            if (distanceInM <= radiusInM) {
                                                Log.e("NST", "Matching Radius; $distanceInM")
                                                sellerDTOList.add(sellerDTO)

                                            } else {
                                                Log.e("NST", "Not Matching Radius; $distanceInM")
                                            }
                                        }

                                    }
                                }
                            }
                        }

                        reachedEnd = docsCount < pageSize.toInt()

                        val subscriptionTaskSnapshotList = sellerDTOList.map {
                            Firebase.firestore
                                .collection(CollectionSubscriptions.name)
                                .whereEqualTo(CollectionSubscriptions.kCustomerId, Firebase.auth.uid)
                                .whereEqualTo(CollectionSubscriptions.kSellerId, it.id)
                                .get()
                        }


                        val finalTask =
                            Tasks.whenAllSuccess<QuerySnapshot>(subscriptionTaskSnapshotList)
                        finalTask.addOnSuccessListener { querySnapshotList ->

                            for (sellerDTO in sellerDTOList) {
                                for (subQuerySnapShot in querySnapshotList) {
                                    val queryDocSnapShot = subQuerySnapShot.firstOrNull()
                                    if (queryDocSnapShot != null) {
                                        val subscriptionNetwork =

                                            queryDocSnapShot.toObject(SubscriptionNetwork::class.java)
                                        if (sellerDTO.id == subscriptionNetwork.seller_id) {

                                            val subscriptionDTO =
                                                SubscriptionDTO.create(subscriptionNetwork)
                                            if (subscriptionDTO != null) {
                                                sellerDTO.subStatus =
                                                    subscriptionDTO.subscriptionStatus
                                                Log.e("NST","${sellerDTO.subStatus}")
                                                sellerDTO.subId=subscriptionDTO.id

                                               // break
                                            } else {
                                                sellerDTO.subStatus = SubscriptionStatus.NONE
                                               // break
                                            }
                                        } else {
                                            //sellerDTO.subStatus = SubscriptionStatus.NONE
                                            //break
                                        }
                                    } else {
                                        //sellerDTO.subStatus = SubscriptionStatus.NONE
                                       // break
                                    }
                                }
                            }

                            /*if (category != selectedCategory) {
                                sellerAdapter.updateDataSource(sellerDTOList)
                                selectedCategory = category
                            } else {
                                sellerAdapter.injectNextBatch(sellerDTOList)
                            }*/
                            if (category != selectedCategory) {
                                sellerAdapter.updateDataSource(sellerDTOList)
                                selectedCategory = category
                                AppDelegate.applicationContext().selectedCategory = selectedCategory
                                searchText = textToSearch
                            } else {
                                if (searchText != textToSearch) {
                                    searchText = textToSearch
                                    sellerAdapter.updateDataSource(sellerDTOList)
                                } else {
                                    sellerAdapter.injectNextBatch(sellerDTOList)
                                }
//                                if (sellerDTOList.isEmpty()){
//                                    viewBinding.noDataTV.visibility=View.VISIBLE
//
//
//                                }else{
//
//                                }
                            }
                            viewBinding.progressbar.visibility = View.GONE
                        }.addOnFailureListener { exception ->
                            Log.e(
                                "NST",
                                "Error in Seller Category Chain\n${exception.message ?: "Unknown error"}"
                            )
                            viewBinding.progressbar.visibility = View.GONE
                        }
                    } else {
                        Log.e("NST", "Response not successful for Geo Seller Query")
                        Log.e("NST", "${response.exception?.localizedMessage}")
                        viewBinding.progressbar.visibility = View.GONE
                    }
                }
        } else {
            // By Location Reached End
            Log.e("NST", "All Seller docs by location fetched ")
            viewBinding.progressbar.visibility = View.GONE
        }
    }

    override fun didScrolledToEnd(position: Int) {
        Log.e("NST", "Customer Reached end: $position")
        getSellerListByCategory(selectedCategory, searchText)
    }

    override fun didSelectItem(dataItem: UserDTO, position: Int) {
        val bundle = Bundle().apply {
            putString("sellerId", dataItem.id)
            putString("subId", dataItem.subId)
            putString("shopName", dataItem.shopName)
            putString("shopDescription", dataItem.shopSmallInfo)
            putString("shopBannerImage", dataItem.shopBannerImageUrl)
        }

        findNavController().navigate(R.id.action_sellersFragment_to_productsListFragment, bundle)
    }

    override fun dataSourceDidUpdate(size: Int) {
        if (size>0) {
            viewBinding.sellerRV.visibility = View.VISIBLE
            viewBinding.noDataTV.visibility = View.GONE
        } else{
            viewBinding.sellerRV.visibility = View.GONE
            viewBinding.noDataTV.visibility = View.VISIBLE
        }
    }

    override fun subscribeTapped(sellerDTO: UserDTO) {
        if (sellerDTO.subStatus == SubscriptionStatus.NONE) {
            progressBar!!.visibility = View.VISIBLE

            Firebase.firestore
                .collection(CollectionSubscriptions.name)
                .document()
                .set(
                    hashMapOf(
                        CollectionSubscriptions.kCustomerId to Firebase.auth.currentUser!!.uid,
                        CollectionSubscriptions.kSellerId to sellerDTO.id,
                        CollectionSubscriptions.kSubStatus to SubscriptionStatus.PENDING.value
                    )
                ).addOnSuccessListener {
                    progressBar!!.visibility = View.GONE


                    sendNotification(sellerDTO.notificationToken)
                    sellerDTO.subStatus = SubscriptionStatus.PENDING
                    sellerAdapter.updateSubscription(sellerDTO)

                }
                .addOnFailureListener { e ->
                    progressBar!!.visibility = View.GONE
                    Toast.makeText(
                        requireContext(), e.localizedMessage
                            ?: "Unable to Subscribe", Toast.LENGTH_SHORT
                    ).show()
                }
        } else if (sellerDTO.subStatus == SubscriptionStatus.PENDING) {
                MaterialAlertDialogBuilder(requireContext(),R.style.AlertDialogTheme)
                    .setMessage("Are you sure to want to cancel the Subscription request?")
                    .setNegativeButton("No") { dialog, which ->
                        dialog.dismiss()

                    }
                    .setPositiveButton("Yes") { dialog, which ->
                        progressBar!!.visibility = View.VISIBLE
                        Firebase.firestore
                            .collection(CollectionSubscriptions.name)
                            .document(sellerDTO.subId)
                            .delete()
                            .addOnSuccessListener {

                                progressBar!!.visibility = View.GONE
//                                sendNotification(sellerDTO.notificationToken, "")
                                sellerDTO.subStatus = SubscriptionStatus.NONE
                                sellerDTO.subId = ""
                                sellerAdapter.updateSubscription(sellerDTO)
                                dialog.dismiss()


                            }.addOnFailureListener { e ->
                                progressBar!!.visibility = View.GONE
                                Toast.makeText(
                                    requireContext(), e.localizedMessage
                                        ?: "Unable to Subscribe", Toast.LENGTH_SHORT
                                ).show()
                            }

                    }
                    .show()


        }
    }

    private fun realTimeUpdateSubscription() {
        Log.e("NAVIN", "realTimeUpdateSubscription")
        val db = Firebase.firestore

        val customerId = Firebase.auth.currentUser?.uid.toString()
        db.collection(CollectionSubscriptions.name)
            .whereEqualTo(CollectionSubscriptions.kCustomerId, customerId)
            .addSnapshotListener { querySnapShot, error ->
                Log.e("NST", "Subscription realTime Called")
                if (error != null) {
                    Log.e("NST", "Subscription realTime error: ${error}")
                } else {
                    if (querySnapShot != null) {
                        val networkSubscriptionList = querySnapShot.toObjects(SubscriptionNetwork::class.java)
                        val subscriptionDTOList = networkSubscriptionList.mapNotNull { SubscriptionDTO.create(it) }
                        Log.e("NST", "Subscription realTime Count: ${subscriptionDTOList.size}")
                        sellerAdapter.updateSubscriptions(subscriptionDTOList)
                    } else {
                        Log.e("NST", "Subscription realTime querySnapShot null")
                    }
                    /*val docSnap = querySnapShot?.documents?.firstOrNull()

                    val subNetwork = docSnap?.toObject(SubscriptionNetwork::class.java)
                    val subDTO = subNetwork?.let { SubscriptionDTO.create(it) }

                    if (subDTO != null) {
                        userDTOArray.firstOrNull { sellerDTO ->
                            sellerDTO.id == subDTO.sellerId
                        }?.subStatus = subDTO.subscriptionStatus
                    }
                    for (userDTO in userDTOArray) {

                        sellerAdapter.updateSubscription(userDTO)

                    }*/
                }
            }
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }

    override fun didSelect(category: ModelDiscover) {
        if (selectedCategory != category) {
            lastDocument = null
            reachedEnd = false
            getSellerListByCategory(category, searchText)
        }
    }

    private fun updateUserLocation() {
        val userGeohash = GeoFireUtils.getGeoHashForLocation(GeoLocation(latitude, longitude))
        val userData = hashMapOf<String, Any>(
            CollectionUser.kGeoHash to userGeohash,
            CollectionUser.kLatitude to latitude,
            CollectionUser.kLongitude to longitude
        )
        Firebase.firestore
            .collection(CollectionUser.name)
            .document(customerId ?: "")
            .update(userData)
            .addOnSuccessListener {
                Log.e("NST", "User location updated in Firebase")
            }.addOnFailureListener { e ->
                Log.e("NST", "Unable to update User location in Firebase")
                e.printStackTrace()
            }
    }

    private fun sendNotification(notificationId: String) {
        try {
            val title = "New subscription request!"
            val user = AppDelegate.applicationContext().userDTO
            val message = if (user != null) { "${user.firstName} ${user.lastName} has sent you a subscription request" } else { "You have received a new subscription request" }
            val jsonObject = JSONObject(
                "{'include_player_ids':['$notificationId']," +
                        "'headings':{'en':'$title'}," +
                        "'contents':{'en':'$message'}," +
                        "'android_group':'123'," +
                        "'android_led_color':'313F55'," +
                        "'android_accent_color':'313F55'}"
            )

            OneSignal.postNotification(jsonObject,
                object : OneSignal.PostNotificationResponseHandler {
                    override fun onSuccess(response: JSONObject?) {
                        Log.e("onSuccess", response.toString())
                        Toast.makeText(context, "send", Toast.LENGTH_SHORT).show()
                    }

                    override fun onFailure(response: JSONObject?) {
                        Log.e("onFailure", response.toString())
                    }
                })
        } catch (e: JSONException) {
            Log.e("JSONException", "${e.message}")
        }

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
                        Constants.showAlertWithListeners(requireContext(),
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
                        Constants.showAlertWithListeners(requireContext(),
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
}




