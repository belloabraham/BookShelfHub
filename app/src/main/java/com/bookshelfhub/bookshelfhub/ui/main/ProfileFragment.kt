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
import com.bookshelfhub.bookshelfhub.Utils.IntentUtil
import com.bookshelfhub.bookshelfhub.Utils.SettingsUtil
import com.bookshelfhub.bookshelfhub.WebViewActivity
import com.bookshelfhub.bookshelfhub.config.RemoteConfig
import com.bookshelfhub.bookshelfhub.databinding.FragmentProfileBinding
import com.bookshelfhub.bookshelfhub.enums.WebView
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

    private val mainActivityViewModel: MainActivityViewModel by activityViewModels()

    private lateinit var layout: FragmentProfileBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        layout= FragmentProfileBinding.inflate(inflater, container, false)


        layout.aboutCard.setOnClickListener {

        }
        layout.reviewCard.setOnClickListener {
            startActivity(intentUtil.openAppStoreIntent(requireActivity().packageName))
        }
        layout.updateCard.setOnClickListener {
            startActivity(intentUtil.openAppStoreIntent(requireActivity().packageName))
        }

        layout.signOutCard.setOnClickListener {
        }

        layout.privacyPolicyCard.setOnClickListener {

            val url = remoteConfig.getString(PRIVACY_URL)
            val intent = Intent(requireActivity(), WebViewActivity::class.java)
            with(intent){
                putExtra(WebView.TITLE.KEY,getString(R.string.privacy))
                putExtra(WebView.URL.KEY, url)
            }
            startActivity(intent)
        }
        layout.termsOfUseCard.setOnClickListener {
            val url = remoteConfig.getString(TERMS_URL)
            val intent = Intent(requireActivity(), WebViewActivity::class.java)
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

        mainActivityViewModel.getIsAppUpdated().observe(viewLifecycleOwner, Observer { isAppUdated ->
            if (!isAppUdated){
               layout.updateCard.visibility = View.VISIBLE
            }
        })


        return layout.root
    }


}