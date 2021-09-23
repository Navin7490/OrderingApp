package com.yorder.shop.ui


import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.yorder.shop.R
import com.yorder.shop.interfaces.CartInterFace
import com.yorder.shop.model.CollectionUser
import com.yorder.shop.model.NetworkUserModel
import com.yorder.shop.model.UserDTO
import com.yorder.shop.model.UserType
import com.yorder.shop.utils.AppDelegate


interface RecyclerViewPagingInterface<T> {
    fun didScrolledToEnd(position: Int)
    fun didSelectItem(dataItem: T, position: Int)
    fun dataSourceDidUpdate(size: Int)
}

class MainActivity : AppCompatActivity(), FirebaseAuth.AuthStateListener,CartInterFace {
    lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var toolbar: MaterialToolbar
    lateinit var navController:NavController
     lateinit var sellerIdFromCart :String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        Firebase.auth.addAuthStateListener(this)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
         navController = navHostFragment.navController

        appBarConfiguration = AppBarConfiguration(setOf(R.id.sellersFragment, R.id.cartFragment, R.id.ordersFragment, R.id.profileFragment))

        bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigationView.setupWithNavController(navController)
        toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.setupWithNavController(navController,appBarConfiguration)
        // bottomNavigationView.itemIconTintList = null

        //bottomNavigationView.setOnNavigationItemSelectedListener(bottomNavItemSelector)
        bottomNavigationView.setOnNavigationItemReselectedListener {}

        navController.addOnDestinationChangedListener(navControllerDestinationChangeListener)


    }

    override fun onAuthStateChanged(p0: FirebaseAuth) {
        if(p0.currentUser == null) {
            AppDelegate.applicationContext().userDTO = null

            if (findNavController(R.id.nav_host_fragment).currentDestination?.id == R.id.splashFragment) {
                Handler(Looper.getMainLooper()).postDelayed({
                    findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_sig_out)
                }, 3000)
            } else {
                // when user logout from profile
                findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_sig_out)
            }
        } else {
            Log.e("NST", "Auth Listener User NOT null")
            Firebase.firestore
                .collection(CollectionUser.name)
                .document(p0.currentUser!!.uid)
                .get()
                .addOnSuccessListener { docSnap ->
                    val networkUser = docSnap.toObject(NetworkUserModel::class.java)
                    if (networkUser != null) {
                        val userDTO = UserDTO.create(networkUser)
                        if (userDTO != null) {
                            if (userDTO.userType == UserType.CUSTOMER) {
                                AppDelegate.applicationContext().userDTO = userDTO

                            } else {
                                Log.e("NST", "This credentials does not belong to Customer")
                            }
                        } else {
                            Log.e("NST", "Please fill user details in console")
                        }
                    } else {
                        Log.e("NST", "Please fill user details in console - 1")
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, exception.message ?: "Unable to get user details", Toast.LENGTH_LONG).show()
                }
        }
    }


    override fun onResume() {
        super.onResume()
    }



    fun setStatusBar(visible: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.show(WindowInsets.Type.statusBars())
             //window.statusBarColor =resources.getColor(R.color.app_bg_white)

        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            //window.statusBarColor =resources.getColor(R.color.app_bg_white)

        }
    }



    val navControllerDestinationChangeListener = NavController.OnDestinationChangedListener { _, destination, _ ->
        Log.e("SST", "${destination.label}")


        when(destination.id) {

            R.id.sellersFragment->{
                toolbar.visibility= View.GONE
                bottomNavigationView.visibility = View.VISIBLE
                setStatusBar(true)
            }
            R.id.ordersFragment,
            R.id.profileFragment -> {
                setStatusBar(true)
                toolbar.visibility= View.VISIBLE

                bottomNavigationView.visibility = View.VISIBLE
            }
            R.id.signInFragment -> {
                  setStatusBar(true)
                toolbar.visibility= View.GONE
                bottomNavigationView.visibility = View.GONE
            }

            R.id.splashFragment ->{
                toolbar.visibility= View.GONE

                bottomNavigationView.visibility = View.GONE

            }
            R.id.cartFragment -> {
                setStatusBar(true)
                toolbar.visibility= View.VISIBLE


            }
            R.id.productsListFragment->{

                toolbar.visibility= View.GONE
                bottomNavigationView.visibility = View.GONE
                setStatusBar(true)
            }
            R.id.sellerInfoFragment->{
                toolbar.visibility= View.VISIBLE
                bottomNavigationView.visibility = View.GONE
                setStatusBar(true)
                toolbar.navigationIcon=resources.getDrawable(R.drawable.ic_arrow_back,resources.newTheme())

            }


            else -> {
                setStatusBar(true)

                toolbar.navigationIcon=resources.getDrawable(R.drawable.ic_arrow_back,resources.newTheme())

                bottomNavigationView.visibility = View.GONE
            }

        }


    }

    override fun onSetUserId(userId: String) {

        sellerIdFromCart =userId
        if (sellerIdFromCart !="null"){
            toolbar.navigationIcon=resources.getDrawable(R.drawable.ic_arrow_back,resources.newTheme())

            toolbar.visibility= View.VISIBLE
            bottomNavigationView.visibility = View.GONE
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.statusBarColor =resources.getColor(R.color.app_bg_white)

        }else{

            toolbar.visibility= View.VISIBLE
            bottomNavigationView.visibility = View.VISIBLE

        }
    }

}