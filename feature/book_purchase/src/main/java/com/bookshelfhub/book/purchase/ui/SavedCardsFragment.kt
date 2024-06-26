package com.bookshelfhub.book.purchase.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ListAdapter
import androidx.work.WorkInfo
import co.paystack.android.Paystack
import co.paystack.android.Transaction
import com.bookshelfhub.book.purchase.R
import com.bookshelfhub.book.purchase.adapters.PaymentCardsAdapter
import com.bookshelfhub.book.purchase.databinding.FragmentSavedCardsBinding
import com.bookshelfhub.core.common.helpers.Json
import com.bookshelfhub.core.common.helpers.dialog.AlertDialogBuilder
import com.bookshelfhub.core.common.worker.Worker
import com.bookshelfhub.core.model.entities.PaymentCard
import com.bookshelfhub.payment.*
import com.bookshelfhub.payment.pay_stack.PayStack
import com.jakewharton.processphoenix.ProcessPhoenix
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.WithFragmentBindings
import kotlinx.coroutines.launch
import me.ibrahimyilmaz.kiel.core.RecyclerViewHolder
import javax.inject.Inject

@AndroidEntryPoint
@WithFragmentBindings
class SavedCardsFragment : Fragment(){

    private var binding: FragmentSavedCardsBinding? = null
    private lateinit var layout: FragmentSavedCardsBinding
    private var paymentCardsAdapter: ListAdapter<PaymentCard, RecyclerViewHolder<PaymentCard>>?=null
    private val savedCardsViewModel: SavedCardsViewModel by viewModels()
    private val cartFragmentViewModel by hiltNavGraphViewModels<CartFragmentsViewModel>(R.id.cartActivityNavigation)

    @Inject
    lateinit var json: Json
    @Inject
    lateinit var worker: Worker


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

                binding = FragmentSavedCardsBinding.inflate(inflater, container, false)
                layout = binding!!

                viewLifecycleOwner.lifecycleScope.launch {
                    
                    paymentCardsAdapter = PaymentCardsAdapter(requireContext()).getPaymentListAdapter { paymentCard ->
                        val paymentSDKType = savedCardsViewModel.getPaymentSDK(paymentCard.countryCode)

                        if(paymentSDKType == null){
                            showPaymentNotSupportedForCardCurrencyDialog()
                        }else{
                            cartFragmentViewModel.paymentSDKType = paymentSDKType
                            chargeCard(paymentSDKType, paymentCard)
                        }
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
                    val actionCardInfo = SavedCardsFragmentDirections.actionSavedCardsFragmentToCardInfoFragment()
                    findNavController().navigate(actionCardInfo)
                }

                savedCardsViewModel.getLivePaymentCards().observe(requireActivity()) { savedPaymentCards ->
                    if (savedPaymentCards.isEmpty()) {
                        layout.noSavedCardsLayout.visibility = VISIBLE
                        layout.paymentCardsRecView.visibility = GONE
                    } else {
                        layout.noSavedCardsLayout.visibility = GONE
                        layout.paymentCardsRecView.visibility = VISIBLE
                        paymentCardsAdapter!!.submitList(savedPaymentCards)
                    }
                }

                layout.myBooksBtn.setOnClickListener {
                    ProcessPhoenix.triggerRebirth(requireContext().applicationContext)
                }


        return layout.root
    }
    

    private fun chargeCard(paymentPaymentSDKType: PaymentSDKType, paymentCard: PaymentCard){
        val totalAmountToCharge = cartFragmentViewModel.totalCostOfBook

        val paystackUserMetaData = hashMapOf(
            Payment.USER_ID.KEY to cartFragmentViewModel.getUserId(),
            Payment.IDS_OF_BOOKS_BOUGHT.KEY to cartFragmentViewModel.combinedBookIds
        )

        if(paymentPaymentSDKType == PaymentSDKType.PAYSTACK){
            chargeCardWithPayStack(paymentCard, totalAmountToCharge, paystackUserMetaData)
        }
    }

    private fun chargeCardWithPayStack(
        paymentCard: PaymentCard,
        totalAmountToCharge:Double,
        metaData: HashMap<String, String>
    ){

        val payStackLivePublicKey = cartFragmentViewModel.getPayStackLivePublicKey()

        val payment: IPayment = PayStack(
            Payment.USER_DATA.KEY,
            metaData,
            requireActivity(),
            json,
            getPayStackPaymentCallBack())

        val payStackPublicKey = if (Config.isDevMode()) getString(R.string.paystack_test_public_key) else payStackLivePublicKey

        viewLifecycleOwner.lifecycleScope.launch {
            payment.chargeCard(
                payStackPublicKey!!,
                paymentCard,
                totalAmountToCharge,
                cartFragmentViewModel.getUserEmail(),
                cartFragmentViewModel.currencyToChargeForBooksSale
            )
        }

    }


    private fun getPayStackPaymentCallBack(): Paystack.TransactionCallback {

        return object : Paystack.TransactionCallback{

            override fun onSuccess(transaction: Transaction?) {

                val paymentTransaction = cartFragmentViewModel.getPaymentTransactions()
                transaction?.let {

                    val length = paymentTransaction.size

                    //Pass transaction reference to all the transaction record that is created
                    for (i in 0 until length){
                        paymentTransaction[i].transactionReference = it.reference
                    }

                    viewLifecycleOwner.lifecycleScope.launch {
                      val paymentTransOnetimeWorkReq =  cartFragmentViewModel.initializePaymentVerificationProcess(paymentTransaction)

                        val workId = paymentTransOnetimeWorkReq.id
                        val livePaymentTransWork = worker.getWorkInfoLiveDataByID(workId)

                        livePaymentTransWork.observe(viewLifecycleOwner){ workInfo->

                            if( workInfo.state == WorkInfo.State.CANCELLED){
                                layout.progressBarLayout.visibility = GONE
                                layout.paymentCardsRecView.visibility = VISIBLE

                                layout.myBooksBtn.visibility = GONE
                                layout.progressBar.visibility = VISIBLE
                            }

                            if( workInfo.state == WorkInfo.State.SUCCEEDED){
                                layout.paymentCardsRecView.visibility = GONE
                                layout.progressBarLayout.visibility = VISIBLE

                                layout.progressMsgTxt.text = getString(R.string.payment_successful)

                                layout.myBooksBtn.visibility = VISIBLE
                                layout.progressBar.visibility = GONE
                            }

                            if( workInfo.state == WorkInfo.State.ENQUEUED){
                                layout.paymentCardsRecView.visibility = GONE
                                layout.progressBarLayout.visibility = VISIBLE

                                layout.progressMsgTxt.text = getString(R.string.processing_trans_msg)

                                layout.myBooksBtn.visibility = GONE
                                layout.progressBar.visibility = VISIBLE
                            }

                            if(workInfo.state == WorkInfo.State.FAILED){
                                showTransactionFailedMsg(getString(R.string.payment_unsuccessful))
                            }
                        }

                    }

                }

            }


            override fun beforeValidate(transaction: Transaction?) {}

            override fun onError(error: Throwable?, transaction: Transaction?) {
                error?.let {
                    showTransactionFailedMsg(it.localizedMessage?.plus(". ").plus(getString(R.string.try_again_later)))
                }
            }

        }
    }

    private fun showTransactionFailedMsg(sdkErrorMsg:String?){
        sdkErrorMsg?.let {
            AlertDialogBuilder.with(it, requireActivity())
                .setCancelable(false)
                .setPositiveAction(R.string.ok){}
                .build()
                .showDialog(R.string.payment_unsuccessful_title)
        }
    }

    private fun showPaymentNotSupportedForCardCurrencyDialog(){
        AlertDialogBuilder.with(R.string.supported_currency_hint, requireActivity())
            .setCancelable(false)
            .setPositiveAction(R.string.ok){}
            .build()
            .showDialog(R.string.unsupported_currency)
    }


}