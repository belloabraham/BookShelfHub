package com.bookshelfhub.book.purchase.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bookshelfhub.book.purchase.R
import com.bookshelfhub.book.purchase.databinding.FragmentCardInfoBinding
import com.bookshelfhub.core.authentication.IUserAuth
import com.bookshelfhub.core.common.helpers.EditTextCreditCardNumberFormatterWatcher
import com.bookshelfhub.core.common.helpers.KeyboardUtil
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.WithFragmentBindings
import javax.inject.Inject
import com.bookshelfhub.core.common.helpers.utils.Regex
import com.bookshelfhub.core.model.entities.PaymentCard
import com.bookshelfhub.payment.CardUtil
import com.bookshelfhub.payment.SupportedCountries
import com.hbb20.countrypicker.dialog.launchCountryPickerDialog
import com.hbb20.countrypicker.models.CPCountry


@AndroidEntryPoint
@WithFragmentBindings
class CardInfoFragment : Fragment() {

    private var binding: FragmentCardInfoBinding?=null
    private val cardViewModel: CardInfoViewModel by viewModels()
    private val cardNoSeparator = " - "
    private val cardExpDateSeparator = "/"
    private var countryCode:String? = SupportedCountries.NIGERIA
    private var selectedCountryAndCurrency:String? = null
    @Inject
    lateinit var userAuth: IUserAuth
    private lateinit var layout: FragmentCardInfoBinding
    @Inject
    lateinit var keyboardUtil: KeyboardUtil

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentCardInfoBinding.inflate(inflater, container, false)
        layout = binding!!


        layout.cardNoTxt.addTextChangedListener(EditTextCreditCardNumberFormatterWatcher(maxUserInputLength = 16,
            inputChuckDividerChar = cardNoSeparator,
            inputChunkLen = 4
        ){ cardNumber->

            cardNumber?.let {
                when {
                    it.matches(Regex(Regex.VISA)) -> {
                        layout.cardNoTxt.setCompoundDrawablesWithIntrinsicBounds(0,0, R.drawable.card_visa,0)
                    }
                    it.matches(Regex((Regex.MASTERCARD))) -> {
                        layout.cardNoTxt.setCompoundDrawablesWithIntrinsicBounds(0,0, R.drawable.card_master,0)
                    }
                    it.matches(Regex((Regex.VERVE))) -> {
                        layout.cardNoTxt.setCompoundDrawablesWithIntrinsicBounds(0,0, R.drawable.card_verve,0)
                    }
                    it.matches(Regex((Regex.AMERICAN_EXPRESS))) -> {
                        layout.cardNoTxt.setCompoundDrawablesWithIntrinsicBounds(0,0, R.drawable.card_american_express,0)
                    }
                    it.matches(Regex((Regex.JCB))) -> {
                        layout.cardNoTxt.setCompoundDrawablesWithIntrinsicBounds(0,0, R.drawable.card_jcb,0)
                    }
                    it.matches(Regex((Regex.DINERS_CLUB))) -> {
                        layout.cardNoTxt.setCompoundDrawablesWithIntrinsicBounds(0,0, R.drawable.card_diners_clud,0)
                    }
                    it.matches(Regex((Regex.DISCOVER))) -> {
                        layout.cardNoTxt.setCompoundDrawablesWithIntrinsicBounds(0,0, R.drawable.card_discover,0)
                    }
                    it.isBlank() -> {
                        layout.cardNoTxt.setCompoundDrawablesWithIntrinsicBounds(0,0, R.drawable.card_discover,0)
                    }
                }
            }
        })

        layout.cardExpiryDateTxt.addTextChangedListener(
            EditTextCreditCardNumberFormatterWatcher(maxUserInputLength = 4,
                inputChuckDividerChar = cardExpDateSeparator, inputChunkLen = 2)
        )

        layout.countryDropDown.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus){
                keyboardUtil.hideKeyboard(layout.cvvTxt)
            }

            requireActivity().launchCountryPickerDialog(
                primaryTextGenerator = { cpCountry -> "${cpCountry.name} (${cpCountry.currencySymbol})" },
                allowClearSelection = true,
            ) { selectedCountry: CPCountry? ->
                countryCode = selectedCountry?.alpha2
                selectedCountryAndCurrency = "${selectedCountry!!.name} (${selectedCountry.currencySymbol})"
                val userSelectedACountry = countryCode != null
                if(userSelectedACountry){
                    layout.countryLayout.hint = selectedCountryAndCurrency
                }
            }
        }

        layout.proceedBtn.setOnClickListener {
            layout.errorTxt.text = null
            layout.cardNoLayout.error = null
            layout.cardExpiryDateLayout.error = null
            layout.cvvLayout.error = null

            val cardNo = layout.cardNoTxt.text.toString().trim().replace(cardNoSeparator,"")
            val cardCVC = layout.cvvTxt.text.toString().replace(cardExpDateSeparator, "")
            val cardMMYY = layout.cardExpiryDateTxt.text.toString()

            val cardExpMonth:Int = cardMMYY.split(cardExpDateSeparator)[0].toInt()
            val cardExpYear:Int = cardMMYY.split(cardExpDateSeparator)[1].toInt()

            val cardCurrencyNotSupported = SupportedCountries.getAll().find {
                it == countryCode
            } == null

            val paymentCard = PaymentCard(cardNo, cardExpMonth, cardExpYear, cardCVC)
            val cardUtil = CardUtil(paymentCard)

            if (!cardUtil.isValidCardNo()){
                layout.cardNoLayout.error = getString(R.string.invalid_card_no)
            }else  if (!cardUtil.isValidCVC()){
                layout.errorTxt.text = getString(R.string.invalid_cvc_no)
                layout.cvvLayout.error = ""
            }else  if (!cardUtil.isValidExpDate()){
                layout.errorTxt.text = getString(R.string.invalid_card_exp_date)
                layout.cardExpiryDateLayout.error = ""
            }else if(countryCode == null){
                layout.errorTxt.text = getString(R.string.card_issued_country_required)
            }else if(cardCurrencyNotSupported){
                layout.errorTxt.text = getString(R.string.supported_currency_hint)
            }else{
                    paymentCard.cardType = cardUtil.getCardType()
                    paymentCard.lastFourDigit = cardUtil.getLastForDigit()
                    paymentCard.countryCode = countryCode!!
                    cardViewModel.addPaymentCard(paymentCard)
                    findNavController().navigateUp()
            }

        }

        val nigeria = resources.getStringArray(R.array.nigeriaPlaceHolder)
        val spinnerAdapter: ArrayAdapter<*> = ArrayAdapter<Any?>(requireContext(), com.bookshelfhub.core.resources.R.layout.spinner_item, nigeria)
        layout.countryDropDown.setAdapter(spinnerAdapter)

        return layout.root
    }

    override fun onResume() {
        super.onResume()
        selectedCountryAndCurrency?.let {
            layout.countryLayout.hint = it
        }
    }

    override fun onDestroyView() {
        binding=null
        super.onDestroyView()
    }

}