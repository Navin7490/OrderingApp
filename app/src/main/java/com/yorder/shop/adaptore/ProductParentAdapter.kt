package com.yorder.shop.adaptore

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yorder.shop.R
import com.yorder.shop.interfaces.SellerProductItemInterface
import com.yorder.shop.model.CartModel
import com.yorder.shop.model.ProductCategoryModel

class ProductParentAdapter(
        var dataSource: ArrayList<ProductCategoryModel>,
        var cartModel: CartModel?,
        val listener: SellerProductItemInterface):
        RecyclerView.Adapter<ProductParentAdapter.ViewHolderParent>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderParent {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_product_parent_adapter, parent, false)
        return ViewHolderParent(view)
    }

    override fun onBindViewHolder(holder: ViewHolderParent, position: Int) {
        holder.bind(dataSource[position])
    }

    override fun getItemCount(): Int {
        return dataSource.size
    }

    fun updateDataSource(data: ArrayList<ProductCategoryModel>) {
        dataSource.clear()
        dataSource.addAll(data)
        notifyDataSetChanged()
    }

    fun updateCartModel(cart: CartModel?) {
        cartModel = cart
        notifyDataSetChanged()
    }

    inner class ViewHolderParent(val itemView: View): RecyclerView.ViewHolder(itemView) {
        private val categoryNameTv: TextView = itemView.findViewById(R.id.productCategoryTv)
        private val productRV: RecyclerView = itemView.findViewById(R.id.parentProductRV)

        fun bind(productCategoryModel: ProductCategoryModel) {
            categoryNameTv.text = productCategoryModel.categoryName

            productRV.setHasFixedSize(true)
            productRV.isNestedScrollingEnabled = false
            productRV.layoutManager = LinearLayoutManager(itemView.context)

            productRV.adapter = ProductChildAdapter(productCategoryModel.productList, cartModel, listener)
        }
    }

}