package com.bookshelfhub.book.purchase.ui

import android.os.Bundle
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ListAdapter
import com.bookshelfhub.book.purchase.R
import com.bookshelfhub.book.purchase.adapters.CartItemsListAdapter
import com.bookshelfhub.book.purchase.databinding.FragmentCartBinding
import com.bookshelfhub.core.common.helpers.dialog.AlertDialogBuilder
import com.bookshelfhub.core.model.entities.CartItem
import com.bookshelfhub.core.model.entities.PaymentTransaction
import com.bookshelfhub.core.model.entities.User
import com.bookshelfhub.payment.SupportedCurrencies
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.WithFragmentBindings
import kotlinx.coroutines.launch
import me.ibrahimyilmaz.kiel.core.RecyclerViewHolder

@AndroidEntryPoint
@WithFragmentBindings
class CartFragment : Fragment() {

    private var binding: FragmentCartBinding?=null
    private lateinit var layout: FragmentCartBinding
    private val cartFragmentViewModel by hiltNavGraphViewModels<CartFragmentsViewModel>(R.id.cartActivityNavigation)
    private var mCartListAdapter:ListAdapter<CartItem, RecyclerViewHolder<CartItem>>?=null
    private var totalNoOfCartItems = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentCartBinding.inflate(inflater, container, false)
        layout = binding!!

        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.cart_activity_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {

                if(menuItem.itemId == R.id.clearCart){
                    AlertDialogBuilder.with(R.string.clear_cart_msg, requireActivity())
                        .setCancelable(true)
                        .setNegativeAction(R.string.cancel){}
                        .setPositiveAction(R.string.clear){
                            cartFragmentViewModel.deleteAllCartItems()
                        }
                        .build().showDialog(R.string.clear_cart_title)
                }

                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        val arrayListOfCartItems  =  arrayListOf<CartItem>()

        mCartListAdapter = CartItemsListAdapter(requireContext()).getCartListAdapter{cartItem, position ->
            removeFromCart(arrayListOfCartItems, cartItem, position)
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

        cartFragmentViewModel.getListOfCartItemsAfterUserEarningsFromReferrals().observe(viewLifecycleOwner) { cartItems ->

            showCartItemsState(cartItems)

            totalNoOfCartItems = cartItems.size
            cartFragmentViewModel.totalCostOfBook = 0.0
            cartFragmentViewModel.getPaymentTransactions().clear()

            cartListAdapter.submitList(cartItems)
            layout.totalCostTxt.text = ""

            if (cartItems.isNotEmpty()) {

                arrayListOfCartItems.addAll(cartItems)
                cartListAdapter.submitList(arrayListOfCartItems)

                lifecycleScope.launch {

                    val user = cartFragmentViewModel.getUser()

                    val bookSoldInNGN = cartItems.filter {
                        it.sellerCurrency == SupportedCurrencies.NGN
                    }

                    val bookSoldInUSD = cartItems.filter {
                        it.sellerCurrency == SupportedCurrencies.USD
                    }

                    val totalCostOfBooksInUSD =  addBooksToPaymentTransaction(bookSoldInUSD, user)
                    val totalCostOfBooksInNGN =  addBooksToPaymentTransaction(bookSoldInNGN, user)
                    val atLeastABookIsSoldInUSD = bookSoldInUSD.isNotEmpty()

                    var currencyToChargeForBookSale = SupportedCurrencies.NGN

                    if(atLeastABookIsSoldInUSD){
                        currencyToChargeForBookSale = SupportedCurrencies.USD
                        cartFragmentViewModel.totalCostOfBook += totalCostOfBooksInUSD
                        //TODO Covert the total amount of books sold in NGN to USD and then add it to cartFragmentViewModel.totalCostOfBook if books are sold in USD in the future
                    }

                    val noBooksWasSoldInUSD = !atLeastABookIsSoldInUSD
                    if(noBooksWasSoldInUSD){
                        cartFragmentViewModel.totalCostOfBook += totalCostOfBooksInNGN
                    }

                    showTotalAmountOfBooks(
                        currencyToChargeForBookSale,
                        cartFragmentViewModel.totalCostOfBook,
                        user
                    )
                }

            }
        }

        return layout.root
    }

    private fun removeFromCart(arrayListOfCartItems:ArrayList<CartItem>, cartItem:CartItem, position:Int){
        arrayListOfCartItems.removeAt(position)
        mCartListAdapter!!.notifyItemRemoved(position)
        cartFragmentViewModel.deleteFromCart(cartItem) //This triggers the live cart block above
        Snackbar.make(layout.rootCoordinateLayout, R.string.item_in_cart_removed_msg, Snackbar.LENGTH_LONG)
            .setAction(R.string.undo) {
                arrayListOfCartItems.add(position, cartItem)
                mCartListAdapter!!.notifyItemInserted(position)
                cartFragmentViewModel.addToCart(cartItem) //This triggers the live cart block above
            }.show()
    }

    private fun addBooksToPaymentTransaction(cartItems:List<CartItem>, user:User): Double {
       var totalBookPrice = 0.0
        val combinedBookIds = StringBuilder()
        for (cartItem in cartItems) {

            cartFragmentViewModel.getPaymentTransactions().add(
                PaymentTransaction(
                    cartItem.bookId,
                    cartFragmentViewModel.getUserId(),
                    cartItem.name,
                    cartItem.coverDataUrl,
                    cartItem.pubId,
                    cartItem.collaboratorsId,
                    user.countryCode,
                    cartItem.userAdditionalInfo,
                    cartItem.price,
                    user.earningsCurrency,
                    cartItem.sellerCurrency
                )
            )

            totalBookPrice += cartItem.price
            combinedBookIds.append("${cartItem.bookId}, ")
        }

        cartFragmentViewModel.combinedBookIds = combinedBookIds.toString()
        return totalBookPrice
    }

    private fun showTotalAmountOfBooks(
        currencyToChargeForBookSale:String,
        totalAmountOfBooks:Double,
        user: User
    ){

        cartFragmentViewModel.currencyToChargeForBooksSale = currencyToChargeForBookSale
        val totalUserEarnings = cartFragmentViewModel.getTotalUserEarnings()


        val noNegativeAmountAfterEarnings = totalAmountOfBooks > totalUserEarnings
        val qualifiedToUseEarnings = user.earningsCurrency == currencyToChargeForBookSale && noNegativeAmountAfterEarnings

        var totalAmountAfterUserEarnings = totalAmountOfBooks

        if(qualifiedToUseEarnings){

            val payStackMinimumAmountToCharge = 50
            val bookPriceAfterEarnings = totalAmountOfBooks - totalUserEarnings

            val userQualifiedToUseNairaEarnings = currencyToChargeForBookSale == SupportedCurrencies.NGN && bookPriceAfterEarnings > payStackMinimumAmountToCharge
            val userQualifiedToUseUSDEarnings = currencyToChargeForBookSale == SupportedCurrencies.USD

            if(userQualifiedToUseNairaEarnings || userQualifiedToUseUSDEarnings){
                totalAmountAfterUserEarnings = bookPriceAfterEarnings
                cartFragmentViewModel.subtractedUserEarnings = totalUserEarnings
            }
        }

        layout.totalCostTxt.text = String.format(
            getString(R.string.total_local_currency),
            currencyToChargeForBookSale,
            totalAmountAfterUserEarnings,
            totalAmountOfBooks,
            totalUserEarnings
        )

        cartFragmentViewModel.totalCostOfBook = totalAmountAfterUserEarnings

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

    override fun onDestroyView() {
        binding=null
        mCartListAdapter = null
        super.onDestroyView()
    }

}