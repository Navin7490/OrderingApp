package com.yorder.shop.adaptore

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.yorder.shop.R
import com.yorder.shop.model.SubscriptionDTO
import com.yorder.shop.model.SubscriptionStatus
import com.yorder.shop.model.UserDTO
import com.yorder.shop.ui.SubscriptionStateChange
import kotlin.collections.ArrayList

class SellerAdapter(val dataSource: ArrayList<UserDTO>, val listener: SubscriptionStateChange<UserDTO>):
    RecyclerView.Adapter<SellerAdapter.ViewHolder>(),Filterable {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.list_sellers, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position == dataSource.size - 1) {
            listener.didScrolledToEnd(position)
        }

        with(holder) {
            Glide.with(sellerImageView.context).load(dataSource[position].shopBannerImageUrl)
                .placeholder(R.drawable.ic_placeholder)
                .into(sellerImageView)

            sellerNameTv.text = dataSource[position].shopName
            sellerInfoTv.text = dataSource[position].shopSmallInfo
            //if (dataSource[position].subStatus == SubscriptionStatus.NONE || dataSource[position].subStatus == SubscriptionStatus.CANCELLED) {
            if (dataSource[position].subStatus == SubscriptionStatus.NONE) {
                subscribeButton.text = "Subscribe"
                subscribeButton.setTextColor(ContextCompat.getColor(subscribeButton.context, R.color.app_text_white))
                subscribeButton.background = ContextCompat.getDrawable(subscribeButton.context,R.drawable.drawable_subscribebutton)
            } else {
                if (dataSource[position].subStatus == SubscriptionStatus.PENDING) {
                    subscribeButton.text = dataSource[position].subStatus.value
                    subscribeButton.setTextColor(ContextCompat.getColor(subscribeButton.context, R.color.app_text_black))
                    subscribeButton.background = ContextCompat.getDrawable(subscribeButton.context,R.drawable.drawable_searchedit)
                } else {
                    subscribeButton.text = dataSource[position].subStatus.value
                    subscribeButton.setTextColor(ContextCompat.getColor(subscribeButton.context, R.color.app_text_black))
                    subscribeButton.setCompoundDrawablesWithIntrinsicBounds(null,null,null,null)
                    subscribeButton.background = ContextCompat.getDrawable(subscribeButton.context,R.drawable.drawable_searchedit)
                }

            }
        }
    }

    override fun getItemCount(): Int {
        return dataSource.size
    }

    fun updateDataSource(newData: List<UserDTO>) {
        dataSource.clear()
        dataSource.addAll(newData)
        notifyDataSetChanged()
        listener.dataSourceDidUpdate(dataSource.size)
    }

    fun injectNextBatch(newData: List<UserDTO>) {
        dataSource.addAll(newData)
        notifyDataSetChanged()
        listener.dataSourceDidUpdate(dataSource.size)
    }

    fun updateSubscription(sellerDTO: UserDTO) {
        val index = dataSource.indexOfFirst { it.id == sellerDTO.id }
        if (index > -1) {
            dataSource[index] = sellerDTO
            notifyItemChanged(index)
        }
     }

    fun updateSubscriptions(subscriptionList: List<SubscriptionDTO>) {
        for (subscriptionDTO in subscriptionList) {
            val index = dataSource.indexOfFirst { it.id == subscriptionDTO.sellerId }
            if (index > -1) {
                dataSource[index].subStatus = subscriptionDTO.subscriptionStatus
                dataSource[index].subId = subscriptionDTO.id
                notifyItemChanged(index)
            }
        }
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val sellerImageView: ImageView = itemView.findViewById(R.id.imageViewSellersProductImageUrl)
        val sellerNameTv: TextView = itemView.findViewById(R.id.textViewSellersName)
        val sellerInfoTv: TextView = itemView.findViewById(R.id.textViewSellersSmallInfo)
        val subscribeButton: TextView = itemView.findViewById(R.id.ButtonSellersSubscribe)

        init {
            itemView.setOnClickListener {
                listener.didSelectItem(dataSource[adapterPosition], adapterPosition)
            }


            subscribeButton.setOnClickListener {
                listener.subscribeTapped(dataSource[adapterPosition])
            }
        }
    }

    override fun getFilter(): Filter {
        return dataFilterd
    }


    private val dataFilterd:Filter=object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {

            val charst=constraint.toString().toLowerCase().trim()
            var filterData:ArrayList<UserDTO> =ArrayList()
            if (charst.isEmpty()){
               filterData = dataSource

            }else{
                var listData:ArrayList<UserDTO> =ArrayList()

                for (item in dataSource){
                    if (item.shopCategory.contains(charst)
                        || item.shopName.toLowerCase().contains(charst)
                        || item.shopSmallInfo.toLowerCase().contains(charst)
                        || item.firstName.toLowerCase().contains(charst)
                        || item.lastName.toLowerCase().contains(charst)
                        || item.area.toLowerCase().contains(charst)
                        || item.city.toLowerCase().contains(charst)
                        || item.address.toLowerCase().contains(charst)
                        || item.societyName.toLowerCase().contains(charst)){
                        listData.add(item)
                    }
                }
                filterData=listData

            }

            val result=FilterResults()
            result.values =filterData
            return result
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            if (results != null) {
                dataSource.clear()
                dataSource.addAll(results.values as ArrayList<UserDTO>)
                notifyDataSetChanged()

            }

        }
    }




}