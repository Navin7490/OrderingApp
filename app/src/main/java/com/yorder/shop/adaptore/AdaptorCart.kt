package com.yorder.shop.adaptore

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.yorder.shop.R
import com.yorder.shop.interfaces.SellerProductItemInterface
import com.yorder.shop.model.CartModel
import com.yorder.shop.model.ModelCart
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class AdaptorCart(
    val context: Context,
    val listCart: ArrayList<ModelCart>,
    var cartModel: CartModel?,
    val listener: SellerProductItemInterface
) : RecyclerView.Adapter<AdaptorCart.ViewHolderCart>() {

    val numberFormatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdaptorCart.ViewHolderCart {

        val layoutInflater: LayoutInflater = LayoutInflater.from(context)
        val view = layoutInflater.inflate(R.layout.item_cart_adapter, parent, false)
        return ViewHolderCart(view, context)
    }

    override fun onBindViewHolder(holder: AdaptorCart.ViewHolderCart, position: Int) {
        holder.bind(listCart[position])
    }

    private fun getProductPrice(context: Context, modelCart: ModelCart): String {
        val amount = numberFormatter.format(modelCart.productPrice.toDouble())
        return context.resources.getString(R.string.amount, amount)
    }

    fun updateCartModel(cart: CartModel?) {
        cartModel = cart
        notifyDataSetChanged()
    }

    fun upDateCart(cart: ArrayList<ModelCart>){
        listCart.clear()
        listCart.addAll(cart)
        notifyDataSetChanged()
    }

    private fun isProductInCart(product: ModelCart): Boolean {
        if (cartModel != null) {
            for (cartProduct in cartModel!!.productList) {
                if (cartProduct.productId == product.productId) {
                    return true
                }
            }
            return false
        } else {
            return false
        }
    }


    override fun getItemCount(): Int {
        return listCart.size
    }

    inner class ViewHolderCart(itemView: View, val context: Context) :
        RecyclerView.ViewHolder(itemView) {

        var tvProductName: TextView = itemView.findViewById(R.id.tvListCartProductName)
        var tvVariationName: TextView = itemView.findViewById(R.id.tvListCartVariationName)
        var tvProductAddOns: TextView = itemView.findViewById(R.id.tvListCartProductAddOns)
        var tvProductQTY: TextView = itemView.findViewById(R.id.tvListCartProductQTY)
        var tvCartProductQTY: TextView = itemView.findViewById(R.id.CartTvQTY)
        var tvSpecialInstruction: TextView = itemView.findViewById(R.id.tvSpecialInstruction)

        var tvProductPrice: TextView = itemView.findViewById(R.id.tvListCartProductPrice)
        var imageProduct: ImageView = itemView.findViewById(R.id.ImageListCart)
        var variationTv: TextView = itemView.findViewById(R.id.variationName)
        var addOnsTv: TextView = itemView.findViewById(R.id.addOnsName)


        var tvRemoveButton: TextView = itemView.findViewById(R.id.tvListCartRemoveButton)
        var tvAddButton: TextView = itemView.findViewById(R.id.tvListCartAddButton)


        init {

        }

        fun bind(modelCart: ModelCart) {
            tvProductName.text = modelCart.productName

            tvProductAddOns.visibility = View.GONE

            // tvProductAddOns.text = "${modelCart.productAddOns},${modelCart.productVariation}"
            val price = modelCart.productPrice
            tvProductQTY.text = modelCart.productQuantity
            tvCartProductQTY.text = " X ${modelCart.productQuantity}"

            tvProductPrice.text = getProductPrice(context, modelCart)


            if (modelCart.productAddOns.isNotEmpty()) {
                tvVariationName.text = "${modelCart.productAddOns}"

            }

            if (modelCart.productVariation.isNotEmpty()) {
                tvVariationName.text = "${modelCart.productVariation}"

            }
            if (modelCart.productVariation.isNotEmpty() && modelCart.productAddOns.isNotEmpty()) {
                tvVariationName.text = "${modelCart.productVariation} • ${modelCart.productAddOns}"

            }


            if (modelCart.productVariation.isEmpty()) {
                variationTv.visibility = View.GONE
            }

            if (modelCart.productAddOns.isEmpty()) {
                addOnsTv.visibility = View.GONE

            }


        }


    }
}


//                tvProductPrice.text = data.productPrice
//                tvProductQTY.text = data.productQuantity
// notifyItemChanged(position)

//                var quantity = 0
//                cartModel!!.productList.forEach {
//                    if (it.productId == data.productId) {
//                        quantity += it.productQuantity
//                        totalPrice = quantity * price.toInt()
//
//
//                    }
//                }
//                tvProductQTY.text = quantity.toString()
//                tvProductPrice.text = "$" + totalPrice