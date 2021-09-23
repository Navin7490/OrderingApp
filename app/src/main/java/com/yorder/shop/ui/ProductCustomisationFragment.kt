package com.yorder.shop.ui

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.yorder.shop.R
import com.yorder.shop.adaptore.ProductAddOnsAdapter
import com.yorder.shop.adaptore.ProductVariantsAdapter
import com.yorder.shop.databinding.FragmentProductCustomisationBinding
import com.yorder.shop.interfaces.BottomSheetListner
import com.yorder.shop.interfaces.ProductCustomisationChange
import com.yorder.shop.model.*
import kotlinx.android.synthetic.main.fragment_product_customisation.*


class ProductCustomisationFragment : BottomSheetDialogFragment(), ProductCustomisationChange,BottomSheetListner {
    lateinit var binding: FragmentProductCustomisationBinding
    lateinit var addOnsAdapter: ProductAddOnsAdapter
    lateinit var variantsAdapter: ProductVariantsAdapter
    lateinit var productModel: ProductModel
    var cartModel: CartModel? = null
    var productPrice = 0
    var count = 1
    var totalPrice: String = ""
    lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var dialog: BottomSheetDialog
    private lateinit var behavior: BottomSheetBehavior<View>


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialog = BottomSheetDialog(requireContext(), R.style.CustomBottomSheetDialogTheme)
        dialog.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            val parentLayout =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            parentLayout?.let { it ->
                val behaviour = BottomSheetBehavior.from(it)
                setupFullHeight(it)
                behaviour.state = BottomSheetBehavior.STATE_EXPANDED
            }


        }
        return dialog

    }

    private fun setupFullHeight(bottomSheet: View) {
        val layoutParams = bottomSheet.layoutParams
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
        bottomSheet.layoutParams = layoutParams
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        binding = FragmentProductCustomisationBinding.inflate(inflater, container, false)
        setStyle(DialogFragment.STYLE_NORMAL,R.style.CustomBottomSheetDialogTheme)
        dialog = BottomSheetDialog(requireContext(),R.style.CustomBottomSheetDialogTheme)
        dialog.setContentView(R.layout.fragment_product_customisation)




        dialog.show()
        productModel = arguments?.getSerializable("productModel") as ProductModel
        cartModel = arguments?.getSerializable("cartModel") as? CartModel

        binding.productNameTv.text = productModel.productName

        val sb = StringBuffer()

        if (productModel.productVariants.isNotEmpty()) {
            for (variation in productModel.productVariants) {

                sb.append(variation.optionName)
                sb.append(" . ")

            }
            binding.productVariatinTVList.text = sb.toString()

        }


        val addOnsArray = arrayListOf<ProductCustomOption>()
        addOnsArray.addAll(productModel.productAddOns)

        val variantArray = arrayListOf<ProductCustomOption>()
        variantArray.addAll(productModel.productVariants)



        cartModel?.let {
            val productInCart =
                it.productList.lastOrNull { cartProduct -> cartProduct.productId == productModel.id }
            productInCart?.let {
                productInCart.productAddOnsList.forEach { addOnInCartId ->
                    addOnsArray.find { addOn -> addOn.id == addOnInCartId }?.isSelected = true
                }
                productInCart.productVariantsList.forEach { variantInCartId ->
                    variantArray.find { variant -> variant.id == variantInCartId }?.isSelected =
                        true
                }
            }
        }

        addOnsAdapter = ProductAddOnsAdapter(addOnsArray, this)
        variantsAdapter = ProductVariantsAdapter(variantArray, this)

        binding.addOnsRecyclerView.adapter = addOnsAdapter
        binding.addOnsRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        binding.variantsRecyclerView.adapter = variantsAdapter
        binding.variantsRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        binding.addToCartButton.setOnClickListener(addToCartClick)
        binding.variantClearButton.setOnClickListener(clearVariants)
        binding.addOnsClearButton.setOnClickListener(clearAddOns)

        updateTotalPrice()


        binding.tvPLRemove.setOnClickListener {
            if (count > 1) {
                count--
                binding.tvPLQty.text = count.toString()
                var price = binding.productPriceTv.text.toString().toInt()
                price *= count
                updateTotalPrice()
            } else {
                binding.tvPLQty.text = count.toString()
            }
        }

        binding.tvPLPlus.setOnClickListener {
            count++
            binding.tvPLQty.text = count.toString()
            var price = binding.productPriceTv.text.toString().toInt()
            price *= count
            updateTotalPrice()
        }

        return binding.root
    }

    private val addToCartClick = View.OnClickListener {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val customerId = currentUser?.uid
        val sellerId = productModel.sellerId
        var qty = binding.tvPLQty.text.toString().toInt()

        if (customerId != null) {
            getMyCart(customerId, sellerId, qty)
        }
    }

    private val clearVariants = View.OnClickListener {
        variantsAdapter.clearAllSelection()
        binding.variantClearButton.visibility = View.GONE

    }

    private val clearAddOns = View.OnClickListener {
        addOnsAdapter.clearAllSelection()
    }

    private fun addToCartData(qty: Int) {

        binding.addToCartButton.isEnabled = false
        val cartProductModel = CartProductModel(
            productId = productModel.id,
            productQuantity = qty,
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
                    binding.addToCartButton.isEnabled = true

                    Log.e("NST", "Product added successfully")
                    findNavController().popBackStack()
                    Toast.makeText(
                        requireContext(), "Product added in cart",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .addOnFailureListener { ex ->
                    binding.addToCartButton.isEnabled = true

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
            val newCartDocRef = Firebase.firestore.collection(CollectionCart.name).document()
            val nProduct = NetworkCartModel.create(newCartModel)
            binding.addToCartButton.isEnabled = false

            newCartDocRef.set(nProduct)
                .addOnSuccessListener {
                    binding.addToCartButton.isEnabled = true

                    Log.e("NST", "New Cart")
                    Log.e("NST", "Product added successfully")
                    findNavController().popBackStack()

                    Toast.makeText(
                        requireContext(), "Product added in cart",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .addOnFailureListener { ex ->
                    Log.e("NST", "New Cart Error")
                    Log.e("NST", ex.toString())
                    binding.addToCartButton.isEnabled = true

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
        sellerId: String,
        qty: Int

    ) {
        Log.e("getCart", "Cart")

        val db = Firebase.firestore
        db.collection(CollectionCart.name)
            .whereEqualTo(CollectionCart.kCustomerId, customerId)
            .get()
            .addOnSuccessListener { document ->
                Log.e("getCart", "s=${document.documents}")


                if (document.documents.isNullOrEmpty()) {
                    addToCartData(qty)
                } else {
                    for (result in document) {
                        val cartDoId = result.id

                        val cId = result.getString("customer_id")
                        val sId = result.getString("seller_id")

                        if (sId == sellerId) {
                            addToCartData(qty)


                        } else {
                            MaterialAlertDialogBuilder(requireContext())
                                .setTitle("Replace cart item?")
                                .setMessage("Do you want Remove other seller Product")
                                .setCancelable(false)
                                .setPositiveButton("yes") { dialog, ok ->
                                    dialog.dismiss()
                                    deleteCart(cartDoId, qty)
                                    // addToCartData()

                                }
                                .setNeutralButton("No") { dialog, No ->
                                    dialog.dismiss()
                                }
                                .show()

                        }

                        // val key = pId as HashMap<*, *>


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


    private fun deleteCart(customerId: String, qty: Int) {
        val db = Firebase.firestore
        val deleteData = db.collection("Cart").document(customerId)
        deleteData.delete()
            .addOnSuccessListener {
                Log.e("delete", "Delete")
                if (cartModel != null) {
                    cartModel = null
                    addToCartData(qty)
                }
                // Toast.makeText(requireContext(), "Deleted", Toast.LENGTH_LONG).show()


            }
            .addOnFailureListener { e ->
                Log.e("delete", "${e.message}")

                //Toast.makeText(requireContext(), "${e.message}:delete error", Toast.LENGTH_LONG)
                //  .show()

            }
    }


    override fun updateTotalPrice() {
        productPrice = productModel.productPrice.toIntOrNull() ?: 0


        addOnsAdapter.dataSource.forEach {
            if (it.isSelected) {

                productPrice += it.price.toIntOrNull() ?: 0

            }
        }
        variantsAdapter.dataSource.forEach {
            if (it.isSelected) {
                productPrice += it.price.toIntOrNull() ?: 0
                binding.variantClearButton.visibility = View.VISIBLE

            }


        }

        var qty = binding.tvPLQty.text.toString().toInt()
        var t = productPrice * qty

        binding.productPriceTv.text = "$t"
    }




    companion object {
        const val TAG = "CustomBottomSheetDialogFragment"

    }

    override fun onclick(productModel: ProductModel, cartModel: CartModel) {
    }


}