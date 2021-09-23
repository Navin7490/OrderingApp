package com.yorder.shop.adaptore

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.yorder.shop.R
import com.yorder.shop.model.ModelSellers
import com.onesignal.OneSignal
import org.json.JSONException
import org.json.JSONObject

const val keySubscribeStatus = "sub_status"

class AdaptorSellers(private val listSellers: ArrayList<ModelSellers>) :
    RecyclerView.Adapter<AdaptorSellers.ViewHolderSellers>(), Filterable {

    var listFilter =  ArrayList(listSellers);

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderSellers {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val itemView = layoutInflater.inflate(R.layout.list_sellers, parent, false)
        return ViewHolderSellers(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolderSellers, position: Int) {
        val listData = listSellers[position]

        holder.dataBind(listData)

        holder.itemView.setOnClickListener { view ->
            val navController = Navigation.findNavController(view)
            val bundle = Bundle()
            bundle.putString("sellerId", listData.sellerId)
            bundle.putString("shopName", listData.shopName)
            bundle.putString("shopDescription", listData.shopDescription)
            bundle.putString("shopBannerImage", listData.shopBannerImage)

            val navOptions =
                NavOptions.Builder().setPopUpTo(R.id.productsListFragment, true).build()
            /*navController.navigate(
                R.id.action_customerHomeFragment_to_productsListFragment,
                bundle, navOptions
            )*/

        }


        holder.tvSubscribe.setOnClickListener {
            Log.e("deviceId", listData.notificationToken)
            sendNotification(it.context, listData.notificationToken, listData.shopBannerImage, listData.shopBannerImage)
        }
    }



    private fun sendNotification(context: Context, deviceId: String, smallIcon: String, largeIcon: String) {
        try {
            val jsonObject = JSONObject(
                "{'include_player_ids':['$deviceId']," +
                        "'headings':{'en':'QuickBit'}," +
                        "'contents':{'en':'Request for subscribe'}," +
                        "'small_icon':'$smallIcon'," +
                        "'large_icon':'$largeIcon'," +
                        "'big_picture':'$largeIcon'," +
                        "'android_group':'123'," +
                        "'android_led_color':'FFE9444E'," +
                        "'android_accent_color':'FFE9444E'}"
            )

            OneSignal.postNotification(jsonObject,
                object : OneSignal.PostNotificationResponseHandler {
                    override fun onSuccess(response: JSONObject?) {
                        Log.e("onSuccess", response.toString())
                        Toast.makeText(context, "send", Toast.LENGTH_SHORT).show()
                    }
                    override fun onFailure(response: JSONObject?) {
                        Log.e("onFailure", response.toString())
                    }
                })
        } catch (e: JSONException) {
            Log.e("JSONException", "${e.message}")
        }

    }

    override fun getItemCount(): Int {
        return listSellers.size
    }

    override fun getFilter(): Filter {
        return FilterData
    }

    inner class ViewHolderSellers(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageViewSellersProductImageUrl)
        val tvName: TextView = itemView.findViewById(R.id.textViewSellersName)
        val tvSmallInfo: TextView = itemView.findViewById(R.id.textViewSellersSmallInfo)
        val tvSubscribe: Button = itemView.findViewById(R.id.ButtonSellersSubscribe)

        var sellerId: String? = null

        fun dataBind(modelSellers: ModelSellers) {
            sellerId = modelSellers.sellerId
            tvName.text = modelSellers.shopName
            tvSmallInfo.text = modelSellers.shopSmallInfo
            Glide.with(imageView.context).load(modelSellers.shopBannerImage)
                .placeholder(R.drawable.ic_placeholder)
                .into(imageView)
        }

    }

    private val FilterData: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults? {
            val filteredList: MutableList<ModelSellers> = ArrayList()
            if (constraint == null || constraint.length == 0) {
                filteredList.addAll(listFilter)
            } else {
                val filterPattern = constraint.toString().toLowerCase().trim { it <=  ' ' }
                for (item in listFilter) {

                    if (item.shopName.toLowerCase().contains(filterPattern)){
                        filteredList.add(item)

                    }
                }
            }
            val results = FilterResults()
            results.values = filteredList
            return results
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults) {
            listSellers.clear()
            listSellers.addAll(results.values as ArrayList<ModelSellers>)
            notifyDataSetChanged()
        }
    }

}


