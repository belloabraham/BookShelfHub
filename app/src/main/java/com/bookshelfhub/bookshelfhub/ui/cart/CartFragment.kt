package com.bookshelfhub.bookshelfhub.ui.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import co.paystack.android.Paystack
import co.paystack.android.Transaction
import com.bookshelfhub.bookshelfhub.BuildConfig
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.Utils.Location
import com.bookshelfhub.bookshelfhub.adapters.recycler.CartItemsListAdapter
import com.bookshelfhub.bookshelfhub.adapters.recycler.PaymentCardsAdapter
import com.bookshelfhub.bookshelfhub.adapters.recycler.SwipeToDeleteCallBack
import com.bookshelfhub.bookshelfhub.databinding.FragmentCartBinding
import com.bookshelfhub.bookshelfhub.helpers.AlertDialogBuilder
import com.bookshelfhub.bookshelfhub.helpers.MaterialBottomSheetBuilder
import com.bookshelfhub.bookshelfhub.services.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.services.database.local.ILocalDb
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.Cart
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.PaymentCard
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.PaymentTransaction
import com.bookshelfhub.bookshelfhub.services.payment.IPayment
import com.bookshelfhub.bookshelfhub.services.payment.PayStack
import com.bookshelfhub.bookshelfhub.services.payment.Payment
import com.bookshelfhub.bookshelfhub.workers.Constraint
import com.bookshelfhub.bookshelfhub.workers.UploadTransactions
import com.bookshelfhub.bookshelfhub.helpers.Json
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.WithFragmentBindings
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@AndroidEntryPoint
@WithFragmentBindings
class CartFragment : Fragment() {

    private lateinit var layout: FragmentCartBinding
    private val cartViewModel: CartViewModel by navGraphViewModels(R.id.cartActivityNavigation)
    @Inject
    lateinit var localDb: ILocalDb
    @Inject
    lateinit var json: Json
    @Inject
    lateinit var userAuth: IUserAuth
    private var savedPaymentCards = emptyList<PaymentCard>()
    private var totalAmountOfBooks:Int=0
    private var bookIsbnsAndReferrerIds=""
    private val userId = userAuth.getUserId()
    private var paymentTransaction = emptyList<PaymentTransaction>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        layout = FragmentCartBinding.inflate(inflater, container, false)

        val adapter = CartItemsListAdapter(requireContext()).getCartListAdapter{
            removeCartItemMsg()
        }

        layout.cartItemsRecView.adapter = adapter


        cartViewModel.getLivePaymentCards().observe(viewLifecycleOwner, Observer { savedPaymentCards->
            this.savedPaymentCards = savedPaymentCards
        })

        layout.checkoutFab.setOnClickListener {
            showSavedPaymentCardList()
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

            totalAmountOfBooks = 0
            bookIsbnsAndReferrerIds = ""
            val countryCode = Location.getCountryCode(requireContext())

            if (cartList.isNotEmpty()){

                for (cart in cartList){
                    paymentTransaction.plus(PaymentTransaction(cart.isbn, cart.title,userId, cart.referrerId, countryCode, cart.coverUrl))
                    totalAmountOfBooks.plus(cart.price)
                    bookIsbnsAndReferrerIds.plus("${cart.isbn} (${cart.referrerId}), ")
                }


                layout.totalCostTxt.text = String.format(getString(R.string.total),"$totalAmountOfBooks")

                layout.checkoutFab.isEnabled = true
                layout.emptyCartLayout.visibility = GONE
                layout.cartItemsRecView.visibility = VISIBLE
                layout.checkoutFab.extend()
            }else{
                layout.checkoutFab.shrink()
                layout.checkoutFab.isEnabled = false
                layout.emptyCartLayout.visibility = VISIBLE
                layout.cartItemsRecView.visibility = GONE
            }
        })


        //Expand or Shrink FAB based on recyclerview scroll position
        layout.cartItemsRecView.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if(dy>0 && layout.checkoutFab.isExtended){
                    layout.checkoutFab.shrink()
                }else if(dy<0 && !layout.checkoutFab.isExtended){
                    layout.checkoutFab.extend()
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


    private fun showSavedPaymentCardList(){
        val view = View.inflate(requireContext(), R.layout.saved_cards, null)
        val addCardBtn = view.findViewById<MaterialButton>(R.id.addNewCardBtn)

        if (savedPaymentCards.isNotEmpty()){

            val recyclerView = view.findViewById<RecyclerView>(R.id.paymentCardsRecView)

            val userData = hashMapOf(
                Payment.USER_ID.KEY to userId,
                Payment.BOOKS_AND_REF.KEY to bookIsbnsAndReferrerIds
            )

            val paymentCardsAdapter = PaymentCardsAdapter(requireContext()).getPaymentListAdapter{

                val payment:IPayment = PayStack(it, requireActivity(), json, getPayStackPaymentCallBack())
                val payStackPublicKey = if (BuildConfig.DEBUG){
                    getString(R.string.paystack_test_public_key)
                }else{
                    getString(R.string.paystack_public_key)
                }
                payment.chargeCard(payStackPublicKey, totalAmountOfBooks, Payment.USERDATA.KEY, userData)

            }

            recyclerView.addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    DividerItemDecoration.VERTICAL
                )
            )
            recyclerView.adapter = paymentCardsAdapter
            paymentCardsAdapter.submitList(savedPaymentCards)
        }

        addCardBtn.setOnClickListener {
            val actionCardInfo = CartFragmentDirections.actionCartFragmentToCardInfoFragment()
            findNavController().navigate(actionCardInfo)
        }

        MaterialBottomSheetBuilder(requireContext(), viewLifecycleOwner)
            .setPositiveAction(R.string.dismiss){}
            .showBottomSheet(view)
    }

    private fun getPayStackPaymentCallBack(): Paystack.TransactionCallback {

       return object : Paystack.TransactionCallback{

            override fun onSuccess(transaction: Transaction?) {
                transaction?.let {

                    val length = paymentTransaction.size -1

                    for (i in 0..length){
                        paymentTransaction[i].transactionReference = it.reference
                    }

                    lifecycleScope.launch(IO){
                        localDb.addPaymentTransactions(paymentTransaction)
                        withContext(Main){
                            val oneTimeVerifyPaymentTrans =
                                OneTimeWorkRequestBuilder<UploadTransactions>()
                                    .setConstraints(Constraint.getConnected())
                                    .build()

                            WorkManager.getInstance(requireContext()).enqueue(oneTimeVerifyPaymentTrans)
                        }
                    }

                    AlertDialogBuilder.with(requireActivity(), R.string.transaction_processing)
                        .setCancelable(false)
                        .setPositiveAction(R.string.ok){
                            requireActivity().finish()
                        }
                        .build()

                }

            }

            override fun beforeValidate(transaction: Transaction?) {

            }

            override fun onError(error: Throwable?, transaction: Transaction?) {
                error?.let {
                    val errorMsg = String.format(getString(R.string.transaction_error), it.localizedMessage)
                    AlertDialogBuilder.with(requireActivity(), errorMsg)
                        .setCancelable(false)
                        .setPositiveAction(R.string.ok){}
                        .build()
                }
            }

       }
    }


    override fun onResume() {
        super.onResume()
        if (cartViewModel.getIsNewCardAdded()){
            showSavedPaymentCardList()
            cartViewModel.setIsNewCard(false)
        }
    }

    private fun removeCartItemMsg():Boolean{
        Snackbar.make(layout.rootCoordinateLayout, R.string.cart_item_remove_msg, Snackbar.LENGTH_LONG)
            .show()
        return  true
    }

}