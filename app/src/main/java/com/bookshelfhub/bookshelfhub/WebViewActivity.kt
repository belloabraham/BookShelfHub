package com.bookshelfhub.bookshelfhub

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.bookshelfhub.bookshelfhub.Utils.StringUtil
import com.bookshelfhub.bookshelfhub.databinding.ActivityWebViewBinding
import com.bookshelfhub.bookshelfhub.enums.WebView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class WebViewActivity : AppCompatActivity() {

    private lateinit var layout: ActivityWebViewBinding
    @Inject
    lateinit var stringUtil: StringUtil

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layout = ActivityWebViewBinding.inflate(layoutInflater)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        val title =   stringUtil.capitalize(intent.getStringExtra(WebView.TITLE.KEY)!!)
         val url = intent.getStringExtra(WebView.URL.KEY)
        setContentView(layout.root)
        setSupportActionBar(layout.toolbar)
        supportActionBar?.setTitle(title)


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
            }
        }

        layout.webView.loadUrl(url!!)
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
            layout.webView.goBack();
        }else{
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
       window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        super.onDestroy()
    }

}