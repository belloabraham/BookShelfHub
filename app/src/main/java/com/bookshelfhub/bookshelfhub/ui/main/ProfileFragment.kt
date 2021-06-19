package com.bookshelfhub.bookshelfhub.ui.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.bookshelfhub.bookshelfhub.MainActivityViewModel
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.SplashActivity
import com.bookshelfhub.bookshelfhub.Utils.IntentUtil
import com.bookshelfhub.bookshelfhub.Utils.SettingsUtil
import com.bookshelfhub.bookshelfhub.WebViewActivity
import com.bookshelfhub.bookshelfhub.config.RemoteConfig
import com.bookshelfhub.bookshelfhub.databinding.FragmentProfileBinding
import com.bookshelfhub.bookshelfhub.enums.Settings
import com.bookshelfhub.bookshelfhub.enums.WebView
import com.bookshelfhub.bookshelfhub.helpers.AlertDialogHelper
import com.bookshelfhub.bookshelfhub.services.authentication.UserAuth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.WithFragmentBindings
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
@WithFragmentBindings
class ProfileFragment : Fragment() {

    private val PRIVACY_URL = "privacy_url"
    private val TERMS_URL = "terms_url"
    @Inject
    lateinit var remoteConfig: RemoteConfig
    @Inject
    lateinit var intentUtil: IntentUtil
    @Inject
    lateinit var settingsUtil: SettingsUtil
    @Inject
    lateinit var userAuth: UserAuth

    private val mainActivityViewModel: MainActivityViewModel by activityViewModels()

    private lateinit var layout: FragmentProfileBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        layout= FragmentProfileBinding.inflate(inflater, container, false)

        lifecycleScope.launch(IO){
           val isChecked = settingsUtil.getBoolean(Settings.SHOW_CONTINUE_POPUP.KEY, true)
            withContext(Main){
                layout.progressPopupToggle.setChecked(isChecked, true)
            }

        }

        layout.aboutCard.setOnClickListener {

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
                userAuth.signOut(activity) {
                    activity?.finish()
                    startActivity(Intent(activity, SplashActivity::class.java))
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
            AlertDialogHelper(activity,{

            }, cancelable = true
            ).showAlertDialog(R.string.publish_book,R.string.sign_out_message,R.string.sign_out, R.string.cancel)
        }

        layout.progressPopupToggle.setOnCheckedChangeListener { isChecked->
            lifecycleScope.launch(IO) {
                settingsUtil.setBoolean(Settings.SHOW_CONTINUE_POPUP.KEY, isChecked)
            }
        }

        mainActivityViewModel.getIsAppUpdated().observe(viewLifecycleOwner, Observer { isAppUpdated ->
            if (!isAppUpdated){
               layout.updateCard.visibility = View.VISIBLE
            }
        })


        return layout.root
    }


}