package com.yorder.shop.adaptore

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.yorder.shop.R
import com.yorder.shop.interfaces.SellerProductItemInterface
import com.yorder.shop.model.CartModel
import com.yorder.shop.model.ProductModel
import java.text.NumberFormat
import java.util.*


class SellerProductListAdapter(val context: Context,
                               val dataSource: ArrayList<ProductModel>,
                               var cartModel: CartModel?,
                               val listener: SellerProductItemInterface
) : RecyclerView.Adapter<SellerProductListAdapter.ViewHolder>() {

    private val numberFormatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    var isavalible = true
    var sb :StringBuffer?= null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.list_products, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val productModel = dataSource[position]
        sb = StringBuffer()

        with(holder) {
            val isavalible = productModel.isAvailable

            if (productModel.productVariants.isNotEmpty()){

                productVariationNameTv.visibility = View.VISIBLE
                for (i in 0 until  productModel.productVariants.size){

                    sb!!.append(productModel.productVariants[i].optionName)
                    sb!!.append(" . ")

                }
                productVariationNameTv.text = sb.toString()

            } else {
                productVariationNameTv.visibility = View.GONE

            }


            productNameTv.text = productModel.productName
            productPriceTv.text = getProductPrice(productPriceTv.context, productModel)
            Glide.with(holder.itemView.context).load(productModel.productImageUrl)
                    .placeholder(R.drawable.ic_placeholder)
                    .into(productImageView)

                if (isProductInCart(productModel)) {
                    // Product exist in cart
                    addToCartButton.visibility = View.GONE
                    starImageView.visibility = View.GONE
                    stepperContainerView.visibility = View.VISIBLE

                    var quantity = 0
                    cartModel!!.productList.forEach {
                        if (it.productId == productModel.id) {
                            quantity += it.productQuantity
                        }
                    }
                    stepperQuantityTv.text = quantity.toString()
                } else {
                    // Product does NOT exist in cart
                    addToCartButton.visibility = View.VISIBLE
                    starImageView.visibility = View.VISIBLE
                    stepperContainerView.visibility = View.GONE
                }

                if (productModel.productAddOns.isNotEmpty() || productModel.productVariants.isNotEmpty()) {
                    starImageView.visibility = View.VISIBLE
                    customizableTv.visibility = View.GONE
                } else {
                    starImageView.visibility = View.GONE
                    customizableTv.visibility = View.GONE
                }




        }


    }

    private fun getProductPrice(context: Context, productModel: ProductModel): String {
        val amount = numberFormatter.format(productModel.productPrice.toDouble())
        return context.resources.getString(R.string.amount, amount)
    }

    fun updateDataSource(data: List<ProductModel>) {
        dataSource.clear()
        dataSource.addAll(data)
        notifyDataSetChanged()
    }

    fun updateCartModel(cart: CartModel?) {
        cartModel = cart
        notifyDataSetChanged()
    }

     fun isProductInCart(product: ProductModel): Boolean {
        if (cartModel != null) {
            for (cartProduct in cartModel!!.productList) {
                if (cartProduct.productId == product.id) {
                    return true
                }
            }
            return false
        } else {
            return false
        }
    }

    override fun getItemCount(): Int {
        return dataSource.size
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productNameTv: TextView = itemView.findViewById(R.id.tvProductName)
        val productVariationNameTv: TextView = itemView.findViewById(R.id.tvVariationName)
        val productPriceTv: TextView = itemView.findViewById(R.id.tvProductPrice)
        val productImageView: ImageView = itemView.findViewById(R.id.imageVariations)
        val starImageView: ImageView = itemView.findViewById(R.id.plusImageView)
        val addToCartButton: Button = itemView.findViewById(R.id.itemAddToCartButton)
        val minusImageButton: ImageButton = itemView.findViewById(R.id.stepperMinusImageButton)
        val plusImageButton: ImageButton = itemView.findViewById(R.id.stepperPlusImageButton)
        val stepperQuantityTv: TextView = itemView.findViewById(R.id.stepperQuantityTv)
        val stepperContainerView: CardView = itemView.findViewById(R.id.productListStepperView)
        val customizableTv: TextView = itemView.findViewById(R.id.customizableTv)

        /*init {
            addToCartButton.setOnClickListener {
                listener.addButtonTapped(adapterPosition)
            }
            plusImageButton.setOnClickListener {
                listener.plusButtonTapped(adapterPosition)
            }
            minusImageButton.setOnClickListener {
                listener.minusButtonTapped(adapterPosition)
            }
        }*/

    }


}