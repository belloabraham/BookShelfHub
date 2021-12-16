package com.bookshelfhub.bookshelfhub.ui.cart

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
import androidx.recyclerview.widget.RecyclerView
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.bookshelfhub.bookshelfhub.BuildConfig
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.Utils.Location
import com.bookshelfhub.bookshelfhub.adapters.recycler.CartItemsListAdapter
import com.bookshelfhub.bookshelfhub.adapters.recycler.SwipeToDeleteCallBack
import com.bookshelfhub.bookshelfhub.services.remoteconfig.IRemoteConfig
import com.bookshelfhub.bookshelfhub.databinding.FragmentCartBinding
import com.bookshelfhub.bookshelfhub.helpers.AlertDialogBuilder
import com.bookshelfhub.bookshelfhub.helpers.MaterialBottomSheetDialogBuilder
import com.bookshelfhub.bookshelfhub.services.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.services.database.local.ILocalDb
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.Cart
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.PaymentCard
import com.bookshelfhub.bookshelfhub.services.database.local.room.entities.PaymentTransaction
import com.bookshelfhub.bookshelfhub.workers.Constraint
import com.bookshelfhub.bookshelfhub.workers.UploadTransactions
import com.bookshelfhub.bookshelfhub.helpers.Json
import com.bookshelfhub.bookshelfhub.models.Earnings
import com.bookshelfhub.bookshelfhub.services.database.cloud.DbFields
import com.bookshelfhub.bookshelfhub.services.database.cloud.ICloudDb
import com.bookshelfhub.bookshelfhub.services.payment.*
import com.bookshelfhub.bookshelfhub.workers.ClearCart
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.WithFragmentBindings
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject


@AndroidEntryPoint
@WithFragmentBindings
class CartFragment : Fragment() {

    private lateinit var layout: FragmentCartBinding
    private val cartViewModel: CartViewModel by activityViewModels()
    @Inject
    lateinit var localDb: ILocalDb
    @Inject
    lateinit var remoteConfig: IRemoteConfig
    @Inject
    lateinit var json: Json
    @Inject
    lateinit var cloudDb: ICloudDb
    @Inject
    lateinit var userAuth: IUserAuth
    private var savedPaymentCards = emptyList<PaymentCard>()
    private var totalAmountInLocalCurrency:Double=0.0
    private var totalAmountInUSD:Double =0.0
    private var bookIsbnsAndReferrerIds=""
    private lateinit var userId:String
    private var paymentTransaction = emptyList<PaymentTransaction>()
    private var userEarnings =0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

          userId = userAuth.getUserId()
        layout = FragmentCartBinding.inflate(inflater, container, false)

        val cartListAdapter = CartItemsListAdapter(requireContext()).getCartListAdapter{
            removeCartItemMsg()
        }

        layout.cartItemsRecView.adapter = cartListAdapter


        //Get all available cards and save them a list of cards
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


        //Listen for changes in no of items in cart
        cartViewModel.getListOfCartItems().observe(viewLifecycleOwner, Observer { cartList ->

            if (listOfCartItems.isEmpty()){
                listOfCartItems = cartList as ArrayList<Cart>
                cartListAdapter.submitList(listOfCartItems)
            }

            totalAmountInLocalCurrency = 0.0
            totalAmountInUSD = 0.0
            bookIsbnsAndReferrerIds = ""
            var localCurrency=""

            val countryCode = Location.getCountryCode(requireContext())

            if (cartList.isNotEmpty()){

                for (cart in cartList){

                    //If price in USD is null return cart.price else return cart.priceInUsd
                     val priceInUSD = cart.priceInUsd ?: cart.price

                    paymentTransaction.plus(PaymentTransaction(cart.isbn, priceInUSD, cart.title, userId,cart.coverUrl, cart.referrerId, countryCode))

                    totalAmountInUSD.plus(priceInUSD)
                    localCurrency = cart.currency
                    totalAmountInLocalCurrency.plus(cart.price)
                    bookIsbnsAndReferrerIds.plus("${cart.isbn} (${cart.referrerId}), ")
                }

                cloudDb.getListOfDataAsync(DbFields.EARNINGS.KEY, DbFields.REFERRER_ID.KEY, userId, Earnings::class.java, shouldRetry = true){

                    userEarnings = 0.0

                    for(earning in it){
                        userEarnings.plus(earning.earn)
                    }

                    if (totalAmountInUSD == totalAmountInLocalCurrency){
                        //Then local currency must be in USD
                        layout.totalCostTxt.text = String.format(getString(R.string.total_usd),totalAmountInLocalCurrency, userEarnings)
                    }else{
                        layout.totalCostTxt.text = String.format(getString(R.string.total_local_and_usd), localCurrency,totalAmountInLocalCurrency,totalAmountInUSD, userEarnings)
                    }

                }

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
                val cart: Cart = cartListAdapter.currentList[position]

                listOfCartItems.removeAt(position)
                cartListAdapter.notifyItemRemoved(position)

                cartViewModel.deleteFromCart(cart)
                val snackBar = Snackbar.make(layout.rootCoordinateLayout, R.string.item_in_cart_removed_msg, Snackbar.LENGTH_LONG)
                snackBar.setAction(R.string.undo) {
                    listOfCartItems.add(position, cart)
                    cartListAdapter.notifyItemInserted(position)
                    cartViewModel.addToCart(cart)

                }.show()
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(layout.cartItemsRecView)

        return layout.root
    }


    private fun showSavedPaymentCardList(){

        val country = Location.getCountryCode(requireContext())

        //TODO Get country code value that will not change irrespective of it is is null or not as country code is a reference type
      /* country.let { countryCode ->

            //Get nullable recommended payment SDK to be used for a user based on their location
          //TODO  val paymentSDKType = PaymentSDK.get(countryCode)

            //If their is a payment service for user location proceed with Payment
            paymentSDKType?.let { paymentSdk->

                val view = View.inflate(requireContext(), R.layout.saved_cards, null)
                val addCardBtn = view.findViewById<MaterialButton>(R.id.addNewCardBtn)

                if (savedPaymentCards.isNotEmpty()){

                    val recyclerView = view.findViewById<RecyclerView>(R.id.paymentCardsRecView)

                    val userData = hashMapOf(
                        Payment.USER_ID.KEY to userId,
                        Payment.BOOKS_AND_REF.KEY to bookIsbnsAndReferrerIds
                    )

                    val paymentCardsAdapter = PaymentCardsAdapter(requireContext()).getPaymentListAdapter{ paymentCard->

                        if (paymentSdk == SDKType.PAYSTACK){
                            chargeCardWithPayStack(paymentCard, userData)
                        }

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

                MaterialBottomSheetDialogBuilder(requireContext(), viewLifecycleOwner)
                    .setPositiveAction(R.string.dismiss){}
                    .showBottomSheet(view)
            }

            if (paymentSDKType==null){

                AlertDialogBuilder.with(requireActivity(), R.string.location_not_supported)
                    .setCancelable(false)
                    .setPositiveAction(R.string.ok){}
                    .build()
            }
        }*/

    }

    private fun chargeCardWithPayStack(paymentCard:PaymentCard, userData:HashMap<String, String>){

        val payStackProdPublicKey = remoteConfig.getString(Payment.PAY_STACK_PUBLIC_KEY.KEY)

        val amountToChargeInUSD = if (totalAmountInUSD==0.0){
            totalAmountInLocalCurrency
        }else{
            totalAmountInUSD
        } - userEarnings

        //TODO
      /*  val payment:IPayment = PayStack(paymentCard, amountToChargeInUSD, requireActivity(),  json, getPayStackPaymentCallBack())

        val payStackPublicKey = if (BuildConfig.DEBUG){
            getString(R.string.paystack_test_public_key)
        }else{
            payStackProdPublicKey
        }
        payment.chargeCard(payStackPublicKey, Payment.USER_DATA.KEY, userData)*/

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