package androidx.test.gradletests.espresso.device

import android.app.Activity
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.widget.TextView

/** Activity that updates a TextView when its screen orientation is changed. */
class EspressoDeviceActivity : Activity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_espresso_device)

    val textView = findViewById<TextView>(R.id.current_screen_orientation)

    if (
      textView
        .getText()
        .toString()
        .equals(getResources().getString(R.string.screen_orientation_text))
    ) {
      val orientation = setOrientationString(getResources().getConfiguration().orientation)
      Log.d(TAG, "onCreate. Orientation set to " + orientation)
    }
  }

  override fun onConfigurationChanged(newConfig: Configuration) {
    super.onConfigurationChanged(newConfig)
    val orientation = setOrientationString(newConfig.orientation)
    Log.d(TAG, "onConfigurationChanged. Orientation set to " + orientation)
  }

  private fun setOrientationString(orientation: Int): String {
    val orientationString =
      if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
        "landscape"
      } else {
        "portrait"
      }

    findViewById<TextView>(R.id.current_screen_orientation).setText(orientationString)
    return orientationString
  }

  companion object {
    private const val TAG = "EspressoDeviceActivity"
  }
}
