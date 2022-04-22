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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.helpers.utils.DeviceUtil
import com.bookshelfhub.bookshelfhub.helpers.utils.Location
import com.bookshelfhub.bookshelfhub.adapters.recycler.CartItemsListAdapter
import com.bookshelfhub.bookshelfhub.adapters.recycler.PaymentCardsAdapter
import com.bookshelfhub.bookshelfhub.adapters.recycler.SwipeToDeleteCallBack
import com.bookshelfhub.bookshelfhub.databinding.FragmentCartBinding
import com.bookshelfhub.bookshelfhub.views.AlertDialogBuilder
import com.bookshelfhub.bookshelfhub.views.MaterialBottomSheetDialogBuilder
import com.bookshelfhub.bookshelfhub.helpers.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.data.models.entities.CartItem
import com.bookshelfhub.bookshelfhub.data.models.entities.PaymentCard
import com.bookshelfhub.bookshelfhub.data.models.entities.PaymentTransaction
import com.bookshelfhub.bookshelfhub.helpers.payment.*
import com.bookshelfhub.bookshelfhub.workers.Worker
import com.flutterwave.raveandroid.rave_java_commons.Meta
import com.flutterwave.raveandroid.rave_presentation.card.CardPaymentCallback
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.WithFragmentBindings
import me.ibrahimyilmaz.kiel.core.RecyclerViewHolder
import javax.inject.Inject

@AndroidEntryPoint
@WithFragmentBindings
class CartFragment : Fragment() {

    private var binding: FragmentCartBinding?=null
    private lateinit var layout: FragmentCartBinding
    private val cartViewModel: CartViewModel by activityViewModels()
    @Inject
    lateinit var worker: Worker
    @Inject
    lateinit var userAuth: IUserAuth

    private var savedPaymentCards = emptyList<PaymentCard>()
    private var totalAmountInLocalCurrency:Double=0.0
    private var totalAmountInUSD:Double =0.0
    private var bookIsbnsAndReferrerIds=""
    private var bookIds =""
    private lateinit var userId:String
    private var paymentTransaction = emptyList<PaymentTransaction>()
    private var mCartListAdapter:ListAdapter<CartItem, RecyclerViewHolder<CartItem>>?=null
    private var paymentCardsAdapter:ListAdapter<PaymentCard, RecyclerViewHolder<PaymentCard>>?=null
    private var mView:View?=null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        userId = userAuth.getUserId()
        binding = FragmentCartBinding.inflate(inflater, container, false)
        layout = binding!!

        mCartListAdapter = CartItemsListAdapter(requireContext()).getCartListAdapter{
            showRemoveCartItemMsg()
        }
        val cartListAdapter = mCartListAdapter!!

        layout.cartItemsRecView.adapter = cartListAdapter


        cartViewModel.getLivePaymentCards().observe(viewLifecycleOwner, Observer { savedPaymentCards->
            this.savedPaymentCards = savedPaymentCards
        })


        //When the User clicks checkout button show saved cards if there be one
        layout.checkoutBtn.setOnClickListener {
            showSavedPaymentCardList()
        }


        layout.cartItemsRecView.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )
        )

        var listOfCartItems: ArrayList<CartItem> =  ArrayList()
       cartViewModel.getLiveListOfCartItemsAfterEarnings().observe(viewLifecycleOwner, Observer{ cartItems->

           val countryCode = Location.getCountryCode(requireActivity().applicationContext)
           showCartItemsState(cartItems)

           totalAmountInLocalCurrency = 0.0
           totalAmountInUSD = 0.0
           bookIsbnsAndReferrerIds = ""
           bookIds = ""

           if (cartItems.isNotEmpty()){
               listOfCartItems = cartItems as ArrayList<CartItem>
               cartListAdapter.submitList(listOfCartItems)

               val anItemInCart = cartItems[0]
               val priceInUSD = anItemInCart.priceInUsd ?: anItemInCart.price

               val userAdditionalInfo = cartViewModel.getUser().additionInfo

               for(cartItem in cartItems){

                   paymentTransaction.plus(PaymentTransaction(cartItem.bookId, priceInUSD, cartItem.title, cartItem.pubId, userId,cartItem.coverUrl,  cartItem.referrerId, countryCode, userAdditionalInfo))

                   totalAmountInUSD.plus(priceInUSD)
                   totalAmountInLocalCurrency.plus(cartItem.price)
                   bookIds.plus("${cartItem.bookId}, ")
                   bookIsbnsAndReferrerIds.plus("${cartItem.bookId} (${cartItem.referrerId}), ")
               }

               showTotalAmountOfBooks(totalAmountInUSD, anItemInCart.currency, totalAmountInLocalCurrency)
           }
       })



        val swipeToDeleteCallback  = object : SwipeToDeleteCallBack(requireContext(), R.color.errorColor, R.drawable.ic_cart_minus_white) {

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, i: Int) {
                val position: Int = viewHolder.bindingAdapterPosition
                val cart: CartItem = cartListAdapter.currentList[position]

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

        val country = Location.getCountryCode(requireActivity().applicationContext)

        //Get country code value that will not change irrespective of it is is null or not as country code is a reference type
        country.let { countryCode ->

            //Get nullable recommended payment SDK to be used for a user based on their location
            val paymentSDKType = PaymentSDK.get(countryCode)

            //If their is a payment service for user location proceed with Payment
            paymentSDKType?.let { paymentSdk->

                 mView = View.inflate(requireContext(), R.layout.saved_cards, layout.root)
                val addCardBtn = mView!!.findViewById<MaterialButton>(R.id.addNewCardBtn)

                if (savedPaymentCards.isNotEmpty()){

                    val recyclerView = mView!!.findViewById<RecyclerView>(R.id.paymentCardsRecView)

                     paymentCardsAdapter = PaymentCardsAdapter(requireContext()).getPaymentListAdapter{ paymentCard->
                        //Callback for when user click on any of the payment cards on the list
                        chargeCard(paymentSdk, paymentCard)

                    }

                    recyclerView.addItemDecoration(
                        DividerItemDecoration(
                            requireContext(),
                            DividerItemDecoration.VERTICAL
                        )
                    )
                    recyclerView.adapter = paymentCardsAdapter!!
                    paymentCardsAdapter!!.submitList(savedPaymentCards)
                }

                addCardBtn.setOnClickListener {
                    val actionCardInfo = CartFragmentDirections.actionCartFragmentToCardInfoFragment()
                    findNavController().navigate(actionCardInfo)
                }

                MaterialBottomSheetDialogBuilder(requireContext(), viewLifecycleOwner)
                    .setPositiveAction(R.string.dismiss){}
                    .showBottomSheet(mView)
            }

            //If their is no payment service available for the user
            if (paymentSDKType==null){

                AlertDialogBuilder.with(R.string.location_not_supported, requireActivity())
                    .setCancelable(false)
                    .setPositiveAction(R.string.ok){}
                    .build()
                    .showDialog(R.string.unsupported_region)
            }
        }

    }

    private fun getFlutterPaymentCallBack(): CardPaymentCallback {
        return object : CardPaymentCallback{
            override fun showProgressIndicator(active: Boolean) {}

            override fun collectCardPin() {}

            override fun collectOtp(message: String?) {}

            override fun collectAddress() {}

            override fun showAuthenticationWebPage(authenticationUrl: String?) {}

            override fun onError(errorMessage: String?, flwRef: String?) {
                showTransactionFailedMsg(errorMessage)
            }

            override fun onSuccessful(flwRef: String?) {
                flwRef?.let {
                    val length = paymentTransaction.size -1

                    //Pass transaction reference to all the transaction record that is created
                    for (i in 0..length){
                        paymentTransaction[i].transactionReference = it
                    }

                    cartViewModel.addPaymentTransactions(paymentTransaction)
                    showPaymentProcessingMsg()
                }
            }

        }
    }


    private fun showTotalAmountOfBooks(
        totalAmountInUSD:Double,
        localCurrency:String,
        totalAmountInLocalCurrency:Double){

        val totalEarnings = cartViewModel.getTotalEarnings()
        layout.totalCostTxt.text =  if (totalAmountInUSD == totalAmountInLocalCurrency) String.format(getString(R.string.total_usd),totalAmountInLocalCurrency, totalEarnings) else String.format(getString(R.string.total_local_and_usd), localCurrency,totalAmountInLocalCurrency,totalAmountInUSD, totalEarnings)

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


    private fun showPaymentProcessingMsg(){
        //Show user a message that their transaction is processing and close Cart activity when the click ok
        AlertDialogBuilder.with(R.string.transaction_processing, requireActivity())
            .setCancelable(false)
            .setPositiveAction(R.string.ok){
                requireActivity().finish()
            }
            .build()
            .showDialog(R.string.processing_transaction)
    }

    private fun showTransactionFailedMsg(sdkErrorMsg:String?){
        sdkErrorMsg?.let {
            val errorMsg = String.format(getString(R.string.transaction_error), it)
            AlertDialogBuilder.with(errorMsg, requireActivity())
                .setCancelable(false)
                .setPositiveAction(R.string.ok){}
                .build()
                .showDialog(R.string.transaction_failed)
        }

    }


    private fun chargeCard(paymentSDKType: SDKType, paymentCard: PaymentCard){
        val amountToChargeInUSD = if (totalAmountInUSD==0.0){
            //User local currency must be in USD
            totalAmountInLocalCurrency
        }else{
            totalAmountInUSD
        } - cartViewModel.getTotalEarnings()

        if (paymentSDKType == SDKType.FLUTTER_WAVE){
            //Meta data containing who bought book and who the referrer for the each book is
            val metaList = listOf(Meta(Payment.USER_ID.KEY, userId), Meta(Payment.BOOKS_AND_REF.KEY, bookIsbnsAndReferrerIds.trim()))
            chargeCardWithFlutter(paymentCard, amountToChargeInUSD, metaList)
        }else{
            //Meta data containing who bought book and  and who the referrer for the book is
            val userData = hashMapOf(
                Payment.USER_ID to userId,
                Payment.BOOKS_AND_REF to bookIsbnsAndReferrerIds.trim()
            )
        }
    }

    private fun chargeCardWithFlutter(paymentCard: PaymentCard, amountToChargeInUSD:Double, metaList:List<Meta>){
        val user =  cartViewModel.getUser()
        val callback = getFlutterPaymentCallBack()

        val firstName = user.firstName
        val lastName = user.lastName

        val payment = FlutterWave(
            cartViewModel.getFlutterEncKey()!!,
            DeviceUtil.getDeviceBrandAndModel(),
            firstName,
            lastName,
            user.email,
            bookIds.trim(),
            metaList,
            callback
        )
        payment.chargeCard(
            cartViewModel.getFlutterPublicKey()!!,
            paymentCard,
            amountToChargeInUSD,
        )
    }

    override fun onResume() {
        super.onResume()
        //Listen for a live view model from CardInfo for a newly added card
        if (cartViewModel.getIsNewCardAdded()){
            showSavedPaymentCardList()
            cartViewModel.setIsNewCard(false)
        }
    }

    override fun onDestroyView() {
        binding=null
        mView=null
        mCartListAdapter = null
        paymentCardsAdapter =null
        super.onDestroyView()
    }

    private fun showRemoveCartItemMsg():Boolean{
        Snackbar.make(layout.rootCoordinateLayout, R.string.cart_item_remove_msg, Snackbar.LENGTH_LONG)
            .show()
        return  true
    }

}