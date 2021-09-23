package com.yorder.shop.adaptore

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.yorder.shop.R
import com.yorder.shop.model.ModelDiscover
import com.yorder.shop.utils.AppDelegate

interface SellerCategoryInterface {
    fun didSelect(category: ModelDiscover)
}

class AdapterDiscover(val listDiscover: ArrayList<ModelDiscover>,
                      val listener: SellerCategoryInterface?,
                      var lastSelectedCategory: ModelDiscover) : RecyclerView.Adapter<AdapterDiscover.ViewHolderDiscover>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterDiscover.ViewHolderDiscover {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.list_discover, parent, false)
        return ViewHolderDiscover(view!!)
    }

    override fun onBindViewHolder(holder: AdapterDiscover.ViewHolderDiscover, position: Int) {
        val data = listDiscover[position]
        holder.tvName.text = data.name
       // Glide.with(holder.tvName.context).load(data.image).into(holder.imageDiscover)

        if (data == lastSelectedCategory) {
            holder.rootCardView.background = ContextCompat.getDrawable(
                    holder.rootCardView.context,
                    R.drawable.drawable_subscribebutton
            )

            holder.rootCardView.setCardBackgroundColor(
                    ContextCompat.getColor(
                            holder.itemView.context,
                            R.color.app_bg_blue
                    )
            )
            holder.tvName.setTextColor(
                    ContextCompat.getColor(
                            holder.itemView.context,
                            R.color.app_text_white
                    )
            )
            holder.rootCardView.radius = 50F
            holder.imageDiscover.imageTintList = ContextCompat.getColorStateList(
                    holder.imageDiscover.context,
                    R.color.app_text_white
            )
        } else {

            holder.rootCardView.background = ContextCompat.getDrawable(
                    holder.rootCardView.context,
                    R.drawable.drawable_searchedit
            )
            holder.rootCardView.radius = 50F
            holder.imageDiscover.imageTintList = ContextCompat.getColorStateList(
                    holder.imageDiscover.context,
                    R.color.app_text_black
            )
            holder.tvName.setTextColor(
                    ContextCompat.getColor(
                            holder.itemView.context,
                            R.color.app_text_black
                    )
            )
        }
    }

    override fun getItemCount(): Int {
        return listDiscover.size
    }

    inner class ViewHolderDiscover(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvName: TextView = itemView.findViewById(R.id.radioButtonListDisNameTag)
        var imageDiscover: ImageView = itemView.findViewById(R.id.Im_Discover)
        val rootCardView: CardView = itemView.findViewById(R.id.cvDiscoverItem)
        init {
            rootCardView.setOnClickListener {
                listener?.didSelect(listDiscover[adapterPosition])
                lastSelectedCategory = listDiscover[adapterPosition]
                AppDelegate.applicationContext().selectedCategory = lastSelectedCategory
                notifyDataSetChanged()
            }
        }
    }

}