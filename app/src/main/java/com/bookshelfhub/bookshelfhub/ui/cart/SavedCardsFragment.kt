package com.bookshelfhub.bookshelfhub.ui.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ListAdapter
import co.paystack.android.Paystack
import co.paystack.android.Transaction
import com.bookshelfhub.bookshelfhub.CartActivityViewModel
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.adapters.recycler.PaymentCardsAdapter
import com.bookshelfhub.bookshelfhub.data.Config
import com.bookshelfhub.bookshelfhub.data.models.entities.PaymentCard
import com.bookshelfhub.bookshelfhub.databinding.FragmentSavedCardsBinding
import com.bookshelfhub.bookshelfhub.helpers.Json
import com.bookshelfhub.bookshelfhub.helpers.payment.*
import com.bookshelfhub.bookshelfhub.helpers.utils.Location
import com.bookshelfhub.bookshelfhub.views.AlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.WithFragmentBindings
import me.ibrahimyilmaz.kiel.core.RecyclerViewHolder
import javax.inject.Inject

@AndroidEntryPoint
@WithFragmentBindings
class SavedCardsFragment : Fragment(){

    private var binding:FragmentSavedCardsBinding? = null
    private lateinit var layout: FragmentSavedCardsBinding
    private var paymentCardsAdapter: ListAdapter<PaymentCard, RecyclerViewHolder<PaymentCard>>?=null
    private val savedCardsViewModel:SavedCardsViewModel by viewModels()
    private val cartActivityViewModel: CartActivityViewModel by activityViewModels()
    @Inject
    lateinit var json: Json

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

                binding = FragmentSavedCardsBinding.inflate(inflater, container, false)
                layout = binding!!

                val paymentSDKType = getPaymentSDKByCountryCode()

                paymentCardsAdapter = PaymentCardsAdapter(requireContext()).getPaymentListAdapter { paymentCard ->
                    //Callback for when user click on any of the payment cards on the list
                    val paymentNotSupportedForUserCountry = paymentSDKType == null
                    if (paymentNotSupportedForUserCountry) {
                        showPaymentNotSupportedForYourCountryDialog()
                    } else {
                        chargeCard(paymentSDKType!!, paymentCard)
                    }
                }

                layout.paymentCardsRecView.addItemDecoration(
                    DividerItemDecoration(
                        requireContext(),
                        DividerItemDecoration.VERTICAL
                    )
                )
                layout.paymentCardsRecView.adapter = paymentCardsAdapter!!


                layout.addNewCardBtn.setOnClickListener {
                    val actionCardInfo =
                        SavedCardsFragmentDirections.actionSavedCardsFragmentToCardInfoFragment()
                    findNavController().navigate(actionCardInfo)
                }

                savedCardsViewModel.getLivePaymentCards()
                    .observe(requireActivity(), Observer { savedPaymentCards ->
                        if (savedPaymentCards.isEmpty()) {
                            layout.noSavedCardsLayout.visibility = VISIBLE
                            layout.paymentCardsRecView.visibility = GONE
                        } else {
                            layout.noSavedCardsLayout.visibility = GONE
                            layout.paymentCardsRecView.visibility = VISIBLE
                            paymentCardsAdapter!!.submitList(savedPaymentCards)
                        }
                    })

                return layout.root
            }




    private fun getPaymentSDKByCountryCode(): PaymentSDKType? {
            val countryCode = Location.getCountryCode(requireContext())
            return if(countryCode==null) null else PaymentSDK.getType(countryCode)
        }

    private fun chargeCard(paymentPaymentSDKType: PaymentSDKType, paymentCard: PaymentCard){
        val totalAmountToChargeInUSD = cartActivityViewModel.getTotalAmountInUSD() - cartActivityViewModel.getTotalEarningsInUSD()

        val userMetaData = hashMapOf(
            Payment.USER_ID.KEY to cartActivityViewModel.getUserId(),
            Payment.IDS_OF_BOOKS_BOUGHT.KEY to cartActivityViewModel.getCombinedBookIds()
        )

        if(paymentPaymentSDKType == PaymentSDKType.PAYSTACK){
            chargeCardWithPayStack(paymentCard, totalAmountToChargeInUSD, userMetaData)
        }
    }

    private fun chargeCardWithPayStack(
        paymentCard: PaymentCard,
        amountToChargeInUSD:Double,
        metaData: HashMap<String, String>
    ){

        val payStackLivePublicKey = cartActivityViewModel.getPayStackLivePublicKey()

        val payment: IPayment = PayStack(
            Payment.USER_DATA.KEY,
            metaData,
            requireActivity(),
            json,
            getPayStackPaymentCallBack())

        val payStackPublicKey = if (Config.isDevMode()) getString(R.string.paystack_test_public_key) else payStackLivePublicKey

        payment.chargeCard(
            payStackPublicKey!!,
            paymentCard,
            amountToChargeInUSD,
            cartActivityViewModel.getUserEmail()!!
        )
    }

    private fun getPayStackPaymentCallBack(): Paystack.TransactionCallback {

        return object : Paystack.TransactionCallback{

            override fun onSuccess(transaction: Transaction?) {

                val paymentTransaction = cartActivityViewModel.getPaymentTransactions()
                transaction?.let {
                    val length = paymentTransaction.size -1

                    //Pass transaction reference to all the transaction record that is created
                    for (i in 0..length){
                        paymentTransaction[i].transactionReference = it.reference
                    }

                    cartActivityViewModel.addPaymentTransactions(paymentTransaction)

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

    private fun showPaymentNotSupportedForYourCountryDialog(){
        AlertDialogBuilder.with(R.string.location_not_supported, requireActivity())
            .setCancelable(false)
            .setPositiveAction(R.string.ok){}
            .build()
            .showDialog(R.string.unsupported_region)
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

}