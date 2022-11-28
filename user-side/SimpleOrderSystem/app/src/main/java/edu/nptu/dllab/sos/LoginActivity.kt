package edu.nptu.dllab.sos

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_login)
		
		onNewData(intent)
		
		// TODO: now always return [REQUEST_USER]
		setResult(RESULT_USER)
		finish()
		
	}
	
	override fun onNewIntent(intent: Intent?) {
		super.onNewIntent(intent)
		intent?.let { onNewData(it) }
	}
	
	private fun onNewData(intent: Intent) {
	
	}
	
	companion object {
		const val RESULT_USER = 500
		const val RESULT_SHOP = 501
		const val RESULT_NONE = 502
		const val RESULT_ERROR = 503
		
		const val EXTRA_ACCOUNT = "account"
		const val EXTRA_PASSWORD = "password"
	}
	
}