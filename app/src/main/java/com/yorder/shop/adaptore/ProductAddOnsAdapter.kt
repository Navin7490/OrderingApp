package com.yorder.shop.adaptore

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.yorder.shop.R
import com.yorder.shop.interfaces.ProductCustomisationChange
import com.yorder.shop.model.ProductCustomOption
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class ProductAddOnsAdapter(
        val dataSource: ArrayList<ProductCustomOption>,
        val listener: ProductCustomisationChange): RecyclerView.Adapter<ProductAddOnsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_product_add_ons_adapter, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val productCustomOption = dataSource[position]
        with(holder) {
            itemCheckBox.text = dataSource[position].optionName
            itemCheckBox.isChecked = dataSource[position].isSelected
            priceTv.text ="+${getProductPrice(priceTv.context,productCustomOption)}"
        }
    }

    private fun getProductPrice(context: Context, productCustomOption: ProductCustomOption): String {
        val numberFormatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

        val amount = numberFormatter.format(productCustomOption.price.toDouble())
        return context.resources.getString(R.string.amount, amount)
    }

    override fun getItemCount(): Int {
        return dataSource.size
    }

    fun clearAllSelection() {
        dataSource.forEach { it.isSelected = false }
        notifyDataSetChanged()
        listener.updateTotalPrice()
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val itemCheckBox: CheckBox = itemView.findViewById(R.id.addOnItemCheckBox)
        val priceTv: TextView = itemView.findViewById(R.id.addOnItemPriceTv)

        init {
            itemCheckBox.setOnClickListener {
                dataSource[adapterPosition].isSelected = (it as CheckBox).isChecked
                notifyDataSetChanged()
                listener.updateTotalPrice()
            }
        }
    }

}