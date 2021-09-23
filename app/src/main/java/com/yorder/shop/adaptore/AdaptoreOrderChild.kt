package com.yorder.shop.adaptore

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.yorder.shop.R
import com.yorder.shop.model.*
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class AdaptorOrderChild(private val listOrder: ArrayList<ModelOrderItem>)
    : RecyclerView.Adapter<AdaptorOrderChild.ViewHolderOrder>() {

    var viewList: View? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdaptorOrderChild.ViewHolderOrder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        viewList = layoutInflater.inflate(R.layout.item_order_adapter, parent, false)
        return ViewHolderOrder(viewList!!)
    }

    override fun onBindViewHolder(holder: AdaptorOrderChild.ViewHolderOrder, position: Int) {
        holder.bind(listOrder[position])
    }

    private fun getCurrencyPrice(context: Context, price: ModelOrderItem): String {
        val numberFormatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
        val amount = numberFormatter.format(price.price.toDouble())
        return context.resources.getString(R.string.amount, amount)
    }

    override fun getItemCount(): Int {
        return listOrder.size
    }

    inner class ViewHolderOrder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvName: TextView = itemView.findViewById(R.id.tvOrderItemName)
        var tvQTY: TextView = itemView.findViewById(R.id.tvQTY)
        var tvVA: TextView = itemView.findViewById(R.id.tvOrderItemVA)
        var tvPrice: TextView = itemView.findViewById(R.id.tvOrderItemPrice)

        fun bind(listOrderData: ModelOrderItem) {
            // demoOrder = listOrderData
            val sbName = StringBuffer()

            tvName.text = listOrderData.name
            tvQTY.text ="X ${listOrderData.qty}"
            tvPrice.text = getCurrencyPrice(tvPrice.context,listOrderData)


            if (listOrderData.itemVariation.isEmpty()){
                sbName.append(listOrderData.itemAddons)
            }

            if (listOrderData.itemAddons.isEmpty()){
                sbName.append(listOrderData.itemVariation)

            }
            if (listOrderData.itemVariation.isNotEmpty() && listOrderData.itemAddons.isNotEmpty()){
                sbName.append("${listOrderData.itemVariation}• ${listOrderData.itemAddons}")

            }

            if (sbName.isNotEmpty()){
                tvVA.visibility=View.VISIBLE

            }

            tvVA.text =sbName



        }
    }

}