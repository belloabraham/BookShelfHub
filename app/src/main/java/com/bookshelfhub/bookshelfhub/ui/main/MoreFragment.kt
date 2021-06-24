package com.bookshelfhub.bookshelfhub.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.bookshelfhub.bookshelfhub.*
import com.bookshelfhub.bookshelfhub.Utils.IntentUtil
import com.bookshelfhub.bookshelfhub.Utils.SettingsUtil
import com.bookshelfhub.bookshelfhub.Utils.StringUtil
import com.bookshelfhub.bookshelfhub.config.RemoteConfig
import com.bookshelfhub.bookshelfhub.databinding.FragmentMoreBinding
import com.bookshelfhub.bookshelfhub.enums.AuthType
import com.bookshelfhub.bookshelfhub.enums.Profile
import com.bookshelfhub.bookshelfhub.enums.Settings
import com.bookshelfhub.bookshelfhub.enums.WebView
import com.bookshelfhub.bookshelfhub.helpers.AlertDialogHelper
import com.bookshelfhub.bookshelfhub.helpers.ClipboardHelper
import com.bookshelfhub.bookshelfhub.helpers.MaterialDialogHelper
import com.bookshelfhub.bookshelfhub.services.authentication.GoogleAuth
import com.bookshelfhub.bookshelfhub.services.authentication.UserAuth
import com.bookshelfhub.bookshelfhub.view.toast.Toast
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
    private lateinit var authType:String
    private val mainActivityViewModel: MainActivityViewModel by activityViewModels()

    private lateinit var layout: FragmentMoreBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        layout= FragmentMoreBinding.inflate(inflater, container, false)

        authType= userAuth.getAuthType()

        lifecycleScope.launch(IO){
           val isChecked = settingsUtil.getBoolean(Settings.SHOW_CONTINUE_POPUP.KEY, true)
            withContext(Main){
                layout.progressPopupToggle.setChecked(isChecked, false)
            }
        }

        //layout.accountCard.setOnClickListener {
         //   startProfileActivity(R.string.account, R.id.accountFragment)
      //  }

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

        layout.signOutCard.setOnClickListener {
            AlertDialogHelper(activity,{
                if (authType==AuthType.GOOGLE.ID){
                    userAuth.signOut {
                        activity?.let {
                            GoogleAuth(it, null).signOut {
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

            val url = remoteConfig.getString(PRIVACY_URL)
            val intent = Intent(activity, WebViewActivity::class.java)
            with(intent){
                putExtra(WebView.TITLE.KEY,getString(R.string.privacy))
                putExtra(WebView.URL.KEY, url)
            }
            startActivity(intent)
        }
        layout.termsOfUseCard.setOnClickListener {
            val url = remoteConfig.getString(TERMS_URL)
            val intent = Intent(activity, WebViewActivity::class.java)
            with(intent){
                putExtra(WebView.TITLE.KEY,getString(R.string.terms))
                putExtra(WebView.URL.KEY, url)
            }
            startActivity(intent)
        }

        layout.verifyPhoneCard.setOnClickListener {

        }
        layout.verifyEmailCard.setOnClickListener {

        }
        layout.needHelpCard.setOnClickListener {

        }

        layout.inviteFreindsCard.setOnClickListener {

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

        layout.progressPopupToggle.setOnCheckedChangeListener { isChecked->
            lifecycleScope.launch(IO) {
                settingsUtil.setBoolean(Settings.SHOW_CONTINUE_POPUP.KEY, isChecked)
            }
        }

        mainActivityViewModel.getNewAppUpdateNotifNumber().observe(viewLifecycleOwner, Observer { notifNumber ->
            if (notifNumber>0){
               layout.updateCard.visibility = View.VISIBLE
            }
        })

        mainActivityViewModel.getUserRecord().observe(viewLifecycleOwner, Observer { userRecord ->
          if (userRecord.mailOrPhoneVerified){
              layout.verifyEmailCard.visibility = View.GONE
              layout.verifyPhoneCard.visibility = View.GONE
          }else{
              if (authType==AuthType.GOOGLE.ID){
                  layout.verifyEmailCard.visibility = View.GONE
              }else{
                  layout.verifyPhoneCard.visibility = View.GONE
              }
          }
        })

        return layout.root
    }


    private fun startProfileActivity(title:Int, fragmentID:Int){
        val intent = Intent(activity, ProfileActivity::class.java)
        with(intent){
            putExtra(Profile.TITLE.KEY,title)
            putExtra(Profile.FRAGMENT_ID.KEY, fragmentID)
        }
        startActivity(intent)
    }

}