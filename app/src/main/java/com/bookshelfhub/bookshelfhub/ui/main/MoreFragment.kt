package com.bookshelfhub.bookshelfhub.ui.main

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.bitvale.switcher.SwitcherX
import com.bookshelfhub.bookshelfhub.*
import com.bookshelfhub.bookshelfhub.Utils.IntentUtil
import com.bookshelfhub.bookshelfhub.Utils.SettingsUtil
import com.bookshelfhub.bookshelfhub.Utils.StringUtil
import com.bookshelfhub.bookshelfhub.config.RemoteConfig
import com.bookshelfhub.bookshelfhub.databinding.FragmentMoreBinding
import com.bookshelfhub.bookshelfhub.enums.*
import com.bookshelfhub.bookshelfhub.helpers.AlertDialogHelper
import com.bookshelfhub.bookshelfhub.helpers.ClipboardHelper
import com.bookshelfhub.bookshelfhub.helpers.MaterialDialogHelper
import com.bookshelfhub.bookshelfhub.services.authentication.GoogleAuth
import com.bookshelfhub.bookshelfhub.services.authentication.UserAuth
import com.bookshelfhub.bookshelfhub.view.toast.Toast
import com.bookshelfhub.bookshelfhub.wrapper.dynamiclink.DynamicLink
import com.google.android.material.card.MaterialCardView
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.WithFragmentBindings
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
@WithFragmentBindings
class MoreFragment : Fragment() {

    private val PRIVACY_URL = "privacy_url"
    private val TERMS_URL = "terms_url"
    private val REFUND_POLICY_URL = "refund_policy_url"
    @Inject
    lateinit var intentUtil: IntentUtil
    @Inject
    lateinit var remoteConfig: RemoteConfig
    @Inject
    lateinit var settingsUtil: SettingsUtil
    @Inject
    lateinit var clipboardHelper: ClipboardHelper
    @Inject
    lateinit var userAuth: UserAuth
    @Inject
    lateinit var stringUtil: StringUtil
    @Inject
    lateinit var dynamicLink: DynamicLink
    private lateinit var authType:String
    private val mainActivityViewModel: MainActivityViewModel by activityViewModels()

    private lateinit var layout: FragmentMoreBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        layout= FragmentMoreBinding.inflate(inflater, container, false)
        val signOutBtn = layout.accountBtn.findViewById<MaterialCardView>(R.id.signOutCard)
        val progressPopupToggle = layout.settingsBtn.findViewById<SwitcherX>(R.id.progressPopupToggle)
        val darkModeToggle = layout.settingsBtn.findViewById<SwitcherX>(R.id.darkModeToggle)
        val profileBtn = layout.accountBtn.findViewById<MaterialCardView>(R.id.profileCard)

        authType= userAuth.getAuthType()

        lifecycleScope.launch(IO){
            val isChecked = settingsUtil.getBoolean(Settings.SHOW_CONTINUE_POPUP.KEY, true)
            withContext(Main){
                progressPopupToggle.setChecked(isChecked, false)
            }
        }

        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_NO -> {
                darkModeToggle.setChecked(false, false)
            }
            Configuration.UI_MODE_NIGHT_YES -> {
                darkModeToggle.setChecked(true, false)
            }
        }

        darkModeToggle.setOnCheckedChangeListener { isChecked->
            mainActivityViewModel.setIsNightMode(isChecked)
        }

        progressPopupToggle.setOnCheckedChangeListener { isChecked->
            lifecycleScope.launch(IO) {
                settingsUtil.setBoolean(Settings.SHOW_CONTINUE_POPUP.KEY, isChecked)
            }
        }

        layout.aboutCard.setOnClickListener {
            startProfileActivity(R.string.about, R.id.aboutFragment)
        }

        layout.reviewCard.setOnClickListener {
            activity?.let {
                startActivity(intentUtil.openAppStoreIntent(it.packageName))
            }
        }
        layout.updateCard.setOnClickListener {
            activity?.let {
                startActivity(intentUtil.openAppStoreIntent(it.packageName))
            }
        }

        profileBtn.setOnClickListener {
            startProfileActivity(R.string.profile, R.id.profileFragment)
        }

        signOutBtn.setOnClickListener {
            AlertDialogHelper(activity,{
                if (authType==AuthType.GOOGLE.ID){
                    userAuth.signOut {
                        activity?.let {
                            GoogleAuth(it, null, R.string.gcp_web_client).signOut {
                                it.finish()
                                startActivity(Intent(it, SplashActivity::class.java))
                            }
                        }
                    }
                }else{
                    userAuth.signOut {
                        activity?.finish()
                        startActivity(Intent(activity, SplashActivity::class.java))
                    }
                }
            }, cancelable = true
            ).showAlertDialog(R.string.sign_out,R.string.sign_out_message,R.string.sign_out, R.string.cancel)
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

        }

        layout.verifyPhoneCard.setOnClickListener {

        }
        layout.verifyEmailCard.setOnClickListener {

        }
        layout.needHelpCard.setOnClickListener {

        }

        layout.interestCard.setOnClickListener {
            startProfileActivity(R.string.book_interest, R.id.interestFragment)
        }

        layout.referraLinkCard.setOnClickListener {
            activity?.let { activity ->
                var link = mainActivityViewModel.getUserReferralLink()
                if (link==null){
                    lifecycleScope.launch(IO){
                      link =  settingsUtil.getString(PubReferrer.USER_REF_LINK.KEY)
                        withContext(Main){
                            if (link==null){
                                dynamicLink.getLinkAsync(
                                    remoteConfig.getString(UserReferrer.USER_REF_TITLE.KEY),
                                    remoteConfig.getString(UserReferrer.USER_REF_DESC.KEY),
                                    remoteConfig.getString(UserReferrer.USER_REF_IMAGE_URI.KEY),
                                    userAuth.getUserId()
                                ){
                                    if (it!=null){
                                        showReferralLinkDialog(it.toString(), activity)
                                    }
                                }
                            }else{
                                showReferralLinkDialog(link!!, activity)
                            }
                        }
                    }
                }else{
                   showReferralLinkDialog(link!!, activity)
                }
            }
        }

        layout.publishBookCard.setOnClickListener {
            val link = getString(R.string.publishers_link)
            MaterialDialogHelper(viewLifecycleOwner, requireContext(),{
                clipboardHelper.copyToClipBoard(link)
                activity?.let {
                    Toast(it).showToast(R.string.link_copied)
                }
            })
                .showAlertDialog(R.string.publish_book, R.string.publish_book_msg, R.string.copy_link, R.string.ok){
                   startActivity(intentUtil.intent(link))
                }
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


    private fun showReferralLinkDialog(link:String, activity:Activity){
        val msg = String.format(getString(R.string.your_referral_link), link )
        AlertDialogHelper(activity, {
            clipboardHelper.copyToClipBoard(link)
                Toast(activity).showToast(R.string.link_copied)
        }, cancelable = true)
            .showAlertDialog(R.string.referral_link, msg, R.string.copy_link, R.string.ok)
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

    private fun startProfileActivity(title:Int, fragmentID:Int){
        val intent = Intent(activity, MoreActivity::class.java)
        with(intent){
            putExtra(Profile.TITLE.KEY,title)
            putExtra(Profile.FRAGMENT_ID.KEY, fragmentID)
        }
        startActivity(intent)
    }

    companion object {
        @JvmStatic
        fun newInstance(): MoreFragment {
            return MoreFragment()
        }
    }

}