package androidx.test.gradletests.espresso.idling_resource

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.test.espresso.IdlingResource

/** Gets a text String from the user and displays it back after a while. */
class EspressoIdlingResourceActivity :
  Activity(), View.OnClickListener, MessageDelayer.DelayerCallback {

  private lateinit var textView: TextView
  private lateinit var editText: EditText
  private var idlingResource: SimpleIdlingResource = SimpleIdlingResource()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_espresso_idling_resource)

    // Set the listeners for the buttons.
    findViewById<View>(R.id.changeTextBt).setOnClickListener(this)
    textView = findViewById<TextView>(R.id.textToBeChanged)
    editText = findViewById<EditText>(R.id.editTextUserInput)
  }

  override fun onClick(view: View) {
    // Get the text from the EditText view.
    val text = editText.text.toString()
    if (view.id == R.id.changeTextBt) {
      // Set a temporary text.
      textView.text = "Waiting for message..."
      // Submit the message to the delayer.
      MessageDelayer.processMessage(text, this, idlingResource)
    }
  }

  override fun onDone(text: String?) {
    textView.text = text
  }
}
