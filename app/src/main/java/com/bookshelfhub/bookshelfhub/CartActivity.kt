package com.bookshelfhub.bookshelfhub

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.recyclerview.widget.RecyclerView
import com.bookshelfhub.bookshelfhub.databinding.ActivityCartBinding
import com.bookshelfhub.bookshelfhub.view.toast.Toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CartActivity : AppCompatActivity() {

    private lateinit var layout: ActivityCartBinding
    private val cartActivityViewModel:CartActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layout = ActivityCartBinding.inflate(layoutInflater)
        setContentView(layout.root)
        setSupportActionBar(layout.toolbar)


        layout.makePaymentFab.setOnClickListener {
        }


        cartActivityViewModel.getLiveCartItems().observe(this, Observer { cartItems ->
           if (cartItems.isNotEmpty()){
               layout.makePaymentFab.isEnabled = true
               layout.emptyCartLayout.visibility = View.GONE
               layout.cartItemsRecView.visibility = View.VISIBLE
               layout.makePaymentFab.extend()
           }else{
               layout.makePaymentFab.shrink()
               layout.makePaymentFab.isEnabled = false
               layout.emptyCartLayout.visibility = View.VISIBLE
               layout.cartItemsRecView.visibility = View.GONE

           }
        })


        layout.cartItemsRecView.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if(dy>0 && layout.makePaymentFab.isExtended){
                    layout.makePaymentFab.shrink()
                }else if(dy<0 && !layout.makePaymentFab.isExtended){
                    layout.makePaymentFab.extend()
                }
            }
        })


    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}