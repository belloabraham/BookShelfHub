package com.bookshelfhub.bookshelfhub.ui.cart

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bookshelfhub.bookshelfhub.CartActivityViewModel
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.helpers.utils.Location
import com.bookshelfhub.bookshelfhub.adapters.recycler.CartItemsListAdapter
import com.bookshelfhub.bookshelfhub.adapters.recycler.SwipeToDeleteCallBack
import com.bookshelfhub.bookshelfhub.databinding.FragmentCartBinding
import com.bookshelfhub.bookshelfhub.helpers.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.data.models.entities.CartItem
import com.bookshelfhub.bookshelfhub.data.models.entities.PaymentTransaction
import com.bookshelfhub.bookshelfhub.helpers.Json
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.WithFragmentBindings
import kotlinx.coroutines.launch
import me.ibrahimyilmaz.kiel.core.RecyclerViewHolder
import javax.inject.Inject

@AndroidEntryPoint
@WithFragmentBindings
class CartFragment : Fragment() {

    private var binding: FragmentCartBinding?=null
    private lateinit var layout: FragmentCartBinding
    private val cartActivityViewModel: CartActivityViewModel by activityViewModels()
    @Inject
    lateinit var json: Json

    private var totalAmountInLocalCurrency:Double=0.0
    private lateinit var userId:String
    private var mCartListAdapter:ListAdapter<CartItem, RecyclerViewHolder<CartItem>>?=null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentCartBinding.inflate(inflater, container, false)
        layout = binding!!

        mCartListAdapter = CartItemsListAdapter(requireContext()).getCartListAdapter{
            showRemoveCartItemMsg()
        }

        val cartListAdapter = mCartListAdapter!!

        layout.cartItemsRecView.adapter = cartListAdapter

        layout.cartItemsRecView.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )
        )

        layout.checkoutBtn.setOnClickListener {
            val savedCardsAction =  CartFragmentDirections.actionCartFragmentToSavedCardsFragment()
            findNavController().navigate(savedCardsAction)
        }


        var listOfCartItems: ArrayList<CartItem> =  ArrayList()

       cartActivityViewModel.getLiveListOfCartItemsAfterEarnings().observe(viewLifecycleOwner, Observer{ cartItems->

           val countryCode = Location.getCountryCode(requireActivity().applicationContext)

           showCartItemsState(cartItems)

           totalAmountInLocalCurrency = 0.0
           cartActivityViewModel.setTotalAmountInUSD(0.0)
           cartActivityViewModel.setCombinedBookIds("")

           if (cartItems.isNotEmpty()){
               listOfCartItems = cartItems as ArrayList<CartItem>
               cartListAdapter.submitList(listOfCartItems)


               lifecycleScope.launch {
                   val userAdditionalInfo = cartActivityViewModel.getUser().additionInfo
                   for(cartItem in cartItems){

                       val priceInUSD = cartItem.priceInUsd ?: cartItem.price

                       cartActivityViewModel.setPaymentTransactions(
                           cartActivityViewModel.getPaymentTransactions().plus(
                           PaymentTransaction(
                               cartItem.bookId,
                               priceInUSD,
                               userId,
                               cartItem.name,
                               cartItem.coverUrl,
                               cartItem.pubId,
                               cartItem.collaboratorsId,
                               countryCode,
                               userAdditionalInfo,
                               cartItem.price )
                       ))

                       cartActivityViewModel.setTotalAmountInUSD(cartActivityViewModel.getTotalAmountInUSD().plus(priceInUSD))
                       totalAmountInLocalCurrency =  totalAmountInLocalCurrency.plus(cartItem.price)
                       cartActivityViewModel.setCombinedBookIds(cartActivityViewModel.getCombinedBookIds().plus("${cartItem.bookId}, "))
                   }
                   showTotalAmountOfBooks(
                       cartActivityViewModel.getTotalAmountInUSD(),
                       localCurrency =  cartItems[0].currency,
                       totalAmountInLocalCurrency
                   )
               }

           }
       })


        val swipeToDeleteCallback  = object : SwipeToDeleteCallBack(requireContext(), R.color.errorColor, R.drawable.ic_cart_minus_white) {

            @SuppressLint("ShowToast")
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, i: Int) {
                val position: Int = viewHolder.bindingAdapterPosition
                val cart: CartItem = cartListAdapter.currentList[position]

                listOfCartItems.removeAt(position)
                cartListAdapter.notifyItemRemoved(position)

                cartActivityViewModel.deleteFromCart(cart)
                 Snackbar.make(layout.rootCoordinateLayout, R.string.item_in_cart_removed_msg, Snackbar.LENGTH_LONG)
                     .setAction(R.string.undo) {
                    listOfCartItems.add(position, cart)
                    cartListAdapter.notifyItemInserted(position)
                    cartActivityViewModel.addToCart(cart)
                }.show()
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(layout.cartItemsRecView)

        return layout.root
    }



    private fun showTotalAmountOfBooks(
        totalAmountInUSD:Double, localCurrency:String,
        totalAmountInLocalCurrency:Double){

        val totalEarningsInUSD = cartActivityViewModel.getTotalEarningsInUSD()
        val totalAmountAfterEarningsInUSD = totalAmountInUSD - totalEarningsInUSD

        val totalUSD = String.format(
            getString(R.string.total_usd),
            totalAmountInUSD,
            totalEarningsInUSD,
            totalAmountAfterEarningsInUSD
        )

        val totalLocalCurrency = String.format(
            getString(R.string.total_local_and_usd),
            localCurrency,
            totalAmountInLocalCurrency,
            totalAmountInUSD,
            totalEarningsInUSD,
            totalAmountAfterEarningsInUSD
        )

        val bookWasPurchasedInUSD = totalAmountInUSD == totalAmountInLocalCurrency
       layout.totalCostTxt.text =  if (bookWasPurchasedInUSD) totalUSD else totalLocalCurrency

    }

    private fun showCartItemsState(cartItems:List<CartItem>){
        if (cartItems.isEmpty()){
            layout.checkoutBtn.isEnabled = false
            layout.emptyCartLayout.visibility = VISIBLE
            layout.cartItemsRecView.visibility = GONE
        }else{
            layout.checkoutBtn.isEnabled = true
            layout.emptyCartLayout.visibility = GONE
           layout.cartItemsRecView.visibility = VISIBLE
        }
    }


    override fun onResume() {
        super.onResume()
        if (cartActivityViewModel.getIsNewCardAdded()){
            cartActivityViewModel.setIsNewCard(false)
        }
    }

    override fun onDestroyView() {
        binding=null
        mCartListAdapter = null
        super.onDestroyView()
    }


    private fun showRemoveCartItemMsg():Boolean{
        Snackbar.make(layout.rootCoordinateLayout, R.string.cart_item_remove_msg, Snackbar.LENGTH_LONG)
            .show()
        return  true
    }

}