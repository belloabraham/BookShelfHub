package com.bookshelfhub.bookshelfhub

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.WebViewClient
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import com.bookshelfhub.bookshelfhub.Utils.ConnectionUtil
import com.bookshelfhub.bookshelfhub.databinding.ActivityWebViewBinding
import com.bookshelfhub.bookshelfhub.extensions.capitalize
import com.bookshelfhub.bookshelfhub.lifecycle.EnableWakeLock
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class WebViewActivity : AppCompatActivity(), LifecycleOwner {

    private lateinit var layout: ActivityWebViewBinding
    @Inject
    lateinit var connectionUtil: ConnectionUtil
    private val webViewActivityViewModel: WebViewActivityViewModel by viewModels()

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layout = ActivityWebViewBinding.inflate(layoutInflater)

        EnableWakeLock(this, lifecycle)

         val title =   webViewActivityViewModel.getTitle()!!.capitalize()
         val url = webViewActivityViewModel.getUrl()!!
        setContentView(layout.root)

        setSupportActionBar(layout.toolbar)
        supportActionBar?.title = title


        layout.webView.settings.javaScriptEnabled = true
        layout.webView.settings.loadWithOverviewMode = true
        layout.webView.scrollBarStyle = View.SCROLLBARS_OUTSIDE_OVERLAY

        layout.webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: android.webkit.WebView, url: String?): Boolean {
              url?.let {
                  view.loadUrl(it)
              }
                return true
            }

            override fun onPageFinished(view: android.webkit.WebView?, url: String?) {
                super.onPageFinished(view, url)
                if (layout.progressBar.isShown) {
                    layout.progressBar.visibility = View.GONE
                }
                if(!connectionUtil.isConnected()){
                    Snackbar.make(layout.webView, R.string.no_internet_error_msg, Snackbar.LENGTH_LONG)
                        .show()
                }
            }
        }

        layout.webView.loadUrl(url)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.web_view_activity_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        layout.webView.reload()
        return super.onOptionsItemSelected(item)
    }


    override fun onSupportNavigateUp(): Boolean {
       goBack()
        return true
    }

    override fun onBackPressed() {
       goBack()
    }

    private fun goBack(){
        if (layout.webView.canGoBack()){
            layout.webView.goBack()
        }else{
            super.onBackPressed()
        }
    }

}