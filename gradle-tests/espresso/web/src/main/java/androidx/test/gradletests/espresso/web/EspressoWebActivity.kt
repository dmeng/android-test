package androidx.test.gradletests.espresso.web

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.webkit.WebView
import android.webkit.WebViewClient

class EspressoWebActivity : Activity() {
  private lateinit var webView: WebView

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_espresso_web)
    webView = findViewById<WebView>(R.id.web_view)
    webView.getSettings().setJavaScriptEnabled(true)
    webView.loadUrl(urlFromIntent(getIntent()))
    webView.requestFocus()
    webView.setWebViewClient(
      object: WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
          return false
        }
      }
    )
  }

  companion object {
    const val KEY_URL_TO_LOAD = "KEY_URL_TO_LOAD"
    const val WEB_FORM_URL = "file:///android_asset/web_form.html"

    fun urlFromIntent(intent: Intent?): String {
      requireNotNull(intent) { "Intent cannot be null!" }
      val url = intent.getStringExtra(KEY_URL_TO_LOAD)
      return if (TextUtils.isEmpty(url)) {
        WEB_FORM_URL
      } else {
        url!!
      }
    }
  }
}
