package com.yorder.shop.adaptore

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.yorder.shop.R
import com.yorder.shop.interfaces.PriceWatcher
import com.yorder.shop.model.*

class AdaptorAddOns(
    val context: Context,
    private val listAddOns: ArrayList<ModelVariationsORAddOns>,
    private val listener: PriceWatcher
) : RecyclerView.Adapter<ViewHolderAddOns>() {

    var currentPrice = 0

    val listCheckedAddOns: ArrayList<ModelVariationsORAddOns> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderAddOns {

        val layoutInflater: LayoutInflater = LayoutInflater.from(context)
        val view = layoutInflater.inflate(R.layout.list_add_ons, parent, false)
        return ViewHolderAddOns(view)
    }

    override fun onBindViewHolder(holder: ViewHolderAddOns, position: Int) {

        val data = listAddOns[position]

        holder.dataBind(data)


        val addOnsName = data.addOnsName
        val price = data.addOnsPrice


        holder.checkBoxName.setOnClickListener {
            val user = FirebaseAuth.getInstance().currentUser
            val customerId = user!!.uid

            notifyDataSetChanged()

            if (holder.checkBoxName.isChecked) {

                val addOnsId = data.addOnsId
                val productId = data.productId
                val sellerId = data.sellerId

                listCheckedAddOns.add(data)
                if (sellerId != null && productId != null && addOnsId != null) {
                    // AddToCart(customerId, sellerId, productId, addOnsId)
                    Log.e("Add", "${data.addOnsId}")
                    getMyCart(customerId, sellerId, productId, addOnsId)


                } else {
                    holder.checkBoxName.isChecked = false
                    Toast.makeText(context, "Sorry item not added in cart", Toast.LENGTH_SHORT)
                        .show()
                }

            } else if (!holder.checkBoxName.isChecked) {
                val addOnsId = data.addOnsId
                val productId = data.productId
                val sellerId = data.sellerId
                listCheckedAddOns.remove(listAddOns[position])
                if (sellerId != null && productId !== null && addOnsId != null) {
                    removeToCart(customerId, sellerId, productId, addOnsId)
                    Log.e("remove", "${data.addOnsId}")
                }


            }


        }
    }

    private fun AddTOCart(
        customerId: String,
        sellersId: String,
        productId: String,
        addOnsId: String
    ) {

        // 1. Get Cart Document
        // 2. Get Product List
        // 3. Update Product List
        // 4. Replace Product List in Document
        // 5. Update Document


        val addOnIdArray = arrayListOf<String>()
        addOnIdArray.add(addOnsId)


        val mapId = hashMapOf<String, Any>(
                CollectionProducts.kProductAddOns to addOnIdArray
        )


        val insertProduct = hashMapOf<Any, Any>(
                CollectionSubscriptions.kCustomerId to customerId,
                CollectionSubscriptions.kSellerId to sellersId
        )

        insertProduct[productId] = mapId
        mapId[CollectionProducts.kProductAddOns] = FieldValue.arrayUnion(addOnsId)


        val db = Firebase.firestore
        val cartRef = db.collection(CollectionCart.name).document(customerId)
        cartRef.set(insertProduct, SetOptions.merge())
            .addOnSuccessListener {
                Log.e("InsertCart", "Inserted")
                Toast.makeText(
                    context,
                    "Add to cart",
                    Toast.LENGTH_LONG
                ).show()

                //up(customerId,addOnsId)
                // getMyCart()

            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    context,
                    "${e.message}",
                    Toast.LENGTH_LONG
                ).show()
                Log.e("InsertCart", "${e.message}")
            }


    }


    private fun removeToCart(
        customerId: String,
        sellersId: String,
        productId: String,
        addOnsId: String
    ) {
        val addOnIdArray = arrayListOf<String>()
        addOnIdArray.add(addOnsId)

        val mapId = hashMapOf<String, Any>(
                CollectionProducts.kProductAddOns to addOnIdArray
        )

        val insertProduct = hashMapOf<Any, Any>(
            CollectionSubscriptions.kCustomerId to customerId,
                CollectionSubscriptions.kSellerId to sellersId
        )

        insertProduct[productId] = mapId
        mapId[CollectionProducts.kProductAddOns] = FieldValue.arrayRemove(addOnsId)
        val db = Firebase.firestore
        val cartRef = db.collection(CollectionCart.name).document(customerId)
        cartRef.set(insertProduct, SetOptions.merge())
            .addOnSuccessListener {
                Log.e("Remove", "Removed")
                Toast.makeText(
                    context,
                    "Remove",
                    Toast.LENGTH_LONG
                ).show()

                //up(customerId,addOnsId)
                // getMyCart()

            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    context,
                    "${e.message}",
                    Toast.LENGTH_LONG
                ).show()
                Log.e("Remove", "${e.message}")
            }


    }

    private fun getMyCart(
        customerId: String,
        sellerId: String,
        productsId: String,
        addOnId: String
    ) {
        Log.e("getCart", "Cart")

        val db = Firebase.firestore
        db.collection(CollectionCart.name)
            .whereEqualTo(FieldPath.documentId(), customerId)
            .get()
            .addOnSuccessListener { document ->
                Log.e("getCart", "s=${document.documents}")


                if (document.documents.isNullOrEmpty()) {
                    AddTOCart(customerId, sellerId, productsId, addOnId)

                } else {
                    for (result in document) {
                        val cId = result.getString(CollectionCart.kCustomerId)
                        val sId = result.getString(CollectionCart.kSellerId)

                        if (sId == sellerId) {

                            AddTOCart(customerId, sellerId, productsId, addOnId)

                            val pId = result.data.keys
                            for (i in 0 until result.data.keys.size) {
                                Log.e("id", "$i")
                                val key = result.data.keys.elementAt(i)
                                Log.e("key", "$key")


                            }
                            Log.e("getId", pId.toString())
                        } else {
                            Toast.makeText(context, "seller is different", Toast.LENGTH_SHORT)
                                .show()


                        }

                        // val key = pId as HashMap<*, *>


                    }
                }


            }
            .addOnFailureListener { e ->

                Log.e("getCart", "${e.message}")

            }

    }




    override fun getItemCount(): Int {
        return listAddOns.size
    }
}

class ViewHolderAddOns(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var tvPrice: TextView = itemView.findViewById(R.id.tvListAddOnsPrice)
    var checkBoxName: CheckBox = itemView.findViewById(R.id.checkboxListAddOnsName)

    @SuppressLint("SetTextI18n")
    fun dataBind(modelAddOns: ModelVariationsORAddOns) {
        checkBoxName.text = modelAddOns.addOnsName
        tvPrice.text = "$" + modelAddOns.addOnsPrice

    }

}


/*
{
"product_list":[
{
 "product_id":"p1",
 "product_variation_list":[
    "v1",
    "v2"
 ],
 "product_add_ons_list":[
    "a1"
 ]
},
{
 "product_id":"p2",
 "product_variation_list":[
    "v12",
    "v22"
 ],
 "product_add_ons_list":[
    "a12"
 ]
}

]
}

*/
