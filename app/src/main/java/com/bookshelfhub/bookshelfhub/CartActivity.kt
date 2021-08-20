package com.bookshelfhub.bookshelfhub

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.bookshelfhub.bookshelfhub.adapters.recycler.CartItemsListAdapter
import com.bookshelfhub.bookshelfhub.adapters.recycler.SwipeToDeleteCallBack
import com.bookshelfhub.bookshelfhub.databinding.ActivityCartBinding
import com.bookshelfhub.bookshelfhub.services.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.services.database.local.ILocalDb
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.Cart
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class CartActivity : AppCompatActivity() {

  private lateinit var layout: ActivityCartBinding
  private val cartActivityViewModel: CartActivityViewModel by viewModels()
  @Inject
  lateinit var localDb: ILocalDb
  @Inject
  lateinit var userAuth:IUserAuth

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    layout = ActivityCartBinding.inflate(layoutInflater)
    setContentView(layout.root)
    setSupportActionBar(layout.toolbar)

    val userId = userAuth.getUserId()
    val adapter = CartItemsListAdapter(this).getCartListAdapter{
      removeCartItemMsg()
    }

    layout.cartItemsRecView.adapter = adapter


    layout.makePaymentFab.setOnClickListener {

    }


    layout.cartItemsRecView.addItemDecoration(
      DividerItemDecoration(
        this,
        DividerItemDecoration.VERTICAL
      )
    )

    cartActivityViewModel.getListOfCartItems().observe(this, Observer {  cartList ->
      if (cartList.isNotEmpty()){
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


    var listOfCartItems: ArrayList<Cart> =  ArrayList()

    lifecycleScope.launch(IO) {
      listOfCartItems = localDb.getListOfCartItems(userId) as ArrayList<Cart>
      withContext(Main){
        adapter.submitList(listOfCartItems)
      }
    }

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

    val swipeToDeleteCallback  = object : SwipeToDeleteCallBack(this, R.color.errorColor, R.drawable.ic_cart_minus_white) {

      override fun onSwiped(viewHolder: RecyclerView.ViewHolder, i: Int) {
        val position: Int = viewHolder.bindingAdapterPosition
        val cart: Cart = adapter.currentList[position]

        listOfCartItems.removeAt(position)
        adapter.notifyItemRemoved(position)

        lifecycleScope.launch(IO) {
          localDb.deleteFromCart(cart)
          withContext(Main){
            val snackBar = Snackbar.make(layout.rootCoordinateLayout, R.string.item_in_cart_removed_msg, Snackbar.LENGTH_LONG)
            snackBar.setAction(R.string.undo) {
              listOfCartItems.add(position, cart)
              adapter.notifyItemInserted(position)
              lifecycleScope.launch(IO){
                localDb.addToCart(cart)
              }
            }.show()
          }
        }

      }
    }
    val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
    itemTouchHelper.attachToRecyclerView(layout.cartItemsRecView)

  }

  private fun removeCartItemMsg():Boolean{
    Snackbar.make(layout.rootCoordinateLayout, R.string.cart_item_remove_msg, Snackbar.LENGTH_LONG)
      .show()
    return  true
  }

  override fun onSupportNavigateUp(): Boolean {
    onBackPressed()
    return true
  }

}