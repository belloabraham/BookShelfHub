package com.bookshelfhub.bookshelfhub.ui.main

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.bitvale.switcher.SwitcherX
import com.bookshelfhub.bookshelfhub.*
import com.bookshelfhub.bookshelfhub.Utils.ConnectionUtil
import com.bookshelfhub.bookshelfhub.Utils.IntentUtil
import com.bookshelfhub.bookshelfhub.Utils.ShareUtil
import com.bookshelfhub.bookshelfhub.Utils.settings.Settings
import com.bookshelfhub.bookshelfhub.Utils.settings.SettingsUtil
import com.bookshelfhub.bookshelfhub.databinding.FragmentMoreBinding
import com.bookshelfhub.bookshelfhub.enums.WebView
import com.bookshelfhub.bookshelfhub.extensions.showToast
import com.bookshelfhub.bookshelfhub.helpers.AlertDialogBuilder
import com.bookshelfhub.bookshelfhub.helpers.MaterialBottomSheetDialogBuilder
import com.bookshelfhub.bookshelfhub.helpers.clipboard.ClipboardHelper
import com.bookshelfhub.bookshelfhub.helpers.dynamiclink.IDynamicLink
import com.bookshelfhub.bookshelfhub.helpers.dynamiclink.Referrer
import com.bookshelfhub.bookshelfhub.helpers.dynamiclink.Social
import com.bookshelfhub.bookshelfhub.models.Earnings
import com.bookshelfhub.bookshelfhub.services.authentication.AuthType
import com.bookshelfhub.bookshelfhub.services.authentication.IGoogleAuth
import com.bookshelfhub.bookshelfhub.services.authentication.IUserAuth
import com.bookshelfhub.bookshelfhub.services.authentication.firebase.GoogleAuth
import com.bookshelfhub.bookshelfhub.services.database.cloud.DbFields
import com.bookshelfhub.bookshelfhub.services.database.cloud.ICloudDb
import com.bookshelfhub.bookshelfhub.services.remoteconfig.IRemoteConfig
import com.google.android.gms.tasks.Tasks
import com.google.android.material.card.MaterialCardView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.WithFragmentBindings
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
@WithFragmentBindings
class MoreFragment : Fragment() {

    private val PRIVACY_URL = "privacy_url"
    private val TERMS_URL = "terms_url"
    private val REFUND_POLICY_URL = "refund_policy_url"
    private val WA_CUSTOMER_SUPPORT = "wa_customer_support_link"

    @Inject
    lateinit var intentUtil: IntentUtil
    @Inject
    lateinit var remoteConfig: IRemoteConfig
    @Inject
    lateinit var settingsUtil: SettingsUtil
    @Inject
    lateinit var connectionUtil: ConnectionUtil
    @Inject
    lateinit var clipboardHelper: ClipboardHelper
    @Inject
    lateinit var userAuth: IUserAuth
    @Inject
    lateinit var cloudDb: ICloudDb
    @Inject
    lateinit var dynamicLink: IDynamicLink
    private lateinit var authType:String
    private val mainActivityViewModel: MainActivityViewModel by activityViewModels()
    private val moreViewModel:MoreViewModel by viewModels()
    private lateinit var userId:String

    private var binding: FragmentMoreBinding?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentMoreBinding.inflate(inflater, container, false)
        val layout = binding!!
        val signOutBtn = layout.accountDropDownView.findViewById<MaterialCardView>(R.id.signOutCard)
        val profileBtn = layout.accountDropDownView.findViewById<MaterialCardView>(R.id.profileCard)
        val deletePaymentCardsBtn = layout.accountDropDownView.findViewById<MaterialCardView>(R.id.deletePaymentCards)

        val progressPopupToggle = layout.settingsDropDownView.findViewById<SwitcherX>(R.id.progressPopupToggle)
        val darkModeToggle = layout.settingsDropDownView.findViewById<SwitcherX>(R.id.darkModeToggle)

        val googleAuth:IGoogleAuth =  GoogleAuth(requireActivity(), R.string.gcp_web_client)


        authType= userAuth.getAuthType()
        userId = userAuth.getUserId()

        viewLifecycleOwner.lifecycleScope.launch(IO){
            val isChecked = settingsUtil.getBoolean(Settings.SHOW_CONTINUE_POPUP.KEY, true)
            withContext(Main){
                progressPopupToggle.setChecked(isChecked, false)
            }
        }
        val isDarkMode =  when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_NO -> {
                false
            }
            else -> true
        }

        darkModeToggle.setChecked(isDarkMode, withAnimation = false)

        darkModeToggle.setOnCheckedChangeListener { isChecked->
            mainActivityViewModel.setIsNightMode(isChecked)
        }

        progressPopupToggle.setOnCheckedChangeListener { isChecked->
            lifecycleScope.launch(IO) {
                settingsUtil.setBoolean(Settings.SHOW_CONTINUE_POPUP.KEY, isChecked)
            }
        }

        layout.aboutCard.setOnClickListener {
            startMoreActivity(R.id.aboutFragment)
        }

        layout.reviewCard.setOnClickListener {
            activity?.let {
                startActivity(intentUtil.getAppStoreIntent(it.packageName))
            }
        }
        layout.updateCard.setOnClickListener {
            activity?.let {
                startActivity(intentUtil.getAppStoreIntent(it.packageName))
            }
        }

        profileBtn.setOnClickListener {
            startMoreActivity(R.id.profileFragment)
        }

        signOutBtn.setOnClickListener {

             AlertDialogBuilder.with(R.string.sign_out_message, requireActivity())
                 .setCancelable(true)
                 .setNegativeAction(R.string.cancel){}
                 .setPositiveAction(R.string.sign_out){
                     userAuth.signOut()
                     if (authType == AuthType.GOOGLE.ID){
                         viewLifecycleOwner.lifecycleScope.launch(IO){
                             try {
                                 googleAuth.signOut().await()
                                 withContext(Main){
                                     moreViewModel.deleteUserData()
                                     startSplashActivity()
                                 }
                             }catch (e:Exception){}
                         }
                     }else{
                         moreViewModel.deleteUserData()
                         startSplashActivity()
                     }

                 }.build()
                 .showDialog(R.string.sign_out)

        }

        layout.privacyPolicyCard.setOnClickListener {
            startWebActivity(R.string.privacy, PRIVACY_URL)
        }
        layout.termsOfUseCard.setOnClickListener {
            startWebActivity(R.string.terms, TERMS_URL)
        }
        layout.refundPolicyCard.setOnClickListener {
            startWebActivity(R.string.refund_policy, REFUND_POLICY_URL)
        }

        layout.earningsCard.setOnClickListener {

            if (!connectionUtil.isConnected()){
                Snackbar.make(
                    layout.container,
                    getString(R.string.earnings_connection_error),
                    Snackbar.LENGTH_SHORT
                ).apply {
                    setAction(getString(R.string.ok)){}
                    show()
                }
            }else{
                showEarnings()
            }
        }

        deletePaymentCardsBtn.setOnClickListener {
            moreViewModel.deleteAllPaymentCards()
            showToast(R.string.all_cards_deleted)
        }

        layout.needHelpCard.setOnClickListener {

          val link =   remoteConfig.getString(WA_CUSTOMER_SUPPORT)
            startActivity(intentUtil.intent(link))

        }

        layout.interestCard.setOnClickListener {
            startMoreActivity(R.id.interestFragment)
        }

        layout.referraLinkCard.setOnClickListener {
            activity?.let { activity ->
                var link = mainActivityViewModel.getUserReferralLink()
                if (link==null){
                    lifecycleScope.launch(IO){
                      link =  settingsUtil.getString(Referrer.REF_LINK.KEY)
                        withContext(Main){
                            if (link==null){
                                dynamicLink.generateShortLinkAsync(
                                    remoteConfig.getString(Social.TITLE.KEY),
                                    remoteConfig.getString(Social.DESC.KEY),
                                    remoteConfig.getString(Social.IMAGE_URL.KEY),
                                    userAuth.getUserId()
                                ){
                                    if (it!=null){
                                        getReferralLink(it.toString())
                                    }
                                }
                            }else{
                                getReferralLink(link!!)
                            }
                        }
                    }
                }else{
                    getReferralLink(link!!)
                }
            }
        }

        layout.publishBookCard.setOnClickListener {
            val link = getString(R.string.publishers_link)

            AlertDialogBuilder.with(R.string.publish_book_msg, requireActivity())
                .setPositiveAction(R.string.copy_link){
                    clipboardHelper.copyToClipBoard(link)
                    activity?.let {
                        showToast(R.string.link_copied)
                    }
                }
                .setNegativeAction(R.string.ok){}
                .build()
                .showDialog(R.string.publish_book)
        }


        mainActivityViewModel.getNewAppUpdateNotifNumber().observe(viewLifecycleOwner, Observer { notifNumber ->
            if (notifNumber>0){
               layout.updateCard.visibility = View.VISIBLE
            }
        })

        mainActivityViewModel.getBookInterest().observe(viewLifecycleOwner, Observer { bookInterest ->
            if(bookInterest.isPresent && bookInterest.get().added){
                layout.interestNotifCard.visibility = View.GONE

            }else{
                layout.interestNotifCard.visibility = View.VISIBLE
            }
        })

        mainActivityViewModel.getUserRecord().observe(viewLifecycleOwner, Observer { userRecord ->
         /*if (!userRecord.mailOrPhoneVerified){
              if (authType==AuthType.GOOGLE.ID){
                  layout.verifyPhoneCard.visibility = View.VISIBLE
              }else{
                  layout.verifyEmailCard.visibility = View.VISIBLE
              }
          }*/
        })

        return layout.root
    }

    private fun showEarnings(){

        val view = View.inflate(requireContext(), R.layout.earnings, null)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
        val earningsText = view.findViewById<TextView>(R.id.earningsText)

        cloudDb.getLiveListOfDataAsync(requireActivity(), DbFields.EARNINGS.KEY, DbFields.REFERRER_ID.KEY, userId, Earnings::class.java, shouldRetry = true) { earnings ->
            progressBar.visibility = GONE
            val totalEarnings = 0.0
            for (earning in earnings){
                totalEarnings.plus(earning.earn)
            }
            earningsText.text = String.format(getString(R.string.total_earnings), totalEarnings)
        }

        MaterialBottomSheetDialogBuilder(requireActivity(), viewLifecycleOwner)
            .setPositiveAction(R.string.ok){}
            .showBottomSheet(view)

    }

    private fun startSplashActivity(){
        activity?.let{
            it.finish()
            startActivity(Intent(it, SplashActivity::class.java))
        }
    }


    private fun startWebActivity(title:Int, rmcUrlKey:String){
        val url = remoteConfig.getString(rmcUrlKey)
        val intent = Intent(activity, WebViewActivity::class.java)
        with(intent){
            putExtra(WebView.TITLE.KEY,getString(title))
            putExtra(WebView.URL.KEY, url)
        }
        startActivity(intent)
    }

    private fun startMoreActivity(fragmentID:Int){
        val intent = Intent(activity, MoreActivity::class.java)
        with(intent){
            putExtra(com.bookshelfhub.bookshelfhub.enums.Fragment.ID.KEY, fragmentID)
        }
        startActivity(intent)
    }

    private fun getReferralLink(text:String){
        val title = getString(R.string.app_name)
        startActivity(ShareUtil.getShareIntent(text,title))
    }

    companion object {
        @JvmStatic
        fun newInstance(): MoreFragment {
            return MoreFragment()
        }
    }

    override fun onDestroyView() {
        binding=null
        super.onDestroyView()
    }

}