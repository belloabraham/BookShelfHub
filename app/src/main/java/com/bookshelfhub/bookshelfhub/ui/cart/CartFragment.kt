package com.bookshelfhub.bookshelfhub.ui.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.adapters.recycler.CartItemsListAdapter
import com.bookshelfhub.bookshelfhub.adapters.recycler.SwipeToDeleteCallBack
import com.bookshelfhub.bookshelfhub.databinding.BookInfoFragmentBinding
import com.bookshelfhub.bookshelfhub.databinding.FragmentCartBinding
import com.bookshelfhub.bookshelfhub.services.database.local.ILocalDb
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.Cart
import com.bookshelfhub.bookshelfhub.ui.main.BookmarkViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.WithFragmentBindings
import javax.inject.Inject


@AndroidEntryPoint
@WithFragmentBindings
class CartFragment : Fragment() {

    private lateinit var layout: FragmentCartBinding
    private val cartViewModel: CartViewModel by viewModels()
    @Inject
    lateinit var localDb: ILocalDb


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        layout = FragmentCartBinding.inflate(inflater, container, false)


        val adapter = CartItemsListAdapter(requireContext()).getCartListAdapter{
            removeCartItemMsg()
        }

        layout.cartItemsRecView.adapter = adapter


        layout.makePaymentFab.setOnClickListener {

        }

        layout.cartItemsRecView.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )
        )

        var listOfCartItems: ArrayList<Cart> =  ArrayList()

        cartViewModel.getListOfCartItems().observe(viewLifecycleOwner, Observer { cartList ->

            if (listOfCartItems.isEmpty()){
                listOfCartItems = cartList as ArrayList<Cart>
                adapter.submitList(listOfCartItems)
            }

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

        val swipeToDeleteCallback  = object : SwipeToDeleteCallBack(requireContext(), R.color.errorColor, R.drawable.ic_cart_minus_white) {

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, i: Int) {
                val position: Int = viewHolder.bindingAdapterPosition
                val cart: Cart = adapter.currentList[position]

                listOfCartItems.removeAt(position)
                adapter.notifyItemRemoved(position)

                cartViewModel.deleteFromCart(cart)
                val snackBar = Snackbar.make(layout.rootCoordinateLayout, R.string.item_in_cart_removed_msg, Snackbar.LENGTH_LONG)
                snackBar.setAction(R.string.undo) {
                    listOfCartItems.add(position, cart)
                    adapter.notifyItemInserted(position)

                    cartViewModel.addToCart(cart)

                }.show()
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(layout.cartItemsRecView)

        return layout.root
    }

    private fun removeCartItemMsg():Boolean{
        Snackbar.make(layout.rootCoordinateLayout, R.string.cart_item_remove_msg, Snackbar.LENGTH_LONG)
            .show()
        return  true
    }

}