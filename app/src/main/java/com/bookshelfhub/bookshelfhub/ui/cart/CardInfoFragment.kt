package com.bookshelfhub.bookshelfhub.ui.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.Utils.payment.CardUtil
import com.bookshelfhub.bookshelfhub.databinding.FragmentCardInfoBinding
import com.bookshelfhub.bookshelfhub.Utils.Regex
import com.bookshelfhub.bookshelfhub.helpers.database.ILocalDb
import com.bookshelfhub.bookshelfhub.helpers.database.room.entities.PaymentCard
import com.bookshelfhub.bookshelfhub.helpers.EditTextCreditCardNumberFormatterWatcher
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.WithFragmentBindings
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@AndroidEntryPoint
@WithFragmentBindings
class CardInfoFragment : Fragment() {

    private lateinit var layout: FragmentCardInfoBinding
    private val cartViewModel: CartViewModel by activityViewModels()
    @Inject
    lateinit var localDb: ILocalDb
    private val cardNoSeparator = "-"
    private val cardExpDateSeparator = "/"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        layout = FragmentCardInfoBinding.inflate(inflater, container, false)


        layout.cardNoTxt.addTextChangedListener(EditTextCreditCardNumberFormatterWatcher(maxUserInputLength = 16,
            inputChuckDividerChar = cardNoSeparator, inputChunkLen = 4){

            when {
                it.toString().contains(Regex.VISA) -> {
                    layout.cardNoTxt.setCompoundDrawablesWithIntrinsicBounds(0,0, R.drawable.card_visa,0)
                }
                it.toString().contains(Regex.MASTERCARD) -> {
                    layout.cardNoTxt.setCompoundDrawablesWithIntrinsicBounds(0,0, R.drawable.card_master,0)
                }
                it.toString().contains(Regex.VERVE) -> {
                    layout.cardNoTxt.setCompoundDrawablesWithIntrinsicBounds(0,0, R.drawable.card_verve,0)
                }
                it.toString().contains(Regex.AMERICAN_EXPRESS) -> {
                    layout.cardNoTxt.setCompoundDrawablesWithIntrinsicBounds(0,0, R.drawable.card_american_express,0)
                }
                it.toString().contains(Regex.JCB) -> {
                    layout.cardNoTxt.setCompoundDrawablesWithIntrinsicBounds(0,0, R.drawable.card_jcb,0)
                }
                it.toString().contains(Regex.DINERS_CLUB) -> {
                    layout.cardNoTxt.setCompoundDrawablesWithIntrinsicBounds(0,0, R.drawable.card_diners_clud,0)
                }
                it.toString().contains(Regex.DISCOVER) -> {
                    layout.cardNoTxt.setCompoundDrawablesWithIntrinsicBounds(0,0, R.drawable.card_discover,0)
                }
                else -> {
                    layout.cardNoTxt.setCompoundDrawablesWithIntrinsicBounds(0,0, R.drawable.card_others,0)
                }
            }

        })


        layout.cardExpiryDateTxt.addTextChangedListener(
            EditTextCreditCardNumberFormatterWatcher(maxUserInputLength = 4,
                inputChuckDividerChar = cardExpDateSeparator, inputChunkLen = 2)
        )


        layout.proceedBtn.setOnClickListener {
            layout.errorTxt.text = null
            layout.cardNoLayout.error = null

            val cardNo = layout.cardNoTxt.text.toString().trim().replace(cardNoSeparator,"")
            val cardCVC = layout.cvvTxt.toString()
            val cardMMYY = layout.cardExpiryDateTxt.toString()
            val cardExpMonth:Int
            val cardExpYear:Int
            //***If cardMMYY is properly formatted
            if (cardMMYY.length==5 && cardMMYY.contains(cardExpDateSeparator)){
                cardExpMonth = cardMMYY.split(cardExpDateSeparator)[0].toInt()
                cardExpYear = cardMMYY.split(cardExpDateSeparator)[1].toInt()

                val paymentCard = PaymentCard(cardNo, cardExpMonth, cardExpYear, cardCVC)

                val cardUtil = CardUtil(paymentCard)

                if (!cardUtil.isValidCardNo()){
                    layout.cardNoLayout.error = getString(R.string.invalid_card_no)
                }else  if (!cardUtil.isValidCVC()){
                    layout.errorTxt.text = getString(R.string.invalid_cvc_no)
                }else  if (!cardUtil.isValidExpDate()){
                    layout.errorTxt.text = getString(R.string.invalid_card_exp_date)
                }else{
                    paymentCard.cardType = cardUtil.getCardType()
                    paymentCard.lastFourDigit = cardUtil.getLastForDigit()

                    /**
                     * Card is added here with lifecycleScope instead of with viewModel
                     * to ensure data is completely added before nav controller navigates back
                     */

                    viewLifecycleOwner.lifecycleScope.launch(IO){
                        localDb.addPaymentCard(paymentCard)
                        withContext(Main){
                            //Set is new card to notify Cart fragment that there is now a new card
                            cartViewModel.setIsNewCard(true)
                            findNavController().navigateUp()
                        }
                    }
                }

            }else{
                layout.errorTxt.text = getString(R.string.invalid_cvc_no)
            }

        }

        return layout.root
    }


}