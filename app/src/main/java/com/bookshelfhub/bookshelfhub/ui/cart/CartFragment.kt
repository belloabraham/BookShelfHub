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
import co.paystack.android.Paystack
import co.paystack.android.Transaction
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.helpers.utils.Location
import com.bookshelfhub.bookshelfhub.adapters.recycler.CartItemsListAdapter
import com.bookshelfhub.bookshelfhub.adapters.recycler.PaymentCardsAdapter
import com.bookshelfhub.bookshelfhub.adapters.recycler.SwipeToDeleteCallBack
import com.bookshelfhub.bookshelfhub.data.Config
import com.bookshelfhub.bookshelfhub.databinding.FragmentCartBinding
import com.bookshelfhub.bookshelfhub.views.AlertDialogBuilder
import com.bookshelfhub.bookshelfhub.views.MaterialBottomSheetDialogBuilder
import com.bookshelfhub.bookshelfhub.helpers.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.data.models.entities.CartItem
import com.bookshelfhub.bookshelfhub.data.models.entities.PaymentCard
import com.bookshelfhub.bookshelfhub.data.models.entities.PaymentTransaction
import com.bookshelfhub.bookshelfhub.helpers.Json
import com.bookshelfhub.bookshelfhub.helpers.payment.*
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FieldValue
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
    lateinit var json: Json
    @Inject
    lateinit var userAuth: IUserAuth

    private var savedPaymentCards = emptyList<PaymentCard>()
    private var totalAmountInLocalCurrency:Double=0.0
    private var totalAmountInUSD:Double =0.0
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

        layout.cartItemsRecView.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )
        )

        layout.checkoutBtn.setOnClickListener {
            showSavedPaymentCardList()
        }


        var listOfCartItems: ArrayList<CartItem> =  ArrayList()

       cartViewModel.getLiveListOfCartItemsAfterEarnings().observe(viewLifecycleOwner, Observer{ cartItems->

           val countryCode = Location.getCountryCode(requireActivity().applicationContext)

           showCartItemsState(cartItems)

           totalAmountInLocalCurrency = 0.0
           totalAmountInUSD = 0.0
           bookIds = ""

           if (cartItems.isNotEmpty()){
               listOfCartItems = cartItems as ArrayList<CartItem>
               cartListAdapter.submitList(listOfCartItems)


               val userAdditionalInfo = cartViewModel.getUser().additionInfo

               for(cartItem in cartItems){

                   val priceInUSD = cartItem.priceInUsd ?: cartItem.price

                   paymentTransaction.plus(PaymentTransaction(
                       cartItem.bookId,
                       priceInUSD,
                       userId,
                       cartItem.name,
                       cartItem.coverUrl,
                       cartItem.pubId,
                       cartItem.collaboratorsId,
                       countryCode,
                       FieldValue.serverTimestamp(),
                       userAdditionalInfo))

                   totalAmountInUSD.plus(priceInUSD)
                   totalAmountInLocalCurrency.plus(cartItem.price)
                   bookIds.plus("${cartItem.bookId}, ")
               }

               showTotalAmountOfBooks(
                   totalAmountInUSD,
                   localCurrency =  cartItems[0].currency,
                   totalAmountInLocalCurrency
               )
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

    private fun getPaymentSDK(): PaymentSDKType? {
        val countryCode = Location.getCountryCode(requireContext())
        return if(countryCode==null) null else PaymentSDK.getType(countryCode)
    }

    private fun showSavedPaymentCardList(){

        val paymentSDKType = getPaymentSDK()
        val paymentSDKExistForUserCountry = paymentSDKType!=null

        if(paymentSDKExistForUserCountry){

            mView = View.inflate(requireContext(), R.layout.saved_cards, layout.root)
            val addCardBtn = mView!!.findViewById<MaterialButton>(R.id.addNewCardBtn)

            if (savedPaymentCards.isNotEmpty()){

                val recyclerView = mView!!.findViewById<RecyclerView>(R.id.paymentCardsRecView)

                paymentCardsAdapter = PaymentCardsAdapter(requireContext()).getPaymentListAdapter{ paymentCard->
                    //Callback for when user click on any of the payment cards on the list
                    chargeCard(paymentSDKType!!, paymentCard)
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

        val paymentSDKDoesNotExistForUserCountry = !paymentSDKExistForUserCountry
        if (paymentSDKDoesNotExistForUserCountry){
          showPaymentNotSupportedForYourRegionDialog()
        }

    }

    private fun showPaymentNotSupportedForYourRegionDialog(){
        AlertDialogBuilder.with(R.string.location_not_supported, requireActivity())
            .setCancelable(false)
            .setPositiveAction(R.string.ok){}
            .build()
            .showDialog(R.string.unsupported_region)
    }



    private fun showTotalAmountOfBooks(
        totalAmountInUSD:Double, localCurrency:String,
        totalAmountInLocalCurrency:Double){

        val totalEarningsInUSD = cartViewModel.getTotalEarningsInUSD()
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

    private fun showPaymentProcessingMsg(){
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


    private fun chargeCard(paymentPaymentSDKType: PaymentSDKType, paymentCard: PaymentCard){
        val totalAmountToChargeInUSD = totalAmountInUSD - cartViewModel.getTotalEarningsInUSD()

        val userMetaData = hashMapOf(
            Payment.USER_ID.KEY to userId,
            Payment.IDS_OF_BOOKS_BOUGHT.KEY to bookIds
        )

        if(paymentPaymentSDKType == PaymentSDKType.PAYSTACK){
            chargeCardWithPayStack(paymentCard, totalAmountToChargeInUSD, userMetaData)
        }
    }

    private fun chargeCardWithPayStack(
        paymentCard:PaymentCard,
        amountToChargeInUSD:Double,
        metaData: HashMap<String, String>
    ){

        val payStackLivePublicKey = cartViewModel.getPayStackLivePublicKey()

        val payment:IPayment = PayStack(
            Payment.USER_DATA.KEY,
            metaData,
            requireActivity(),
            json,
            getPayStackPaymentCallBack())

        val payStackPublicKey = if (Config.isDevMode()) getString(R.string.paystack_test_public_key) else payStackLivePublicKey

        payment.chargeCard(payStackPublicKey!!, paymentCard, amountToChargeInUSD)
    }

    override fun onResume() {
        super.onResume()
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

    private fun getPayStackPaymentCallBack(): Paystack.TransactionCallback {

        return object : Paystack.TransactionCallback{

            override fun onSuccess(transaction: Transaction?) {

                transaction?.let {
                    val length = paymentTransaction.size -1

                    //Pass transaction reference to all the transaction record that is created
                    for (i in 0..length){
                        paymentTransaction[i].transactionReference = it.reference
                    }

                    cartViewModel.addPaymentTransactions(paymentTransaction)

                    //Show user a message that their transaction is processing and close Cart activity when the click ok
                   showPaymentProcessingMsg()
                }

            }

            override fun beforeValidate(transaction: Transaction?) {}

            override fun onError(error: Throwable?, transaction: Transaction?) {
                error?.let {
                    val errorMsg = String.format(getString(R.string.transaction_error), it.localizedMessage)
                    showTransactionFailedMsg(errorMsg)
                }
            }

        }
    }


    private fun showRemoveCartItemMsg():Boolean{
        Snackbar.make(layout.rootCoordinateLayout, R.string.cart_item_remove_msg, Snackbar.LENGTH_LONG)
            .show()
        return  true
    }

}