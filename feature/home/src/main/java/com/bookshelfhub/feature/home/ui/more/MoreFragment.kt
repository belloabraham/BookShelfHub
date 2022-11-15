package com.bookshelfhub.feature.home.ui.more

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bitvale.switcher.SwitcherX
import com.bookshelfhub.core.authentication.AuthType
import com.bookshelfhub.core.authentication.IGoogleAuth
import com.bookshelfhub.core.authentication.IUserAuth
import com.bookshelfhub.core.authentication.firebase.GoogleAuth
import com.bookshelfhub.core.common.extensions.isAppUsingDarkTheme
import com.bookshelfhub.core.common.extensions.showToast
import com.bookshelfhub.core.common.helpers.ErrorUtil
import com.bookshelfhub.core.common.helpers.clipboard.ClipboardHelper
import com.bookshelfhub.core.common.helpers.dialog.AlertDialogBuilder
import com.bookshelfhub.core.common.helpers.dialog.MaterialBottomSheetDialogBuilder
import com.bookshelfhub.core.common.helpers.utils.ConnectionUtil
import com.bookshelfhub.core.common.helpers.utils.IntentUtil
import com.bookshelfhub.core.common.helpers.utils.ShareUtil
import com.bookshelfhub.core.datastore.settings.Settings
import com.bookshelfhub.core.dynamic_link.IDynamicLink
import com.bookshelfhub.core.dynamic_link.Referrer
import com.bookshelfhub.core.remote.remote_config.IRemoteConfig
import com.bookshelfhub.feature.home.MainActivityViewModel
import com.bookshelfhub.feature.home.MoreActivity
import com.bookshelfhub.feature.home.R
import com.bookshelfhub.feature.home.databinding.FragmentMoreBinding
import com.bookshelfhub.feature.webview.WebView
import com.bookshelfhub.feature.webview.WebViewActivity
import com.google.android.material.card.MaterialCardView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.WithFragmentBindings
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
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
    lateinit var connectionUtil: ConnectionUtil
    @Inject
    lateinit var clipboardHelper: ClipboardHelper
    @Inject
    lateinit var userAuth: IUserAuth

    private lateinit var authType:String
    private val mainActivityViewModel: MainActivityViewModel by activityViewModels()
    private val moreViewModel: MoreViewModel by viewModels()
    private lateinit var userId:String

    private var binding: FragmentMoreBinding?=null

    @SuppressLint("ShowToast")
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


        authType= userAuth.getAuthType()
        userId = userAuth.getUserId()

        viewLifecycleOwner.lifecycleScope.launch{
            val isChecked = moreViewModel.getBoolean(Settings.SHOW_CONTINUE_POPUP)
            progressPopupToggle.setChecked(isChecked, false)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            val isDarkTheme = isAppUsingDarkTheme() || mainActivityViewModel.isDarkUserPreferedTheme()
            darkModeToggle.setChecked(isDarkTheme, withAnimation = false)
        }

        darkModeToggle.setOnCheckedChangeListener { isChecked->
            setAppTheme(isChecked)
        }

        progressPopupToggle.setOnCheckedChangeListener { isChecked->
            lifecycleScope.launch{
                moreViewModel.setBoolean(Settings.SHOW_CONTINUE_POPUP, isChecked)
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
                     if(authType == AuthType.PHONE){
                           moreViewModel.deleteUserData()
                           requireActivity().finish()
                     }else{
                         val googleAuth: IGoogleAuth =  GoogleAuth(requireActivity(), R.string.gcp_web_client)
                         viewLifecycleOwner.lifecycleScope.launch{
                             try {
                                 googleAuth.signOut().await()
                                 moreViewModel.deleteUserData()
                                 requireActivity().finish()
                             }catch (e:Exception){
                                 ErrorUtil.e(e)
                             }
                         }
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
            lifecycleScope.launch {
               val link =  moreViewModel.getString(Referrer.REF_LINK)
               if(link==null){
                  showToast(R.string.internet_connection_required)
               }else{
                   getReferralLink(link)
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


        mainActivityViewModel.getIsNewAppUpdateNotificationNumber().observe(viewLifecycleOwner) { notificationNumber ->
            val thereIsNewUpdate = notificationNumber > 0
            if (thereIsNewUpdate) {
                layout.updateCard.visibility = View.VISIBLE
            }
        }

        mainActivityViewModel.getLiveOptionalBookInterest().observe(viewLifecycleOwner
        ) { bookInterest ->
            if (bookInterest.isPresent && bookInterest.get().added) {
                layout.interestNotifCard.visibility = GONE
            } else {
                layout.interestNotifCard.visibility = View.VISIBLE
            }
        }

        moreViewModel.getLiveUserRecord().observe(viewLifecycleOwner) { _ ->
            /*if (!userRecord.mailOrPhoneVerified){
                 if (authType==AuthType.GOOGLE){
                     layout.verifyPhoneCard.visibility = View.VISIBLE
                 }else{
                     layout.verifyEmailCard.visibility = View.VISIBLE
                 }
             }*/
        }

        return layout.root
    }

    private fun showEarnings(){

        val view = View.inflate(requireContext(), R.layout.earnings, null)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
        val earningsText = view.findViewById<TextView>(R.id.earningsText)

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val earnings =   moreViewModel.getRemoteEarnings()
                val total = earnings?.total ?: 0
                earningsText.text = String.format(getString(R.string.total_earnings), total)
                progressBar.visibility = GONE
            }catch (e:Exception){
                ErrorUtil.e(e)
                return@launch
            }
        }

        MaterialBottomSheetDialogBuilder(requireActivity(), viewLifecycleOwner)
            .setPositiveAction(R.string.ok){}
            .showBottomSheet(view)

    }

    private fun setAppTheme(userSelectedDarkTheme: Boolean) {
        viewLifecycleOwner.lifecycleScope.launch {
            mainActivityViewModel.setIsDarkTheme(userSelectedDarkTheme)
        }
        val appIsUsingLightTheme = !isAppUsingDarkTheme()
        val userSelectedLightTheme = !userSelectedDarkTheme

        if (userSelectedDarkTheme && appIsUsingLightTheme) {
            //Set darkTheme
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }else if (userSelectedLightTheme && isAppUsingDarkTheme()){
            //Set Light theme
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    private fun startWebActivity(title:Int, rmcUrlKey:String){
        val url = remoteConfig.getString(rmcUrlKey)
        val intent = Intent(activity, WebViewActivity::class.java)
        with(intent){
            putExtra(WebView.TITLE,getString(title))
            putExtra(WebView.URL, url)
        }
        startActivity(intent)
    }

    private fun startMoreActivity(fragmentID:Int){
        val intent = Intent(activity, MoreActivity::class.java)
        with(intent){
            putExtra(com.bookshelfhub.core.data.Fragment.ID, fragmentID)
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