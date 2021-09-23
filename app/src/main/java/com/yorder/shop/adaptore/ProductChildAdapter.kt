package com.yorder.shop.adaptore

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.yorder.shop.R
import com.yorder.shop.interfaces.SellerProductItemInterface
import com.yorder.shop.model.CartModel
import com.yorder.shop.model.ProductModel
import java.text.NumberFormat
import java.util.*

class ProductChildAdapter(
        val dataSource: List<ProductModel>,
        var cartModel: CartModel?,
        val listener: SellerProductItemInterface):
        RecyclerView.Adapter<ProductChildAdapter.ViewHolderChild>() {

    private val numberFormatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderChild {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.list_products, parent, false)
        return ViewHolderChild(view)
    }

    override fun onBindViewHolder(holder: ViewHolderChild, position: Int) {
        holder.bind(dataSource[position])
    }

    override fun getItemCount(): Int {
        return dataSource.size
    }

    private fun getProductPrice(context: Context, productModel: ProductModel): String {
        val amount = numberFormatter.format(productModel.productPrice.toDouble())
        return context.resources.getString(R.string.amount, amount)
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

    inner class ViewHolderChild(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val productNameTv: TextView = itemView.findViewById(R.id.tvProductName)
        private val productVariationNameTv: TextView = itemView.findViewById(R.id.tvVariationName)
        private val productPriceTv: TextView = itemView.findViewById(R.id.tvProductPrice)
        private val productImageView: ImageView = itemView.findViewById(R.id.imageVariations)
        private val starImageView: ImageView = itemView.findViewById(R.id.plusImageView)
        private val addToCartButton: Button = itemView.findViewById(R.id.itemAddToCartButton)
        private val minusImageButton: ImageButton = itemView.findViewById(R.id.stepperMinusImageButton)
        private val plusImageButton: ImageButton = itemView.findViewById(R.id.stepperPlusImageButton)
        private val stepperQuantityTv: TextView = itemView.findViewById(R.id.stepperQuantityTv)
        private val stepperContainerView: CardView = itemView.findViewById(R.id.productListStepperView)
        private val customizableTv: TextView = itemView.findViewById(R.id.customizableTv)

        init {
            addToCartButton.setOnClickListener {
                listener.addButtonTapped(dataSource[adapterPosition])
            }
            plusImageButton.setOnClickListener {
                listener.plusButtonTapped(dataSource[adapterPosition])
            }
            minusImageButton.setOnClickListener {
                listener.minusButtonTapped(dataSource[adapterPosition])
            }
        }

        fun bind(productModel: ProductModel) {
            if (productModel.productVariants.isNotEmpty()){
                productVariationNameTv.visibility = View.VISIBLE
                val sb = StringBuffer()
                for (element in productModel.productVariants){
                    sb.append(element.optionName)
                    sb.append(" • ")
                }

                productVariationNameTv.text = sb.toString()
            } else {
                productVariationNameTv.visibility = View.GONE

            }

            productNameTv.text = productModel.productName
            productPriceTv.text = getProductPrice(productPriceTv.context, productModel)
            Glide.with(itemView.context).load(productModel.productImageUrl)
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

}