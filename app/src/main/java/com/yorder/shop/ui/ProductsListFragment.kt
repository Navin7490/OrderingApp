package com.yorder.shop.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.Tasks
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.yorder.shop.R
import com.yorder.shop.adaptore.*
import com.yorder.shop.const.Constant
import com.yorder.shop.databinding.FragmentProductsListBinding
import com.yorder.shop.interfaces.BottomSheetListner
import com.yorder.shop.interfaces.ProductCustomisationChange
import com.yorder.shop.interfaces.SellerProductItemInterface
import com.yorder.shop.model.*
import kotlinx.android.synthetic.main.fragment_product_customisation.*
import kotlinx.android.synthetic.main.item_seller_product_list_main.*
import kotlinx.android.synthetic.main.list_product_category.*
import java.text.NumberFormat
import java.util.*
import kotlin.ClassCastException
import kotlin.collections.ArrayList

class ProductsListFragment : Fragment(), SellerProductItemInterface, ProductCustomisationChange,
    SelectedCategory {

    private lateinit var viewBinding: FragmentProductsListBinding

    private lateinit var recyclerView: RecyclerView

    private lateinit var productParentAdapter: ProductParentAdapter

    lateinit var adapterProductCategory: AdapterProductCategory

    private var tooBar: androidx.appcompat.widget.Toolbar? = null

    lateinit var activityapp: AppCompatActivity

    private var sellerId: String = ""
    lateinit var subId: String
    private var sellerDTO: UserDTO? = null
    private var cartDocId: String = ""
    var subscriptionStatus = SubscriptionStatus.NONE
    var cartModel: CartModel? = null

    lateinit var bottomSheetDialog: BottomSheetDialog

    lateinit var categoryProductList: ArrayList<String>
    private val queryListenerList: ArrayList<ListenerRegistration> = arrayListOf()

    // bottom sheet
    lateinit var addOnsAdapter: ProductAddOnsAdapter
    lateinit var variantsAdapter: ProductVariantsAdapter

    var productPrice = 0
    var count = 1
    var sellerIdcustomize: String = ""
    var idcust: String = ""
    var proprice: Int = 0

    lateinit var tvPLQ: TextView

    private lateinit var bottomsheetLitsner: BottomSheetListner
    private var flagCheck = false
    private var checkCallBack = false
    private var checkScroll = true
    private var posSelected = 0
    private var totalCount = 0

    var listner: SelectedCategory = this
    var firstVisiblePosition = 0
    var lastVisiblePosition = 0
    var selectedPosition = 0
    var categories = listOf<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityapp = AppCompatActivity()
        sellerId = arguments?.getString("sellerId", "").toString()
        subId = arguments?.getString("subId", "").toString()
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentProductsListBinding.inflate(inflater, container, false)

        categoryProductList = arrayListOf()

        //  tooBar = viewBinding.toolbarProductList

        recyclerView = viewBinding.RvProductList
        val linearLayoutManager = LinearLayoutManager(requireContext())

        recyclerView.layoutManager = linearLayoutManager

        recyclerView.setHasFixedSize(true)

        productParentAdapter = ProductParentAdapter(arrayListOf(), cartModel, this)

        recyclerView.adapter = productParentAdapter

        adapterProductCategory = AdapterProductCategory(arrayListOf(), this)

//        tooBar!!.setNavigationOnClickListener {
//            findNavController().popBackStack()
//        }

        viewBinding.ImageProductListBack.setOnClickListener {
            findNavController().popBackStack()
        }

        viewBinding.floatingViewCart.setOnClickListener {
            val bundle = Bundle().apply {
                this.putString("sellerId", sellerId)
            }
            findNavController().navigate(R.id.action_productsListFragment_to_cartFragment, bundle)
        }

        // product category
        val mLayoutManager = LinearLayoutManager(requireContext())
        mLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        viewBinding.RvProductCategory.setHasFixedSize(true)
        viewBinding.RvProductCategory.layoutManager = mLayoutManager

        adapterProductCategory = AdapterProductCategory(arrayListOf(), this)
        viewBinding.RvProductCategory.adapter = adapterProductCategory
        checkScroll = false


        //  start scrollView event

        recyclerView.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->

            val firstVisiblePosition =
                (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
            lastVisiblePosition =
                (recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
            val firstCompletelyVisiblePosition =
                (recyclerView.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()



            Log.e("scroll", "$firstCompletelyVisiblePosition")



            checkScroll = true

            if (firstVisiblePosition > -1) {

                if (firstCompletelyVisiblePosition == -1) {

                    selectedPosition = lastVisiblePosition
                    viewBinding.RvProductCategory.smoothScrollToPosition(lastVisiblePosition)

                    adapterProductCategory.notifyDataSetChanged()
                } else if (firstCompletelyVisiblePosition > -1) {

                    selectedPosition = firstCompletelyVisiblePosition

                    viewBinding.RvProductCategory.smoothScrollToPosition(
                        firstCompletelyVisiblePosition
                    )
                    adapterProductCategory.notifyDataSetChanged()


                } else {
                    selectedPosition = firstVisiblePosition

                    viewBinding.RvProductCategory.smoothScrollToPosition(
                        firstCompletelyVisiblePosition
                    )
                    adapterProductCategory.notifyDataSetChanged()
                }

            }


            //recyclerView.smoothScrollToPosition(visiblePosition)
            // recyclerView.scrollTo(scrollX,scrollY)
        }

        // category recyclerView lisetner

        //  end scrollView event


        // start info click event
        viewBinding.ImButtonInfo.setOnClickListener {
            val b = Bundle().apply {
                this.putString("sellerId", sellerId)
                this.putString("status", subscriptionStatus.value)
                this.putString("subId", subId)
            }
            findNavController().navigate(R.id.sellerInfoFragment, b)
        }
        // end info click event


        viewBinding.tvRequestedbtn.setOnClickListener {
            if (viewBinding.tvRequestedbtn.text.toString() == "Subscribe") {
                viewBinding.progressView.visibility = View.VISIBLE
                Firebase.firestore
                    .collection(CollectionSubscriptions.name)
                    .document()
                    .set(
                        hashMapOf(
                          CollectionSubscriptions.kCustomerId to Firebase.auth.currentUser!!.uid,
                            CollectionSubscriptions.kSellerId to sellerDTO?.id,
                            CollectionSubscriptions.kSubStatus to SubscriptionStatus.PENDING.value
                        )
                    ).addOnSuccessListener {
                        viewBinding.progressView.visibility = View.GONE

                        viewBinding.tvRequestedbtn.text = SubscriptionStatus.PENDING.value
                        viewBinding.tvRequestedbtn.setTextColor(
                            ContextCompat.getColor(
                                viewBinding.tvRequestedbtn.context,
                                R.color.app_bg_blue
                            )
                        )
                        viewBinding.tvRequestedbtn.background = ContextCompat.getDrawable(
                            viewBinding.tvRequestedbtn.context,
                            R.drawable.drawable_searchedit
                        )


                    }
                    .addOnFailureListener { e ->
                        viewBinding.progressView.visibility = View.GONE
                        Toast.makeText(
                            requireContext(), e.localizedMessage
                                ?: "Unable to Subscribe", Toast.LENGTH_SHORT
                        ).show()
                    }
            } else if (viewBinding.tvRequestedbtn.text.toString() == SubscriptionStatus.PENDING.value) {

                MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
                    .setMessage("Are you sure to want to cancel the Subscription request?")
                    .setNegativeButton("No") { dialog, which ->
                        dialog.dismiss()

                    }
                    .setPositiveButton("Yes") { dialog, which ->
                        viewBinding.progressView.visibility = View.VISIBLE
                        Firebase.firestore
                            .collection(CollectionSubscriptions.name)
                            .document(subId)
                            .delete()
                            .addOnSuccessListener {
                                viewBinding.progressView.visibility = View.GONE


                                viewBinding.tvRequestedbtn.text = "Subscribe"
                                viewBinding.tvRequestedbtn.setTextColor(
                                    ContextCompat.getColor(
                                        viewBinding.tvRequestedbtn.context,
                                        R.color.app_text_white
                                    )
                                )
                                viewBinding.tvRequestedbtn.background = ContextCompat.getDrawable(
                                    viewBinding.tvRequestedbtn.context,
                                    R.drawable.drawable_subscribebutton
                                )

                                dialog.dismiss()


                            }
                            .addOnFailureListener { e ->
                                viewBinding.progressView.visibility = View.GONE
                                Toast.makeText(
                                    requireContext(), e.localizedMessage
                                        ?: "Unable to Subscribe", Toast.LENGTH_SHORT
                                ).show()
                            }

                    }
                    .show()


            }
        }

        return viewBinding.root
    }


    override fun selectedItem(position: Int) {
        super.selectedItem(position)
        recyclerView.scrollToPosition(position)


    }


    override fun onResume() {
        super.onResume()
        getSeller()
        getProductsForSeller()
        getSubscription()
        cartRealTimeUpdate()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            bottomsheetLitsner = context as BottomSheetListner
            //listner = context as SelectedCategory

        } catch (e: ClassCastException) {
            e.message
        }
    }


    override fun onDestroyView() {
        Log.e("A-LC", "OrderList onDestroyView")
        // Remove Snapshot listener
        queryListenerList.forEach {
            it.remove()
        }
        queryListenerList.clear()
        super.onDestroyView()
    }
    // start bottom sheet

    private val addToCartClick = View.OnClickListener {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val customerId = currentUser?.uid

        if (customerId != null) {
            getMyCart(customerId, sellerIdcustomize)
        }
    }

    private val clearVariants = View.OnClickListener {
        variantsAdapter.clearAllSelection()
        bottomSheetDialog.variantClearButton.visibility = View.GONE

    }

    private val clearAddOns = View.OnClickListener {
        addOnsAdapter.clearAllSelection()
    }

    private fun addToCartData() {

        bottomSheetDialog.addToCartButton.isEnabled = false

        val cartProductModel = CartProductModel(
            productId = idcust,
            productQuantity = tvPLQ.text.toString().toInt(),
            productAddOnsList = addOnsAdapter.dataSource.mapNotNull { if (it.isSelected) it.id else null },
            productVariantsList = variantsAdapter.dataSource.mapNotNull { if (it.isSelected) it.id else null }
        )
        if (cartModel != null) {

            cartModel!!.productList.add(cartModel!!.productList.size, cartProductModel)

            val nProduct = NetworkCartModel.create(cartModel!!)
            Log.e("NST", nProduct.toString())


            Firebase.firestore.collection(CollectionCart.name)
                .document(cartModel!!.id)
                .set(nProduct)
                .addOnSuccessListener {
                    bottomSheetDialog.addToCartButton.isEnabled = true

                    Log.e("NST", "Product added successfully")
                    // findNavController().popBackStack()
                    bottomSheetDialog.dismiss()

                    Toast.makeText(
                        requireContext(), "Product added in cart",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .addOnFailureListener { ex ->
                    bottomSheetDialog.addToCartButton.isEnabled = true

                    Log.e("NST", ex.toString())
                    Toast.makeText(
                        requireContext(),
                        ex.message ?: "Unable to add product",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        } else {
            val newCartModel = CartModel(
                id = "id",
                customerId = FirebaseAuth.getInstance().currentUser!!.uid,
                sellerId = sellerIdcustomize,
                productList = arrayListOf(cartProductModel),
                createdBy = FirebaseAuth.getInstance().currentUser!!.uid,
                updatedBy = FirebaseAuth.getInstance().currentUser!!.uid,
                createdAt = Timestamp.now(),
                updatedAt = Timestamp.now()
            )
            val newCartDocRef = Firebase.firestore.collection(CollectionCart.name).document()
            val nProduct = NetworkCartModel.create(newCartModel)
            bottomSheetDialog.addToCartButton.isEnabled = false

            newCartDocRef.set(nProduct)
                .addOnSuccessListener {
                    bottomSheetDialog.addToCartButton.isEnabled = true

                    Log.e("NST", "New Cart")
                    Log.e("NST", "Product added successfully")
                    // findNavController().popBackStack()
                    bottomSheetDialog.dismiss()


                    Toast.makeText(
                        requireContext(), "Product added in cart",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .addOnFailureListener { ex ->
                    Log.e("NST", "New Cart Error")
                    Log.e("NST", ex.toString())
                    bottomSheetDialog.addToCartButton.isEnabled = true

                    Toast.makeText(
                        requireContext(),
                        ex.message ?: "Unable to add product",
                        Toast.LENGTH_SHORT
                    ).show()
                }

        }
    }

    private fun getMyCart(
        customerId: String,
        sellerId: String

    ) {
        Log.e("getCart", "Cart")

        val db = Firebase.firestore
        db.collection(CollectionCart.name)
            .whereEqualTo(CollectionCart.kCustomerId, customerId)
            .get()
            .addOnSuccessListener { document ->
                Log.e("getCart", "s=${document.documents}")


                if (document.documents.isNullOrEmpty()) {
                    addToCartData()
                } else {
                    for (result in document) {
                        val sId = result.getString(CollectionCart.kSellerId)

                        if (sId == sellerId) {
                            addToCartData()
                        } else {
                            MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
                                .setTitle("Replace cart item")
                                .setMessage("Do you want remove other seller product")
                                .setPositiveButton("Yes") { dialog, which ->
                                    deleteCart(cartModel!!.id) { flag, exception ->
                                        if (flag) {
                                            addToCartData()
                                        } else {
                                            Toast.makeText(
                                                requireContext(),
                                                exception!!.toString(),
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }
                                    dialog.dismiss()


                                }
                                .setNegativeButton("No") { dialog, No ->
                                    dialog.dismiss()
                                }
                                .show()

                        }
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    requireContext(),
                    e.message ?: "Unable to add product",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e("getCart", "${e.message}")

            }

    }

    override fun updateTotalPrice() {
        productPrice = proprice

        addOnsAdapter.dataSource.forEach {
            if (it.isSelected) {
                productPrice += it.price.toIntOrNull() ?: 0
            }
        }
        variantsAdapter.dataSource.forEach {
            if (it.isSelected) {
                productPrice += it.price.toIntOrNull() ?: 0
                bottomSheetDialog.variantClearButton.visibility = View.VISIBLE
            }
        }

        var qty = tvPLQ.text.toString().toInt()
        var t = productPrice * qty

        bottomSheetDialog.productPriceTv.text = "$t"
    }

    private fun getSeller() {
        val queryListener = Firebase.firestore
            .collection(CollectionUser.name)
            .document(sellerId)


            .addSnapshotListener { documentSnapshot, error ->
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
                                showAlertForSeller()
                            } else {
                                viewBinding.tvProductListTitile2.text = sellerDTO!!.shopSmallInfo
                                viewBinding.tvProductListTitile1.text = sellerDTO!!.shopName
                            }
                        }
                    } else {
                        showAlertForSeller()
                    }
                }
            }


        queryListenerList.add(queryListener)
    }

    private fun getProductsForSeller() {
        val queryListener = Firebase.firestore
            .collection(CollectionProducts.name)
            .whereEqualTo(CollectionProducts.kProductSellerId, sellerId)
            .orderBy(CollectionProducts.kProductName)
            .addSnapshotListener { querySnapshot, error ->

                Log.e("NST", "SellerProducts ProductList RealTime")
                if (error != null) {
                    Log.e("NST", "Error in Products QuerySnapshot: $error")
                    Toast.makeText(
                        requireContext(),
                        error.localizedMessage ?: "Unable to get products",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    if (querySnapshot != null) {
                        if (!querySnapshot.isEmpty) {
                            val networkProductList =
                                querySnapshot.toObjects(NetworkProductModel::class.java)


                            val productModelArray =
                                networkProductList.mapNotNull { ProductModel.create(it) }
                            categories = productModelArray.map { it.productCategory }.distinct()

                            val dataSource = arrayListOf<ProductCategoryModel>()
                            for (category in categories) {
                                val productList =
                                    productModelArray.filter { it.productCategory == category }
                                dataSource.add(ProductCategoryModel(category, productList))
                            }
                            productParentAdapter.updateDataSource(dataSource)
                            adapterProductCategory.updateDataSource(categories)
                        } else {
                            productParentAdapter.updateDataSource(arrayListOf())
                        }
                    } else {
                        productParentAdapter.updateDataSource(arrayListOf())
                    }
                }
            }

        queryListenerList.add(queryListener)
    }

    private fun getSubscription() {
        val queryListener = Firebase.firestore
            .collection(CollectionSubscriptions.name)
            .whereEqualTo(CollectionSubscriptions.kCustomerId, Firebase.auth.currentUser!!.uid)
            .whereEqualTo(CollectionSubscriptions.kSellerId, sellerId)
            .addSnapshotListener { querySnapshot, error ->
                Log.e("NST", "SellerProducts SubscriptionStatus RealTime")
                if (error != null) {
                    Log.e("NST", "Error in SubStatus QuerySnapshot: $error")
                    Toast.makeText(
                        requireContext(),
                        error.localizedMessage ?: "Unable to get subscription status",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    if (querySnapshot != null) {
                        if (!querySnapshot.isEmpty) {
                            val subDocSnap = querySnapshot.documents.first()
                            val networkSubscription =
                                subDocSnap.toObject(SubscriptionNetwork::class.java)
                            if (networkSubscription != null) {
                                val subscriptionDTO = SubscriptionDTO.create(networkSubscription)
                                if (subscriptionDTO != null) {
                                    subscriptionStatus = subscriptionDTO.subscriptionStatus
                                    if (subscriptionStatus.value == "Approved") {
                                        viewBinding.tvRequestedbtn.visibility = View.INVISIBLE
                                    } else {
                                        viewBinding.tvRequestedbtn.visibility = View.VISIBLE

                                        viewBinding.tvRequestedbtn.text = subscriptionStatus.value

                                    }
                                } else {
                                    subscriptionStatus = SubscriptionStatus.NONE
                                    viewBinding.tvRequestedbtn.visibility = View.VISIBLE

                                    viewBinding.tvRequestedbtn.text = "Subscribe"
                                    viewBinding.tvRequestedbtn.setTextColor(
                                        ContextCompat.getColor(
                                            viewBinding.tvRequestedbtn.context,
                                            R.color.app_text_white
                                        )
                                    )
                                    viewBinding.tvRequestedbtn.background =
                                        ContextCompat.getDrawable(
                                            viewBinding.tvRequestedbtn.context,
                                            R.drawable.drawable_subscribebutton
                                        )
                                }
                            } else {
                                subscriptionStatus = SubscriptionStatus.NONE
                                viewBinding.tvRequestedbtn.visibility = View.VISIBLE
                                viewBinding.tvRequestedbtn.text = "Subscribe"
                                viewBinding.tvRequestedbtn.setTextColor(
                                    ContextCompat.getColor(
                                        viewBinding.tvRequestedbtn.context,
                                        R.color.app_text_white
                                    )
                                )
                                viewBinding.tvRequestedbtn.background = ContextCompat.getDrawable(
                                    viewBinding.tvRequestedbtn.context,
                                    R.drawable.drawable_subscribebutton
                                )

                            }
                        } else {
                            subscriptionStatus = SubscriptionStatus.NONE
                            viewBinding.tvRequestedbtn.visibility = View.VISIBLE

                            viewBinding.tvRequestedbtn.text = "Subscribe"
                            viewBinding.tvRequestedbtn.setTextColor(
                                ContextCompat.getColor(
                                    viewBinding.tvRequestedbtn.context,
                                    R.color.app_text_white
                                )
                            )
                            viewBinding.tvRequestedbtn.background = ContextCompat.getDrawable(
                                viewBinding.tvRequestedbtn.context,
                                R.drawable.drawable_subscribebutton
                            )

                        }
                    } else {
                        subscriptionStatus = SubscriptionStatus.NONE
                        viewBinding.tvRequestedbtn.visibility = View.VISIBLE

                        viewBinding.tvRequestedbtn.text = "Subscribe"
                        viewBinding.tvRequestedbtn.setTextColor(
                            ContextCompat.getColor(
                                viewBinding.tvRequestedbtn.context,
                                R.color.app_text_white
                            )
                        )
                        viewBinding.tvRequestedbtn.background = ContextCompat.getDrawable(
                            viewBinding.tvRequestedbtn.context,
                            R.drawable.drawable_subscribebutton
                        )

                    }
                    Log.e("NST", "Subscription Status: ${subscriptionStatus.value}")
                }
            }
        queryListenerList.add(queryListener)
    }


    private fun cartRealTimeUpdate() {
        viewBinding.progressView.visibility = View.VISIBLE
        val queryListener = Firebase.firestore
            .collection(CollectionCart.name)
            .whereEqualTo(CollectionCart.kCustomerId, Firebase.auth.currentUser!!.uid)
            .addSnapshotListener { querySnapShot, error ->
                if (error != null) {
                    Log.e("NST", "Error in Cart SnapShot: ${error.message}")
                    viewBinding.progressView.visibility = View.GONE
                } else {
                    val docSnapShot = querySnapShot!!.documents.firstOrNull()
                    if (docSnapShot != null) {
                        val networkCartModel: NetworkCartModel? =
                            docSnapShot.toObject(NetworkCartModel::class.java)
                        if (networkCartModel != null) {
                            cartModel = CartModel.create(networkCartModel)
                        } else {
                            Log.e("NST", "NetworkCartModel null in Update")
                            cartModel = null
                        }
                    } else {
                        cartModel = null
                    }

                    productParentAdapter.updateCartModel(cartModel)
                    updateCartView()
                    viewBinding.progressView.visibility = View.GONE
                }
            }
        queryListenerList.add(queryListener)
    }

    @SuppressLint("SetTextI18n")
    private fun updateCartView() {
        if (cartModel != null) {
            var totalOfAllProductPrice = 0

            var arr = arrayListOf<ProductModel>()

            val productDocSnapshotTaskList = cartModel!!.productList.map {
                Firebase.firestore.collection(CollectionProducts.name).document(it.productId).get()
            }
            Tasks.whenAllSuccess<DocumentSnapshot>(productDocSnapshotTaskList)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val networkProductList =
                            task.result.mapNotNull { it.toObject(NetworkProductModel::class.java) }
                        val productDTOList =
                            networkProductList.mapNotNull { ProductModel.create(it) }

                        cartModel!!.productList.forEach { cartProduct ->
                            val index =
                                productDTOList.indexOfFirst { it.id == cartProduct.productId }
                            if (index > -1) {
                                val productModel = productDTOList[index]

                                if (productModel.isAvailable == true) {

                                    arr.add(productModel)

                                    // Product price
                                    var productPrice = productModel.productPrice.toIntOrNull()
                                        ?: 0
                                    // Selected Add On Price
                                    var addOnTotal = 0
                                    for (selectedAddOn in cartProduct.productAddOnsList) {
                                        val productCustomOption =
                                            productModel.productAddOns.firstOrNull { it.id == selectedAddOn }
                                        productCustomOption?.let {
                                            addOnTotal += productCustomOption.price.toIntOrNull()
                                                ?: 0

                                        }
                                    }
                                    // Selected Variation Price
                                    var variationTotal = 0
                                    for (selectedVariation in cartProduct.productVariantsList) {
                                        val productCustomOption =
                                            productModel.productVariants.firstOrNull { it.id == selectedVariation }
                                        productCustomOption?.let {
                                            variationTotal += productCustomOption.price.toIntOrNull()
                                                ?: 0
                                        }
                                    }
                                    productPrice += addOnTotal + variationTotal
                                    productPrice *= cartProduct.productQuantity

                                    totalOfAllProductPrice += productPrice
                                    viewBinding.floatingViewCart.visibility = View.VISIBLE


                                }

                            }

                        }



                        viewBinding.tvViewCartItem.text = arr.size.toString()

                        viewBinding.tvViewCartTotalPrice.text = "View Cart - ${
                            context?.let {
                                getProductPrice(
                                    it,
                                    totalOfAllProductPrice.toString()
                                )
                            }
                        }"
                    }
                }
        } else {
            viewBinding.floatingViewCart.visibility = View.GONE
        }
    }

    private fun getProductPrice(context: Context, price: String): String {
        val numberFormatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
        val amount = numberFormatter.format(price.toDouble())
        return context.resources.getString(R.string.amount, amount)
    }

    override fun onViewCartUpdated(totalPrice: Int, size: Int) {}

    override fun addButtonTapped(productModel: ProductModel) {
        //TODO(Get actual product model)
//        val productModel = productParentAdapter.dataSource[position].productList[0]
        if (!productModel.isAvailable) {
            Toast.makeText(context, "Sorry this product is not available", Toast.LENGTH_SHORT)
                .show()
        } else {
            if (subscriptionStatus == SubscriptionStatus.APPROVED) {
                if (productModel.productVariants.isNotEmpty() || productModel.productAddOns.isNotEmpty()) {
                    addProductWithNewCustomisations(productModel)
                } else {
                    if (cartModel != null && cartModel!!.sellerId != sellerId) {
                        MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
                            .setTitle("Replace cart item")
                            .setMessage("Do you want remove other seller product")
                            .setPositiveButton("Yes") { dialog, which ->
                                deleteCart(cartModel!!.id) { flag, exception ->
                                    if (flag) {
                                        addProductWithOutCustomisation(productModel)
                                    } else {
                                        Toast.makeText(
                                            requireContext(),
                                            exception!!.toString(),
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                                dialog.dismiss()
                            }
                            .setNegativeButton("No") { _, _ -> }
                            .show()
                    } else {
                        addProductWithOutCustomisation(productModel)
                    }
                }
            } else {
                Log.e("NST", "Can not add product: ${subscriptionStatus.value}")
                val message: String
                when (subscriptionStatus) {
                    SubscriptionStatus.NONE -> {
                        message = "Please subscribe to seller"
                    }
                    SubscriptionStatus.PENDING -> {
                        message = "Your subscription request is pending"
                    }
                    SubscriptionStatus.CANCELLED -> {
                        message = "Your subscription request is cancelled"
                    }
                    else -> {
                        message = "Undefine subscription status"
                    }
                }
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun plusButtonTapped(productModel: ProductModel) {
//        val productModel = productParentAdapter.dataSource[position].productList[0]
        if (productModel.productVariants.isNotEmpty() || productModel.productAddOns.isNotEmpty()) {
            MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
                .setCancelable(true)
                .setTitle("Add Product")
                .setMessage("${productModel.productName} already exist with customisation options. Do you want to repeat it or add with new options?")
                .setPositiveButton(
                    "Add New customisation"
                ) { _, _ ->
                    addProductWithNewCustomisations(productModel)
                }
                .setNegativeButton(
                    "Repeat customisation"
                ) { _, _ ->
                    repeatProduct(productModel)
                }
                .show()
        } else {
            repeatProduct(productModel)
        }
    }

    override fun minusButtonTapped(productModel: ProductModel) {
        val product =
            cartModel!!.productList.last { it.productId == productModel.id }
        val indexInCart = cartModel!!.productList.indexOf(product)

        cartDocId = cartModel!!.id

        if (product.productQuantity == 1) {
            cartModel!!.productList.removeAt(indexInCart)
        } else {
            product.productQuantity--
            cartModel!!.productList[indexInCart] = product
        }

        if (cartModel!!.productList.isEmpty()) {
            deleteCart(cartModel!!.id) { _, _ -> }
        } else {
            val nProduct = NetworkCartModel.create(cartModel!!)
            Firebase.firestore.collection(CollectionCart.name)
                .document(cartModel!!.id)
                .set(nProduct)
                .addOnSuccessListener {
                    Log.e("NST", "Product updated successfully")
                }
                .addOnFailureListener { ex ->
                    Log.e("NST", ex.toString())
                    Toast.makeText(
                        requireContext(),
                        ex.message ?: "Unable to update product",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    private fun addProductWithNewCustomisations(productModel: ProductModel) {
        bottomSheetDialog =
            BottomSheetDialog(requireContext(), R.style.CustomBottomSheetDialogTheme)


        bottomSheetDialog.setContentView(R.layout.fragment_product_customisation)


       // bottomSheetDialog.behavior.peekHeight = 2000

        bottomSheetDialog.show()


        var tvplusButton = bottomSheetDialog.findViewById<TextView>(R.id.tvPLPlus)
        var tvMinusButton = bottomSheetDialog.findViewById<TextView>(R.id.tvPLRemove)
        tvPLQ = bottomSheetDialog.findViewById<TextView>(R.id.tvPLQty)!!
        tvMinusButton?.setOnClickListener {
            if (count > 1) {
                count--
                tvPLQ?.text = count.toString()
                var price = bottomSheetDialog.productPriceTv.text.toString().toInt()
                price *= count
                updateTotalPrice()
            } else {
                tvPLQ?.text = count.toString()
            }
        }



        tvplusButton!!.setOnClickListener {
            count++
            tvPLQ?.text = count.toString()
            var price = bottomSheetDialog.productPriceTv.text.toString().toInt()
            price *= count
            updateTotalPrice()
        }




        sellerIdcustomize = productModel.sellerId
        idcust = productModel.id
        proprice = productModel.productPrice.toIntOrNull() ?: 0

        bottomSheetDialog.productNameTv.text = productModel.productName

        val addOnsArray = arrayListOf<ProductCustomOption>()
        addOnsArray.addAll(productModel.productAddOns)

        val variantArray = arrayListOf<ProductCustomOption>()
        variantArray.addAll(productModel.productVariants)

        if (addOnsArray.isEmpty()) {
            bottomSheetDialog.kAddOnTv.visibility = View.GONE
            bottomSheetDialog.addOnsClearButton.visibility = View.GONE
            bottomSheetDialog.kAddOnTvoptional.visibility = View.GONE
            bottomSheetDialog.dividerAddons.visibility = View.GONE

        }
        if (variantArray.isEmpty()) {
            bottomSheetDialog.kVariantTv.visibility = View.GONE
            bottomSheetDialog.variantClearButton.visibility = View.GONE
            bottomSheetDialog.productVariationTvLine.visibility = View.GONE
            bottomSheetDialog.dividerVariation.visibility = View.GONE

        }

        if (variantArray.isNotEmpty()) {

            var sb = StringBuffer()
            for (variation in variantArray) {

                sb.append(variation.optionName)
                sb.append(" . ")

            }
            bottomSheetDialog.productVariatinTVList.text = sb.toString()

        } else {

        }

        bottomSheetDialog.imBack.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        addOnsAdapter = ProductAddOnsAdapter(addOnsArray, this)
        variantsAdapter = ProductVariantsAdapter(variantArray, this)

        bottomSheetDialog.addOnsRecyclerView.adapter = addOnsAdapter
        bottomSheetDialog.addOnsRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        bottomSheetDialog.variantsRecyclerView.adapter = variantsAdapter
        bottomSheetDialog.variantsRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        bottomSheetDialog.addToCartButton.setOnClickListener(addToCartClick)
        bottomSheetDialog.variantClearButton.setOnClickListener(clearVariants)
        bottomSheetDialog.addOnsClearButton.setOnClickListener(clearAddOns)

        updateTotalPrice()

        productParentAdapter.cartModel?.let {
            val productInCart =
                it.productList.lastOrNull { cartProduct ->
                    cartProduct.productId == productModel.id
                }
            productInCart?.let {
                productInCart.productAddOnsList.forEach { addOnInCartId ->
                    addOnsArray.find { addOn -> addOn.id == addOnInCartId }?.isSelected = true
                }
                productInCart.productVariantsList.forEach { variantInCartId ->
                    variantArray.find { variant -> variant.id == variantInCartId }?.isSelected =

                        true

                    bottomSheetDialog.variantClearButton.visibility = View.VISIBLE
                }
            }

        }
    }


    private fun addProductWithOutCustomisation(productModel: ProductModel) {
//        val productModel = productParentAdapter.dataSource[position].productList[0]
        val cartProductModel = CartProductModel(
            productId = productModel.id,
            productQuantity = 1,
            productAddOnsList = listOf(),
            productVariantsList = listOf()
        )

        if (cartModel != null) {
            cartModel!!.productList.add(cartModel!!.productList.size, cartProductModel)
            val nProduct = NetworkCartModel.create(cartModel!!)

            Firebase.firestore.collection(CollectionCart.name)
                .document(cartModel!!.id)
                .set(nProduct)
                .addOnSuccessListener {
                    Log.e("NST", "Product added successfully")
                }
                .addOnFailureListener { ex ->
                    Log.e("NST", ex.toString())
                    Toast.makeText(
                        requireContext(),
                        ex.message ?: "Unable to add product",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        } else {
            val newCartModel = CartModel(
                id = "id",
                customerId = FirebaseAuth.getInstance().currentUser!!.uid,
                sellerId = productModel.sellerId,
                productList = arrayListOf(cartProductModel),
                createdBy = FirebaseAuth.getInstance().currentUser!!.uid,
                updatedBy = FirebaseAuth.getInstance().currentUser!!.uid,
                createdAt = Timestamp.now(),
                updatedAt = Timestamp.now()
            )
            val nProduct = NetworkCartModel.create(newCartModel)
            val newCartDocRef = Firebase.firestore.collection(CollectionCart.name).document()
            newCartDocRef.set(nProduct)
                .addOnSuccessListener {
                    Log.e("NST", "Product added successfully")
                }
                .addOnFailureListener { ex ->
                    Log.e("NST", ex.toString())
                    Toast.makeText(
                        requireContext(),
                        ex.message ?: "Unable to added product",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    private fun repeatProduct(productModel: ProductModel) {

        val product =
            cartModel!!.productList.last { it.productId == productModel.id }
        val indexInCart = cartModel!!.productList.indexOf(product)
        product.productQuantity++
        cartModel!!.productList[indexInCart] = product
        val nProduct = NetworkCartModel.create(cartModel!!)

        Firebase.firestore.collection(CollectionCart.name)
            .document(cartModel!!.id)
            .set(nProduct)
            .addOnSuccessListener {
                Log.e("NST", "Product added successfully")
            }
            .addOnFailureListener { ex ->
                Log.e("NST", ex.toString())
                Toast.makeText(
                    requireContext(),
                    ex.message ?: "Unable to added product",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun deleteCart(customerId: String, callback: (Boolean, Exception?) -> Unit) {
        val db = Firebase.firestore
        val deleteData = db.collection(CollectionCart.name).document(customerId)
        deleteData.delete()
            .addOnSuccessListener {
                Log.e("NST", "Cart Deleted")
                viewBinding.floatingViewCart.visibility = View.INVISIBLE
                callback(true, null)
            }
            .addOnFailureListener { exception ->
                Log.e("NST", "Error in Cart Delete: ${exception.toString()}")
                callback(false, exception)
            }
    }

    private fun showAlertForSeller() {
        Constant.showAlertWithListeners(
            requireContext(),
            "Invalid Seller",
            "Unable to fetch seller details, Try later",
            "OK",
            DialogInterface.OnClickListener { _, _ ->
                findNavController().popBackStack()
            },
            "",
            null
        )
    }


    // product category adapter
    inner class AdapterProductCategory(
        val categoryList: ArrayList<String>,
        val listner: SelectedCategory
    ) : RecyclerView.Adapter<AdapterProductCategory.ViewHolderCategoryName>() {

        var lastSelection = 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderCategoryName {
            val layoutInflater: LayoutInflater = LayoutInflater.from(context)
            val view = layoutInflater.inflate(R.layout.list_product_category, parent, false)
            return ViewHolderCategoryName(view!!)
        }

        override fun onBindViewHolder(holder: ViewHolderCategoryName, position: Int) {


            holder.tvName.text = categoryList[position]


            if (position == selectedPosition) {

                holder.tvName.setTextColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.app_text_black
                    )
                )
            } else {

                holder.tvName.setTextColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.app_text_product_category_gray
                    )
                )

            }
        }

        fun updateDataSource(data: List<String>) {
            categoryList.clear()
            categoryList.addAll(data)
            notifyDataSetChanged()
        }

        override fun getItemCount(): Int {
            return categoryList.size
        }


        inner class ViewHolderCategoryName(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var tvName: TextView = itemView.findViewById(R.id.tvProductCategory)

            init {

                itemView.setOnClickListener {
                    selectedPosition = adapterPosition
                    listner.selectedItem(selectedPosition)
                    Log.e("Position", "PositionClick=$lastSelection")
                    notifyDataSetChanged()
                }


            }

        }
    }
}

interface SelectedCategory {
    fun selectedItem(position: Int) {

    }

}
