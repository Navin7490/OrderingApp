package com.yorder.shop.adaptore

import android.content.Context
import android.text.Html
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.yorder.shop.R
import com.yorder.shop.model.OrderModel
import com.yorder.shop.ui.OrderStatus
import de.hdodenhof.circleimageview.CircleImageView
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AdaptorOrderParent( val listOrderDetail: ArrayList<OrderModel>,val listener:OrderStatus<OrderModel>)
    :RecyclerView.Adapter<AdaptorOrderParent.ViewHolderOrderDetail>() {
   var lastselected=-1
    private val dateFormatter = SimpleDateFormat("dd/MM/yyyy hh:mm aa", Locale("en", "IN"))
    private val numberFormatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdaptorOrderParent.ViewHolderOrderDetail {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val viewList = layoutInflater.inflate(R.layout.list_order, parent, false)
        return ViewHolderOrderDetail(viewList)
    }

    override fun onBindViewHolder(holder: AdaptorOrderParent.ViewHolderOrderDetail, position: Int) {
        if (position == listOrderDetail.size - 1) {
            listener.didScrolledToEnd(position)
        }

        with(holder){
            bind(listOrderDetail[position])

        }
    }

    private fun getCurrencyPrice(context: Context, price: OrderModel): String {
        val amount = numberFormatter.format(price.totalPrice.toDouble())
        return context.resources.getString(R.string.amount, amount)
    }

    fun upDateData(demoOrder: List<OrderModel>) {
        listOrderDetail.addAll(demoOrder)
        notifyDataSetChanged()
        listener.dataSourceDidUpdate(listOrderDetail.size)


    }

    fun updateOrderStatus(orderModel: OrderModel) {
        val index = listOrderDetail.indexOfFirst { it.orderId == orderModel.orderId }

        if (index > -1) {

            orderModel.sellerModel=listOrderDetail[index].sellerModel
            listOrderDetail[index] = orderModel
            notifyItemChanged(index)
            notifyDataSetChanged()
        }
    }




    override fun getItemCount(): Int {
        return listOrderDetail.size
    }

    inner class ViewHolderOrderDetail(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var image: CircleImageView = itemView.findViewById(R.id.Im_OrderShopBanner)
        var tvShopName: TextView = itemView.findViewById(R.id.tv_OrderShopName)
        var tvtime: TextView = itemView.findViewById(R.id.tv_order_deiveryTime)
        var moreareaVisible: ConstraintLayout = itemView.findViewById(R.id.areaMoreItem)
        var recyclerView: RecyclerView = itemView.findViewById(R.id.RvOderItems)
        var tvTotalPrice: TextView = itemView.findViewById(R.id.tvOrderTotal)
        var imExpand: ImageView = itemView.findViewById(R.id.Im_orderMore)
        var seekBar: SeekBar = itemView.findViewById(R.id.seekBar)
        var ktvNotes: TextView = itemView.findViewById(R.id.ktvNotes)

        private var tvNotes: TextView = itemView.findViewById(R.id.tvNotes)


        init {
            itemView.setOnClickListener {
                listOrderDetail[adapterPosition].isexpand = !listOrderDetail[adapterPosition].isexpand
                lastselected = adapterPosition
                notifyDataSetChanged()
            }
        }

        fun bind(listOrderData: OrderModel) {
            tvShopName.text = listOrderData.sellerModel?.shopName
            tvTotalPrice.text = getCurrencyPrice(tvTotalPrice.context, listOrderData)
            Glide.with(image.context).load(listOrderData.sellerModel?.shopBannerImageUrl)
                    .placeholder(R.drawable.ic_placeholder).into(image)

            recyclerView.setHasFixedSize(true)
            recyclerView.isNestedScrollingEnabled = false
            recyclerView.layoutManager = LinearLayoutManager(itemView.context)

            recyclerView.adapter = AdaptorOrderChild(listOrderData.productItemList)

            seekBar.isEnabled = false

            tvtime.text = "Ordered on: ${dateFormatter.format(listOrderData.createdAt.toDate())}"

            when (listOrderData.orderStatus) {
                "New" -> {
                    seekBar.progress = 0
                }
                "On Going" -> {
                    seekBar.progress = 25
                }
                "Ready" -> {
                    seekBar.progress = 50
                }
                "Completed", "Paid" -> {
                    seekBar.progress = 100
                }
                else -> {
                    seekBar.progress = 0
                }
            }

            if (listOrderData.isexpand) {
                moreareaVisible.visibility = View.VISIBLE
                imExpand.setImageResource(R.drawable.ic_vectorexpand)
            } else {
                moreareaVisible.visibility = View.GONE
                imExpand.setImageResource(R.drawable.ic_vector_more)
            }


            if (listOrderData.productInstruction.isNotEmpty()) {
                val noteStr = tvNotes.context.resources.getString(R.string.notes, listOrderData.productInstruction)
                val noteStyled: Spanned = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    Html.fromHtml(noteStr, Html.FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH)
                } else {
                    Html.fromHtml(noteStr)
                }
                tvNotes.text = listOrderData.productInstruction
                tvNotes.visibility = View.VISIBLE
                ktvNotes.visibility = View.VISIBLE

            } else {
                tvNotes.visibility = View.GONE
                ktvNotes.visibility = View.GONE

            }
        }
    }
}