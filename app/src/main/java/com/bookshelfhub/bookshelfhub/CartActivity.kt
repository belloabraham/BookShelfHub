package com.bookshelfhub.bookshelfhub

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import co.paystack.android.Paystack
import co.paystack.android.Transaction
import com.afollestad.materialdialogs.LayoutMode
import com.bookshelfhub.bookshelfhub.adapters.recycler.PaymentCardsAdapter
import com.bookshelfhub.bookshelfhub.data.Config
import com.bookshelfhub.bookshelfhub.data.models.entities.PaymentCard
import com.bookshelfhub.bookshelfhub.data.models.entities.PaymentTransaction
import com.bookshelfhub.bookshelfhub.databinding.ActivityCartBinding
import com.bookshelfhub.bookshelfhub.helpers.Json
import com.bookshelfhub.bookshelfhub.helpers.payment.*
import com.bookshelfhub.bookshelfhub.helpers.utils.Location
import com.bookshelfhub.bookshelfhub.ui.cart.CartFragmentDirections
import com.bookshelfhub.bookshelfhub.views.AlertDialogBuilder
import com.bookshelfhub.bookshelfhub.views.MaterialBottomSheetDialogBuilder
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint
import me.ibrahimyilmaz.kiel.core.RecyclerViewHolder
import javax.inject.Inject

@AndroidEntryPoint
class CartActivity : AppCompatActivity() {

  private lateinit var layout: ActivityCartBinding
  private lateinit var navController: NavController
  private lateinit var appBarConfiguration: AppBarConfiguration
  private val cartActivityViewModel: CartActivityViewModel by viewModels()
  private var mView:View?=null
  private var paymentCardsAdapter: ListAdapter<PaymentCard, RecyclerViewHolder<PaymentCard>>?=null
  private var savedPaymentCards = emptyList<PaymentCard>()

  @Inject
  lateinit var json: Json


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    layout = ActivityCartBinding.inflate(layoutInflater)
    setContentView(layout.root)
    setSupportActionBar(layout.toolbar)

    val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
    navController = navHostFragment.findNavController()
    appBarConfiguration = AppBarConfiguration(navController.graph)
    setupActionBarWithNavController(navController, appBarConfiguration)

    cartActivityViewModel.getLivePaymentCards().observe(this, Observer { savedPaymentCards->
      this.savedPaymentCards = savedPaymentCards
    })

    cartActivityViewModel.shouldShowPaymentCardVisible().observe(this, Observer { shouldShowPaymentCards->
       if(shouldShowPaymentCards){
         showSavedPaymentCardList()
       }
    })

  }

  private fun getPaymentSDK(): PaymentSDKType? {
    val countryCode = Location.getCountryCode(this)
    return if(countryCode==null) null else PaymentSDK.getType(countryCode)
  }

  private fun showPaymentNotSupportedForYourRegionDialog(){
    AlertDialogBuilder.with(R.string.location_not_supported, this)
      .setCancelable(false)
      .setPositiveAction(R.string.ok){}
      .build()
      .showDialog(R.string.unsupported_region)
  }

  private fun showSavedPaymentCardList(){

    val paymentSDKType = getPaymentSDK()
    val paymentSDKExistForUserCountry = paymentSDKType!=null

    if(paymentSDKExistForUserCountry){

      mView = View.inflate(this, R.layout.saved_cards, layout.root)
      val addCardBtn = mView!!.findViewById<MaterialButton>(R.id.addNewCardBtn)

      if (savedPaymentCards.isNotEmpty()){

        val recyclerView = mView!!.findViewById<RecyclerView>(R.id.paymentCardsRecView)

        paymentCardsAdapter = PaymentCardsAdapter(this).getPaymentListAdapter{ paymentCard->
          //Callback for when user click on any of the payment cards on the list
          chargeCard(paymentSDKType!!, paymentCard)
        }

        recyclerView.addItemDecoration(
          DividerItemDecoration(
            this,
            DividerItemDecoration.VERTICAL
          )
        )
        recyclerView.adapter = paymentCardsAdapter!!
        paymentCardsAdapter!!.submitList(savedPaymentCards)
      }

      addCardBtn.setOnClickListener {
        val actionCardInfo = CartFragmentDirections.actionCartFragmentToCardInfoFragment()
        navController.navigate(actionCardInfo)
      }

      MaterialBottomSheetDialogBuilder(this, this)
        .setPositiveAction(R.string.cancel){}
        .setOnDismissListener {
          cartActivityViewModel.setIsPaymentCardVisible(false)
        }
        .showBottomSheet(mView, cornerRadius = 0f, layoutMode = LayoutMode.MATCH_PARENT)
    }

    val paymentSDKDoesNotExistForUserCountry = !paymentSDKExistForUserCountry
    if (paymentSDKDoesNotExistForUserCountry){
      showPaymentNotSupportedForYourRegionDialog()
    }

  }


  private fun showTransactionFailedMsg(sdkErrorMsg:String?){
    sdkErrorMsg?.let {
      val errorMsg = String.format(getString(R.string.transaction_error), it)
      AlertDialogBuilder.with(errorMsg, this)
        .setCancelable(false)
        .setPositiveAction(R.string.ok){}
        .build()
        .showDialog(R.string.transaction_failed)
    }

  }

  private fun showPaymentProcessingMsg(){
    AlertDialogBuilder.with(R.string.transaction_processing, this)
      .setCancelable(false)
      .setPositiveAction(R.string.ok){
        this.finish()
      }
      .build()
      .showDialog(R.string.processing_transaction)
  }

  override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

}
