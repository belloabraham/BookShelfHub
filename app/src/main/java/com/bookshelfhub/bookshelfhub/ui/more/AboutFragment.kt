package com.bookshelfhub.bookshelfhub.ui.more

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.helpers.utils.AnimUtil
import com.bookshelfhub.bookshelfhub.helpers.utils.AppUtil
import com.bookshelfhub.bookshelfhub.helpers.utils.datetime.DateTimeUtil
import com.bookshelfhub.bookshelfhub.helpers.remoteconfig.IRemoteConfig
import com.bookshelfhub.bookshelfhub.databinding.FragmentAboutBinding
import com.bookshelfhub.bookshelfhub.views.about.builder.AboutPageBuilder
import com.bookshelfhub.bookshelfhub.views.about.AutoFitGridLayout
import com.bookshelfhub.bookshelfhub.views.about.Item
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.WithFragmentBindings
import javax.inject.Inject


@AndroidEntryPoint
@WithFragmentBindings
class AboutFragment : Fragment() {


    private val linkItemAnimDuration=200L
    @Inject
    lateinit var appUtil: AppUtil
    @Inject
    lateinit var remoteConfig: IRemoteConfig
    private val EMAIL="email"
    private val PHONE="phone"
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
        val email = remoteConfig.getString(EMAIL)
        val phoneNumber = remoteConfig.getString(PHONE)

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
                AnimUtil(requireActivity()).animate(v,linkItemAnimDuration, R.anim.expand_in)
            }
        }
    }


    private fun addLinkItems(holder: ViewGroup, item: Item): View{
        val view = layoutInflater.inflate(R.layout.xab_each_link, null)
        view.id = item.getId()
        val tvLabel = view.findViewById<TextView>(R.id.label)
        val ivIcon: AppCompatImageView = view.findViewById(R.id.icon)
        ivIcon.setImageBitmap(item.getIcon())
        tvLabel.setText(item.getLabel())
        view.setOnClickListener(item.getOnClick())
        holder.addView(view)
        return view
    }

    override fun onDestroyView() {
        binding=null
        super.onDestroyView()
    }

}