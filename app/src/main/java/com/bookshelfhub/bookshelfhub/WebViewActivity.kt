package com.bookshelfhub.bookshelfhub

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.bookshelfhub.bookshelfhub.Utils.ConnectionUtil
import com.bookshelfhub.bookshelfhub.databinding.ActivityWebViewBinding
import com.bookshelfhub.bookshelfhub.enums.WebView
import com.bookshelfhub.bookshelfhub.extensions.capitalize
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class WebViewActivity : AppCompatActivity() {

    private lateinit var layout: ActivityWebViewBinding
    @Inject
    lateinit var connectionUtil: ConnectionUtil

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layout = ActivityWebViewBinding.inflate(layoutInflater)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

         val title =   intent.getStringExtra(WebView.TITLE.KEY)!!.capitalize()
         val url = intent.getStringExtra(WebView.URL.KEY)
        setContentView(layout.root)

        setSupportActionBar(layout.toolbar)
        supportActionBar?.title = title



        layout.webView.settings.javaScriptEnabled = true
        layout.webView.settings.loadWithOverviewMode = true
        layout.webView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY)

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

        layout.webView.loadUrl(url!!)
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

    override fun onDestroy() {
       window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        super.onDestroy()
    }

}