package com.bookshelfhub.core.ui.views.about.builder

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.view.View
import com.bookshelfhub.core.common.helpers.utils.ColorUtil
import com.bookshelfhub.core.common.helpers.utils.IconUtil
import com.bookshelfhub.core.common.helpers.utils.IntentUtil
import com.bookshelfhub.core.ui.R
import com.bookshelfhub.core.ui.views.about.Item
import java.util.*


class AboutPageBuilder private constructor (private val context: Context){

    private var util = IntentUtil(context)

    private lateinit var name: String
    private lateinit var subTitle: String
    private lateinit var appName: String
    private lateinit var appTitle: String

    private lateinit var cover: Bitmap
    private lateinit var appIcon: Bitmap

    private var subTitleColor = 0
    private var backgroundColor = 0

    private var showDivider = true
    private var dividerColor = 0

    private var linksAnimated = true
    private var linksColumnsCount = 5

    private var showAsCard = true

    private val links: LinkedList<Item> = LinkedList()
    private val actions: LinkedList<Item> = LinkedList()


    companion object{
        fun with(context: Context): AboutPageBuilder = AboutPageBuilder(context)
    }

    fun addPhoneLink(phone: String): AboutPageBuilder {
        return addLink(R.mipmap.phone, R.string.phone, util.openPhoneDialer(phone))
    }

    fun getLastLinkId(): Int {
        return getLastLink().getId()
    }

    private fun getLastLink(): Item {
        return links.getLast()
    }

    fun setName(text: String): AboutPageBuilder {
        name = text
        return this
    }

    fun setName(text: Int): AboutPageBuilder {
        return setName(context.getString(text))
    }

    fun setSubTitle(text: String): AboutPageBuilder {
        subTitle = text
        return this
    }

    fun setSubTitle(text: Int): AboutPageBuilder {
        return setSubTitle(context.getString(text))
    }

    fun setAppName(text: String): AboutPageBuilder {
        appName = text
        return this
    }

    fun setAppName(text: Int): AboutPageBuilder {
        return setAppName(context.getString(text))
    }

    fun setAppTitle(text: String): AboutPageBuilder {
        appTitle = text
        return this
    }

    fun setAppTitle(text: Int): AboutPageBuilder {
        return setAppTitle(context.getString(text))
    }


    fun setCover(cover: Bitmap): AboutPageBuilder {
        this.cover = cover
        return this
    }

    fun setCover(cover: Int): AboutPageBuilder {
        return setCover(IconUtil.getBitmap(context, cover))
    }

    fun setCover(cover: BitmapDrawable): AboutPageBuilder {
        return setCover(IconUtil.getBitmap(cover))
    }


    fun setSubTitleColor(color: Int): AboutPageBuilder {
        subTitleColor = ColorUtil.get(context, color)
        return this
    }


    fun setDividerColor(color: Int): AboutPageBuilder {
        dividerColor = ColorUtil.get(context, color)
        return this
    }

    fun setBackgroundColor(color: Int): AboutPageBuilder {
        backgroundColor = ColorUtil.get(context, color)
        return this
    }


    fun setLinksColumnsCount(count: Int): AboutPageBuilder {
        linksColumnsCount = count
        return this
    }

    fun setLinksAnimated(animate: Boolean): AboutPageBuilder {
        linksAnimated = animate
        return this
    }


    fun setShowDivider(showDivider: Boolean): AboutPageBuilder {
        this.showDivider = showDivider
        return this
    }


    private fun addLink(
        icon: Bitmap,
        label: String,
        onClickListener: View.OnClickListener
    ): AboutPageBuilder {
        links.add(Item(icon, label, onClickListener))
        return this
    }

    private fun addLink(icon: Int, label: Int, onClickListener: View.OnClickListener): AboutPageBuilder {
        return addLink(IconUtil.getBitmap(context, icon), context.getString(label), onClickListener)
    }

    private fun addLink(icon: Int, label: Int, intent: Intent): AboutPageBuilder {
        return addLink(icon, label, util.clickIntent(intent))
    }

    private fun addLink(icon: Int, label: Int, uri: Uri): AboutPageBuilder {
        return addLink(icon, label, util.clickUri(uri))
    }

    private fun addLink(icon: Int, label: Int, url: String): AboutPageBuilder {
        return addLink(icon, label, Uri.parse(url))
    }


    fun addLink(
        icon: BitmapDrawable,
        label: String,
        onClickListener: View.OnClickListener
    ): AboutPageBuilder {
        return addLink(IconUtil.getBitmap(icon), label, onClickListener)
    }

    fun addFacebookLink(user: Int): AboutPageBuilder {
        return addFacebookLink(context.getString(user))
    }

    private fun addFacebookLink(user: String): AboutPageBuilder {
        return addLink(R.mipmap.facebook, R.string.facebook, util.openFacebook(user))
    }

    fun addInstagramLink(user: Int): AboutPageBuilder {
        return addInstagramLink(context.getString(user))
    }

    private fun addInstagramLink(user: String): AboutPageBuilder {
        return addLink(R.mipmap.instagram, R.string.instagram, util.openInstagram(user))
    }

    fun addTwitterLink(user: Int): AboutPageBuilder {
        return addTwitterLink(context.getString(user))
    }

    private fun addTwitterLink(user: String): AboutPageBuilder {
        return addLink(R.mipmap.twitter, R.string.twitter, util.openTwitter(user))
    }

    fun addWebsiteLink(url: Int): AboutPageBuilder {
        return addWebsiteLink(context.getString(url))
    }

    private fun addWebsiteLink(url: String): AboutPageBuilder {
        return addLink(R.mipmap.website, R.string.website, url)
    }

    fun addEmailLink(email: String): AboutPageBuilder {
        return addLink(R.mipmap.email, R.string.email, util.sendEmail(email, null, null))
    }

    fun addAction(
        icon: Bitmap,
        label: String,
        onClickListener: View.OnClickListener
    ): AboutPageBuilder {
        actions.add(Item(icon, label, onClickListener))
        return this
    }

    fun addAction(icon: Bitmap, label: String, intent: Intent): AboutPageBuilder {
        return addAction(icon, label, util.clickIntent(intent))
    }

    fun addAction(icon: Bitmap, label: String, uri: Uri): AboutPageBuilder {
        return addAction(icon, label, util.clickUri(uri))
    }

    fun addAction(icon: Bitmap, label: String, url: String): AboutPageBuilder {
        return addAction(icon, label, Uri.parse(url))
    }

    fun addAction(icon: Bitmap, label: Int, onClickListener: View.OnClickListener): AboutPageBuilder {
        return addAction(icon, context.getString(label), onClickListener)
    }

    fun addAction(icon: Bitmap, label: Int, intent: Intent): AboutPageBuilder {
        return addAction(icon, label, util.clickIntent(intent))
    }

    fun addAction(icon: Bitmap, label: Int, uri: Uri): AboutPageBuilder {
        return addAction(icon, label, util.clickUri(uri))
    }

    fun addAction(icon: Bitmap, label: Int, url: String): AboutPageBuilder {
        return addAction(icon, label, Uri.parse(url))
    }

    fun addAction(icon: Int, label: Int, onClickListener: View.OnClickListener): AboutPageBuilder {
        return addAction(
            IconUtil.getBitmap(context, icon),
            context.getString(label),
            onClickListener
        )
    }

    fun addAction(icon: Int, label: Int, intent: Intent): AboutPageBuilder {
        return addAction(icon, label, util.clickIntent(intent))
    }

    fun addAction(icon: Int, label: Int, uri: Uri): AboutPageBuilder {
        return addAction(icon, label, util.clickUri(uri))
    }


    fun addAction(icon: Int, label: Int, url: String): AboutPageBuilder {
        return addAction(icon, label, Uri.parse(url))
    }

    fun addAction(icon: Int, label: String, onClickListener: View.OnClickListener): AboutPageBuilder {
        return addAction(IconUtil.getBitmap(context, icon), label, onClickListener)
    }

    fun addAction(icon: Int, label: String, intent: Intent): AboutPageBuilder {
        return addAction(icon, label, util.clickIntent(intent))
    }

    fun addAction(icon: Int, label: String, uri: Uri): AboutPageBuilder {
        return addAction(icon, label, util.clickUri(uri))
    }

    fun addAction(icon: Int, label: String, url: String): AboutPageBuilder {
        return addAction(icon, label, Uri.parse(url))
    }

    fun addAction(
        icon: BitmapDrawable,
        label: Int,
        onClickListener: View.OnClickListener
    ): AboutPageBuilder {
        return addAction(IconUtil.getBitmap(icon), context.getString(label), onClickListener)
    }

    fun addAction(icon: BitmapDrawable, label: Int, intent: Intent): AboutPageBuilder {
        return addAction(icon, label, util.clickIntent(intent))
    }


    fun addAction(icon: BitmapDrawable, label: Int, uri: Uri): AboutPageBuilder {
        return addAction(icon, label, util.clickUri(uri))
    }

    fun addAction(icon: BitmapDrawable, label: Int, url: String): AboutPageBuilder {
        return addAction(icon, label, Uri.parse(url))
    }


    fun addAction(
        icon: BitmapDrawable,
        label: String,
        onClickListener: View.OnClickListener
    ): AboutPageBuilder {
        return addAction(IconUtil.getBitmap(icon), label, onClickListener)
    }


    fun addAction(icon: BitmapDrawable, label: String, intent: Intent): AboutPageBuilder {
        return addAction(icon, label, util.clickIntent(intent))
    }

    fun addAction(icon: BitmapDrawable, label: String, uri: Uri): AboutPageBuilder {
        return addAction(icon, label, util.clickUri(uri))
    }

    fun addAction(icon: BitmapDrawable, label: String, url: String): AboutPageBuilder {
        return addAction(icon, label, Uri.parse(url))
    }

    fun setShowAsCard(showAsCard: Boolean): AboutPageBuilder {
        this.showAsCard = showAsCard
        return this
    }

    fun isShowAsCard(): Boolean {
        return showAsCard
    }

    fun getSubTitle(): String {
        return subTitle
    }
    fun getAppTitle(): String {
        return appTitle
    }

    fun getAppIcon(): Bitmap {
        return appIcon
    }

    fun getLinksColumnsCount(): Int {
        return linksColumnsCount
    }


    fun isLinksAnimated(): Boolean {
        return linksAnimated
    }


    fun getLinks(): LinkedList<Item> {
        return links
    }
}