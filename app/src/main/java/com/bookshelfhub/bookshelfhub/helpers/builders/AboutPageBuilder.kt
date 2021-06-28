package com.bookshelfhub.bookshelfhub.helpers.builders

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.view.View
import com.bookshelfhub.bookshelfhub.R
import com.bookshelfhub.bookshelfhub.Utils.ColorUtil
import com.bookshelfhub.bookshelfhub.Utils.IconUtil
import com.bookshelfhub.bookshelfhub.Utils.IntentUtil
import com.bookshelfhub.bookshelfhub.view.about.Item
import java.util.*


class AboutPageBuilder {

    private lateinit var context: Context
    private lateinit var util: IntentUtil

    private lateinit var name: String
    private lateinit var subTitle: String
    private lateinit var brief: String
    private lateinit var appName: String
    private lateinit var appTitle: String

    private lateinit var photo: Bitmap
    private lateinit var cover: Bitmap
    private lateinit var appIcon: Bitmap

    private var nameColor = 0
    private var subTitleColor = 0
    private var briefColor = 0
    private var iconColor = 0
    private var backgroundColor = 0

    private var showDivider = true
    private var dividerColor = 0
    private var dividerHeight = 4
    private var dividerDashWidth = 15
    private var dividerDashGap = 15

    private var linksAnimated = true
    private var linksColumnsCount = 5
    private var actionsColumnsCount = 2

    private var wrapScrollView = false
    private var showAsCard = true

    private val links: LinkedList<Item> = LinkedList()
    private val actions: LinkedList<Item> = LinkedList()

    @Deprecated("Used {@link #with(Context)} instead.")
    constructor( context: Context) {
        this.context = context
        util = IntentUtil(context)
    }

    companion object{
        fun with(context: Context): AboutPageBuilder = AboutPageBuilder(context)
    }

    fun addPhoneLink(name: Int, phone: Int): AboutPageBuilder {
        return addPhoneLink(context.getString(phone))
    }

    fun addPhoneLink(phone: String): AboutPageBuilder {
        return addLink(R.mipmap.phone, R.string.phone, util.openPhoneDialer(phone))
    }


    private fun getApplicationID(): String {
        return context.getPackageName()
    }

    @Throws(PackageManager.NameNotFoundException::class)
    private fun getPackageInfo(): PackageInfo {
        return context.getPackageManager().getPackageInfo(getApplicationID(), 0)
    }

    fun getLastActionId(): Int {
        return getLastAction().getId()
    }

    fun getLastAction(): Item {
        return actions.getLast()
    }

    fun getLastLinkId(): Int {
        return getLastLink().getId()
    }

    fun getLastLink(): Item {
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

    fun setBrief(text: String): AboutPageBuilder {
        brief = text
        return this
    }

    fun setBrief(text: Int): AboutPageBuilder {
        return setBrief(context.getString(text))
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

    fun setPhoto(photo: Bitmap): AboutPageBuilder {
        this.photo = photo
        return this
    }

    fun setPhoto(photo: Int): AboutPageBuilder {
        return setPhoto(IconUtil.getBitmap(context, photo))
    }

    fun setPhoto(photo: BitmapDrawable): AboutPageBuilder {
        return setPhoto(IconUtil.getBitmap(photo))
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

    fun setAppIcon(icon: Bitmap): AboutPageBuilder {
        appIcon = icon
        return this
    }

    fun setAppIcon(icon: Int): AboutPageBuilder {
        return setAppIcon(IconUtil.getBitmap(context, icon))
    }

    fun setAppIcon(icon: BitmapDrawable): AboutPageBuilder {
        return setAppIcon(IconUtil.getBitmap(icon))
    }

    fun setNameColor(color: Int): AboutPageBuilder {
        nameColor = ColorUtil.get(context, color)
        return this
    }

    fun setSubTitleColor(color: Int): AboutPageBuilder {
        subTitleColor = ColorUtil.get(context, color)
        return this
    }

    fun setBriefColor(color: Int): AboutPageBuilder {
        briefColor = ColorUtil.get(context, color)
        return this
    }

    fun setDividerColor(color: Int): AboutPageBuilder {
        dividerColor = ColorUtil.get(context, color)
        return this
    }

    fun setIconColor(color: Int): AboutPageBuilder {
        iconColor = ColorUtil.get(context, color)
        return this
    }

    fun setBackgroundColor(color: Int): AboutPageBuilder {
        backgroundColor = ColorUtil.get(context, color)
        return this
    }

    fun setActionsColumnsCount(count: Int): AboutPageBuilder {
        actionsColumnsCount = count
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

    fun setDividerHeight(dividerHeight: Int): AboutPageBuilder {
        this.dividerHeight = dividerHeight
        return this
    }

    fun setDividerDashWidth(dividerDashWidth: Int): AboutPageBuilder {
        this.dividerDashWidth = dividerDashWidth
        return this
    }

    fun setDividerDashGap(dividerDashGap: Int): AboutPageBuilder {
        this.dividerDashGap = dividerDashGap
        return this
    }

    fun setShowDivider(showDivider: Boolean): AboutPageBuilder {
        this.showDivider = showDivider
        return this
    }

    fun setWrapScrollView(wrapScrollView: Boolean): AboutPageBuilder {
        this.wrapScrollView = wrapScrollView
        return this
    }

    fun addLink(
        icon: Bitmap,
        label: String,
        onClickListener: View.OnClickListener
    ): AboutPageBuilder {
        links.add(Item(icon, label, onClickListener))
        return this
    }

    fun addLink(icon: Bitmap, label: String, intent: Intent): AboutPageBuilder {
        return addLink(icon, label, util.clickIntent(intent))
    }

    fun addLink(icon: Bitmap, label: String, uri: Uri): AboutPageBuilder {
        return addLink(icon, label, util.clickUri(uri))
    }

    fun addLink(icon: Bitmap, label: String, url: String): AboutPageBuilder {
        return addLink(icon, label, Uri.parse(url))
    }

    fun addLink(icon: Bitmap, label: Int, onClickListener: View.OnClickListener): AboutPageBuilder {
        return addLink(icon, context.getString(label), onClickListener)
    }

    fun addLink(icon: Bitmap, label: Int, intent: Intent): AboutPageBuilder {
        return addLink(icon, label, util.clickIntent(intent))
    }

    fun addLink(icon: Bitmap, label: Int, uri: Uri): AboutPageBuilder {
        return addLink(icon, label, util.clickUri(uri))
    }

    fun addLink(icon: Bitmap, label: Int, url: String): AboutPageBuilder {
        return addLink(icon, label, Uri.parse(url))
    }

    fun addLink(icon: Int, label: Int, onClickListener: View.OnClickListener): AboutPageBuilder {
        return addLink(IconUtil.getBitmap(context, icon), context.getString(label), onClickListener)
    }

    fun addLink(icon: Int, label: Int, intent: Intent): AboutPageBuilder {
        return addLink(icon, label, util.clickIntent(intent))
    }

    fun addLink(icon: Int, label: Int, uri: Uri): AboutPageBuilder {
        return addLink(icon, label, util.clickUri(uri))
    }

    fun addLink(icon: Int, label: Int, url: String): AboutPageBuilder {
        return addLink(icon, label, Uri.parse(url))
    }

    fun addLink(icon: Int, label: String, onClickListener: View.OnClickListener): AboutPageBuilder {
        return addLink(IconUtil.getBitmap(context, icon), label, onClickListener)
    }

    fun addLink(icon: Int, label: String, intent: Intent): AboutPageBuilder {
        return addLink(icon, label, util.clickIntent(intent))
    }

    fun addLink(icon: Int, label: String, uri: Uri): AboutPageBuilder {
        return addLink(icon, label, util.clickUri(uri))
    }

    fun addLink(icon: Int, label: String, url: String): AboutPageBuilder {
        return addLink(icon, label, Uri.parse(url))
    }

    fun addLink(
        icon: BitmapDrawable,
        label: Int,
        onClickListener: View.OnClickListener
    ): AboutPageBuilder {
        return addLink(IconUtil.getBitmap(icon), context.getString(label), onClickListener)
    }

    fun addLink(icon: BitmapDrawable, label: Int, intent: Intent): AboutPageBuilder {
        return addLink(icon, label, util.clickIntent(intent))
    }

    fun addLink(icon: BitmapDrawable, label: Int, uri: Uri): AboutPageBuilder {
        return addLink(icon, label, util.clickUri(uri))
    }

    fun addLink(icon: BitmapDrawable, label: Int, url: String): AboutPageBuilder {
        return addLink(icon, label, Uri.parse(url))
    }

    fun addLink(
        icon: BitmapDrawable,
        label: String,
        onClickListener: View.OnClickListener
    ): AboutPageBuilder {
        return addLink(IconUtil.getBitmap(icon), label, onClickListener)
    }

    fun addLink(icon: BitmapDrawable, label: String, intent: Intent): AboutPageBuilder {
        return addLink(icon, label, util.clickIntent(intent))
    }

    fun addLink(icon: BitmapDrawable, label: String, uri: Uri): AboutPageBuilder {
        return addLink(icon, label, util.clickUri(uri))
    }


    fun addLink(icon: BitmapDrawable, label: String, url: String): AboutPageBuilder {
        return addLink(icon, label, Uri.parse(url))
    }

    fun addFacebookLink(user: Int): AboutPageBuilder {
        return addFacebookLink(context.getString(user))
    }

    fun addFacebookLink(user: String): AboutPageBuilder {
        return addLink(R.mipmap.facebook, R.string.facebook, util.openFacebook(user))
    }

    fun addInstagramLink(user: Int): AboutPageBuilder {
        return addInstagramLink(context.getString(user))
    }

    fun addInstagramLink(user: String): AboutPageBuilder {
        return addLink(R.mipmap.instagram, R.string.instagram, util.openInstagram(user))
    }

    fun addTwitterLink(user: Int): AboutPageBuilder {
        return addTwitterLink(context.getString(user))
    }


    fun addTwitterLink(user: String): AboutPageBuilder {
        return addLink(R.mipmap.twitter, R.string.twitter, util.openTwitter(user))
    }

    fun addWebsiteLink(url: Int): AboutPageBuilder {
        return addWebsiteLink(context.getString(url))
    }

    fun addWebsiteLink(url: String): AboutPageBuilder {
        return addLink(R.mipmap.website, R.string.website, url)
    }

    fun addEmailLink(email: Int, subject: Int, message: Int): AboutPageBuilder {
        return addEmailLink(
            context.getString(email),
            context.getString(subject),
            context.getString(message)
        )
    }

    fun addEmailLink(email: Int, subject: String, message: String?): AboutPageBuilder {
        return addEmailLink(context.getString(email), subject, message)
    }

    fun addEmailLink(email: Int, subject: String): AboutPageBuilder {
        return addEmailLink(context.getString(email), subject, null)
    }

    fun addEmailLink(email: Int, subject: Int): AboutPageBuilder {
        return addEmailLink(context.getString(email), context.getString(subject), null)
    }

    fun addEmailLink(email: String, subject: String, message: String?): AboutPageBuilder {
        return addLink(R.mipmap.email, R.string.email, util.sendEmail(email, subject, message))
    }

    fun addEmailLink(email: Int): AboutPageBuilder {
        return addEmailLink(context.getString(email))
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

    fun getName(): String {
        return name
    }

    fun getSubTitle(): String {
        return subTitle
    }

    fun getBrief(): String {
        return brief
    }

    fun getAppName(): String {
        return appName
    }

    fun getAppTitle(): String {
        return appTitle
    }

    fun getPhoto(): Bitmap {
        return photo
    }

    fun getCover(): Bitmap {
        return cover
    }

    fun getAppIcon(): Bitmap {
        return appIcon
    }

    fun getNameColor(): Int {
        return nameColor
    }

    fun getSubTitleColor(): Int {
        return subTitleColor
    }

    fun getBriefColor(): Int {
        return briefColor
    }

    fun getDividerColor(): Int {
        return dividerColor
    }

    fun getIconColor(): Int {
        return iconColor
    }

    fun getBackgroundColor(): Int {
        return backgroundColor
    }

    fun getLinksColumnsCount(): Int {
        return linksColumnsCount
    }

    fun getActionsColumnsCount(): Int {
        return actionsColumnsCount
    }

    fun isShowDivider(): Boolean {
        return showDivider
    }

    fun getDividerHeight(): Int {
        return dividerHeight
    }

    fun getDividerDashWidth(): Int {
        return dividerDashWidth
    }

    fun getDividerDashGap(): Int {
        return dividerDashGap
    }

    fun isLinksAnimated(): Boolean {
        return linksAnimated
    }

    fun isWrapScrollView(): Boolean {
        return wrapScrollView
    }

    fun getLinks(): LinkedList<Item> {
        return links
    }

    fun getActions(): LinkedList<Item> {
        return actions
    }

}