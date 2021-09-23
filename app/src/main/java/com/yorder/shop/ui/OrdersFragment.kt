package com.yorder.shop.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.yorder.shop.R
import com.yorder.shop.adaptore.AdaptorOrderChild
import com.yorder.shop.adaptore.AdaptorOrderParent
import com.yorder.shop.databinding.FragmentOrdersBinding
import com.yorder.shop.model.*
import com.onesignal.OneSignal
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlin.collections.ArrayList

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

interface OrderStatus<T> : RecyclerViewPagingInterface<T> {

}

class OrdersFragment : Fragment(), OrderStatus<OrderModel> {

    private var param1: String? = null
    private var param2: String? = null
    lateinit var viewBinding: FragmentOrdersBinding
    lateinit var listOrder: ArrayList<OrderModel>
    private var adaptorOrderChild: AdaptorOrderChild? = null
    lateinit var adaptorOrderParent: AdaptorOrderParent
    private val deviceState = OneSignal.getDeviceState()
    private var deviceIdNotification: String = ""
    var lastDocument: DocumentSnapshot? = null
    var reachedEnd = false
    var pageSize: Long = 10
     var sellerDTO:UserDTO?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)

        }
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        // view Binding

        viewBinding = FragmentOrdersBinding.inflate(inflater, container, false)
        deviceIdNotification = deviceState?.userId.toString()

        reachedEnd = false
        val layoutManager = LinearLayoutManager(context)
//        layoutManager.reverseLayout = true
//        layoutManager.stackFromEnd = true
        viewBinding.recyclerViewOrderParent.layoutManager = layoutManager
        viewBinding.recyclerViewOrderParent.setHasFixedSize(true)


        listOrder = ArrayList()
        adaptorOrderChild = AdaptorOrderChild(arrayListOf())
        adaptorOrderParent = AdaptorOrderParent(arrayListOf(), this)
        viewBinding.recyclerViewOrderParent.adapter = adaptorOrderParent


         realTimeUpdateOrderStatus()
         getMyOrder()

        viewBinding.btnCartContinueShopping.setOnClickListener {
            findNavController().navigate(R.id.sellersFragment)
        }
        return viewBinding.root
    }


    private fun getMyOrder() {

        viewBinding.progressbar.visibility = View.VISIBLE
        if (!reachedEnd) {

            var query = Firebase.firestore.collection(CollectionOrders.name)
                    .whereEqualTo(CollectionOrders.kCustomerId, Firebase.auth.uid)
                    .orderBy(CollectionOrders.kCreatedAt,Query.Direction.DESCENDING)
            if (lastDocument != null) {
                query = query.startAfter(lastDocument!!)
            }
            query = query.limit(pageSize)

            Tasks.whenAllComplete(query.get())
                    .addOnCompleteListener { response ->

                        if (response.isSuccessful) {

                            val orderList = ArrayList<OrderModel>()
                            val resultTasks = response.result



                            // for (result in resultTask) {

                            var docsCount = 0

                            for (resultTask in resultTasks) {


                                val querySnapshot: QuerySnapshot = resultTask.result as QuerySnapshot

                                docsCount += querySnapshot.documents.size

                                lastDocument = querySnapshot.documents.lastOrNull()

                                for (documentSnapShot in querySnapshot.documents) {
                                    val networkOrderModel = documentSnapShot.toObject(NetworkOrderModel::class.java)

                                    if (networkOrderModel != null) {
                                        val orderModel = OrderModel.create(networkOrderModel)

                                        orderList.add(orderModel)
                                    }
                                }

                            }
                            // }
                            reachedEnd = docsCount < pageSize.toInt()


                            val sellerTaskSnapshotList = orderList.map {
                                Firebase.firestore
                                        .collection(CollectionUser.name)
                                        .whereEqualTo(FieldPath.documentId(), it.sellerId)
                                        .get()
                            }

                            val finalTask = Tasks.whenAllSuccess<QuerySnapshot>(sellerTaskSnapshotList)


                            finalTask.addOnSuccessListener { querySnapshotList ->


                                for (orderDTO in orderList) {
                                    for (sellerQuerySnapShot in querySnapshotList) {
                                        val queryDocSnapShot = sellerQuerySnapShot.firstOrNull()
                                        if (queryDocSnapShot != null) {
                                            val networkUserModel = queryDocSnapShot.toObject(NetworkUserModel::class.java)
                                            if (orderDTO.sellerId == networkUserModel.id) {
                                                  sellerDTO = UserDTO.create(networkUserModel)!!
                                                orderDTO.sellerModel = sellerDTO
                                            }
                                        }
                                    }
                                }
                                adaptorOrderParent.upDateData(orderList)
                                viewBinding.progressbar.visibility = View.GONE


                            }.addOnFailureListener {
                                viewBinding.progressbar.visibility = View.GONE

                            }

                        } else {
                            viewBinding.progressbar.visibility = View.GONE

                        }
                    }


        } else {
            viewBinding.progressbar.visibility = View.GONE

        }

    }


    // start real time order status fun
    private fun realTimeUpdateOrderStatus() {

        Firebase.firestore.collection(CollectionOrders.name)
                .whereEqualTo(CollectionOrders.kCustomerId, Firebase.auth.uid)
                .addSnapshotListener { querySnapShot, error ->
                    if (error != null) {
                        Log.e("error", "$error")
                    } else {

                        val document = querySnapShot?.documents

                        document?.map {
                            val networkOrderModel=  it.toObject(NetworkOrderModel::class.java)
                            if (networkOrderModel != null){

                                val orderModel= OrderModel.create(networkOrderModel)

                                adaptorOrderParent.updateOrderStatus(orderModel)

                            }
                        }
                    }
                }
    }
    // end real time order status fun


    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                OrdersFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }


    override fun didScrolledToEnd(position: Int) {
        Log.e("didi", "$position")
        getMyOrder()

    }

    override fun didSelectItem(dataItem: OrderModel, position: Int) {
    }

    override fun dataSourceDidUpdate(size: Int) {
        if (size > 0) {
            viewBinding.recyclerViewOrderParent.visibility=View.VISIBLE
        } else {
            viewBinding.recyclerViewOrderParent.visibility=View.GONE

            viewBinding.tvOrderIsEmpty.visibility = View.VISIBLE
            viewBinding.btnCartContinueShopping.visibility = View.VISIBLE
            viewBinding.imCartEmpty.visibility = View.VISIBLE
        }
    }


}

