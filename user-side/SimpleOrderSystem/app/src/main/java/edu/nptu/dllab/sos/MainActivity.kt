package edu.nptu.dllab.sos

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import edu.nptu.dllab.sos.databinding.ActivityMainBinding
import edu.nptu.dllab.sos.io.Translator
import edu.nptu.dllab.sos.test.TestActivity
import edu.nptu.dllab.sos.util.SOSVersion

/**
 * The main activity of this app
 */
@SOSVersion(since = "0.0")
class MainActivity : AppCompatActivity() {
	
	private lateinit var binding: ActivityMainBinding
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		
		binding = ActivityMainBinding.inflate(layoutInflater)
		setContentView(binding.root)
		
		// TODO: change to settings get language
		if(Translator.getLang() == null) Translator.initTranslator(applicationContext, "zh_tw")
		
		if(intent.data != null) {
			parseNewData(intent.data!!)
		}
		
		binding.mainQrScanner.setOnClickListener {
			startActivity(Intent(applicationContext, QrCodeScanner::class.java))
		}
		
		// For test
		binding.button4.setOnClickListener {
			startActivity(Intent(this, TestActivity::class.java))
		}
		
		binding.button5.setOnClickListener {
			startActivity(Intent(this, MenuActivity::class.java).apply { putExtra(MenuActivity.EXTRA_SHOP_ID, -1) })
		}
		
		binding.scanOverlay.setBox(RectF(50f, 80f, 200f, 200f))
		binding.scanOverlay.draw(Canvas(Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888)))
		// Test end
		
	}
	
	override fun onNewIntent(intent: Intent?) {
		super.onNewIntent(intent)
		
		if(intent != null && intent.data != null) {
			parseNewData(intent.data!!)
		}
		
	}
	
	/**
	 * Process the new data from intent
	 */
	@SOSVersion(since = "0.0")
	private fun parseNewData(uri: Uri) {
		/*
		 * FISH NOTE:
		 *   Parse the uri that open from any app.
		 *   We process the uri path to open specific activity,
		 *    that the app want to open
		 */
		when(uri.host) {
			"dllab.test" -> {
				startActivity(Intent(applicationContext, TestActivity::class.java).setData(intent.data))
			}
			"dllab.menu" -> {
				val shopIdStr = uri.getQueryParameter("shopId") ?: return
				val num = if(shopIdStr == "test") -1 else shopIdStr.toIntOrNull()
				if(num != null) {
					val menuActI = Intent(applicationContext, MenuActivity::class.java)
					menuActI.putExtra(MenuActivity.EXTRA_SHOP_ID, num)
					startActivity(menuActI)
				}
			}
		}
	}
	
}