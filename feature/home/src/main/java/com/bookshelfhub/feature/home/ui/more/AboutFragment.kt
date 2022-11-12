package com.bookshelfhub.feature.home.ui.more

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import com.bookshelfhub.core.common.helpers.utils.AnimUtil
import com.bookshelfhub.core.common.helpers.utils.AppUtil
import com.bookshelfhub.core.common.helpers.utils.datetime.DateTimeUtil
import com.bookshelfhub.core.resources.R
import com.bookshelfhub.core.remote.remote_config.RemoteConfig
import com.bookshelfhub.core.remote.remote_config.IRemoteConfig
import com.bookshelfhub.core.ui.views.about.AutoFitGridLayout
import com.bookshelfhub.core.ui.views.about.Item
import com.bookshelfhub.core.ui.views.about.builder.AboutPageBuilder
import com.bookshelfhub.feature.home.databinding.FragmentAboutBinding
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.WithFragmentBindings
import javax.inject.Inject


@AndroidEntryPoint
@WithFragmentBindings
class AboutFragment : Fragment() {


    private val aboutPageLinkItemsAnimDuration = 200L
    @Inject
    lateinit var appUtil: AppUtil
    @Inject
    lateinit var remoteConfig: IRemoteConfig
    private var binding: FragmentAboutBinding?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentAboutBinding.inflate(inflater, container, false)
        val layout = binding!!

        layout.versionText.text = String.format(getString(R.string.version), appUtil.getAppVersionName())
        layout.copyrightText.text = String.format(getString(R.string.copyright), DateTimeUtil.getYear())

        layout.contactGrid.setColumnCount(3)
        layout.socialGrid.setColumnCount(3)
        val email = remoteConfig.getString(RemoteConfig.EMAIL)
        val phoneNumber = remoteConfig.getString(RemoteConfig.PHONE)

        val socialBuilder = getAboutPageSocialBuilder()
        val contactBuilder = getAboutPageContactBuilder(email, phoneNumber)

        loadLinks(socialBuilder, layout.socialGrid)
        loadLinks(contactBuilder, layout.contactGrid)

        return layout.root
    }

    private fun getAboutPageSocialBuilder(): AboutPageBuilder {
       return AboutPageBuilder.with(requireContext())
            .addFacebookLink(R.string.bsh_social_handle)
            .addInstagramLink(R.string.bsh_social_handle)
            .addTwitterLink(R.string.bsh_social_handle)
            .setLinksAnimated(true)
    }


    private fun getAboutPageContactBuilder(email:String, phoneNumber:String): AboutPageBuilder {
        return AboutPageBuilder.with(requireContext())
            .addPhoneLink(phoneNumber)
            .addEmailLink(email)
            .addWebsiteLink(R.string.bsh_website)
            .setLinksAnimated(true)
    }

    private fun loadLinks(pageBuilder: AboutPageBuilder, gridView: AutoFitGridLayout) {
        for (item in pageBuilder.getLinks()) {
            val v: View = addLinkItems(gridView, item)
            if (pageBuilder.isLinksAnimated()) {
                AnimUtil(requireActivity()).animate(v,aboutPageLinkItemsAnimDuration, R.anim.expand_in)
            }
        }
    }


    private fun addLinkItems(holder: ViewGroup, item: Item): View{
        val view = layoutInflater.inflate(com.bookshelfhub.feature.home.R.layout.xab_each_link, null)
        view.id = item.getId()
        val tvLabel = view.findViewById<TextView>(com.bookshelfhub.feature.home.R.id.label)
        val ivIcon: AppCompatImageView = view.findViewById(com.bookshelfhub.feature.home.R.id.icon)
        ivIcon.setImageBitmap(item.getIcon())
        tvLabel.text = item.getLabel()
        view.setOnClickListener(item.getOnClick())
        holder.addView(view)
        return view
    }

    override fun onDestroyView() {
        binding=null
        super.onDestroyView()
    }

}