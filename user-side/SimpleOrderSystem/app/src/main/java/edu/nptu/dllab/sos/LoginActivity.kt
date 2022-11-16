package edu.nptu.dllab.sos

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_login)
		
		// TODO: now always return [REQUEST_USER]
		setResult(RESULT_USER)
		finish()
		
	}
	
	
	companion object {
		const val RESULT_USER = 500
		const val RESULT_SHOP = 501
		const val RESULT_NONE = 502
		const val RESULT_ERROR = 503
	}
	
}