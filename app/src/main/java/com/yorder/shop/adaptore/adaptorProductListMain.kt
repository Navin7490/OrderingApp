package com.yorder.shop.adaptore

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.yorder.shop.R
import com.yorder.shop.interfaces.SellerProductItemInterface
import com.yorder.shop.model.CartModel
import com.yorder.shop.model.ProductModel

class AdaptorProductListMain(
    val listMain: ArrayList<String>,
    val listProduct: ArrayList<ProductModel>,
    var cartModel: CartModel?,
    val listener: SellerProductItemInterface,
) : RecyclerView.Adapter<ViewHolderMain>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderMain {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_seller_product_list_main, parent, false)
        return ViewHolderMain(view)
    }

    fun updateDataSourceCategory(data: List<String>) {
        listMain.clear()
        listMain.addAll(data)
        notifyDataSetChanged()
    }



    override fun onBindViewHolder(holder: ViewHolderMain, position: Int) {
        val data = listMain[position]

        with(holder) {

            val sellerProductListAdapter =
                SellerProductListAdapter(tvname.context,
                    listProduct , cartModel, listener)
            rvMain.adapter =sellerProductListAdapter

            tvname.text =data


        }
    }

    fun updateDataSourceProduct(data: List<ProductModel>) {
        listProduct.clear()
        listProduct.addAll(data)

        notifyDataSetChanged()
    }

    fun updateCartModel(cart: CartModel?) {
        cartModel = cart
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return listMain.size
    }
}

class ViewHolderMain(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val tvname: TextView = itemView.findViewById(R.id.tvProductCategoryName)
    val rvMain: RecyclerView = itemView.findViewById(R.id.rvProductListMain)


}
