package androidx.test.gradletests.espresso.web

import android.content.Intent
import androidx.test.espresso.web.assertion.WebViewAssertions.webMatches
import androidx.test.espresso.web.sugar.Web.onWebView
import androidx.test.espresso.web.webdriver.DriverAtoms
import androidx.test.espresso.web.webdriver.DriverAtoms.clearElement
import androidx.test.espresso.web.webdriver.DriverAtoms.findElement
import androidx.test.espresso.web.webdriver.DriverAtoms.getText
import androidx.test.espresso.web.webdriver.DriverAtoms.webClick
import androidx.test.espresso.web.webdriver.Locator
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.Matchers.containsString
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EspressoWebTest {

  private val activityScenarioRule: ActivityScenarioRule<EspressoWebActivity> =
    ActivityScenarioRule(withWebFormIntent())

  @Test
  fun typeTextInInput_clickButton_SubmitsForm() {
    // Selects the WebView in your layout. If you have multiple WebViews you can also use a
    // matcher to select a given WebView, onWebView(withId(R.id.web_view)).
    onWebView()
      // Find the input element by ID
      .withElement(findElement(Locator.ID, "text_input"))
      // Clear previous input
      .perform(clearElement())
      // Enter text into the input element
      .perform(DriverAtoms.webKeys(MACCHIATO))
      // Find the submit button
      .withElement(findElement(Locator.ID, "submitBtn"))
      // Simulate a click via javascript
      .perform(webClick())
      // Find the response element by ID
      .withElement(findElement(Locator.ID, "response"))
      // Verify that the response page contains the entered text
      .check(webMatches(getText(), containsString(MACCHIATO)))
  }

  private fun withWebFormIntent(): Intent {
    val basicFormIntent = Intent()
    basicFormIntent.putExtra(EspressoWebActivity.KEY_URL_TO_LOAD, EspressoWebActivity.WEB_FORM_URL)
    return basicFormIntent
  }

  companion object {
    private const val MACCHIATO = "Macchiato"
  }
}
