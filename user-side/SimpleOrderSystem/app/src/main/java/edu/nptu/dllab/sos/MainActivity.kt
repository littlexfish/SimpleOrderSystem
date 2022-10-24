package edu.nptu.dllab.sos

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.RectF
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import edu.nptu.dllab.sos.android.DownloadBitmap
import edu.nptu.dllab.sos.data.LinkEvent
import edu.nptu.dllab.sos.data.OpenMenuEvent
import edu.nptu.dllab.sos.databinding.ActivityMainBinding
import edu.nptu.dllab.sos.io.Translator
import edu.nptu.dllab.sos.test.TestActivity
import edu.nptu.dllab.sos.util.Position
import edu.nptu.dllab.sos.util.SOSVersion
import org.msgpack.core.MessagePack
import java.io.IOException
import java.net.Socket

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
		
		binding.button4.setOnClickListener {
			startActivity(Intent(this, TestActivity::class.java))
		}
		
		binding.scanOverlay.setBox(RectF(50f, 80f, 200f, 200f))
		binding.scanOverlay.draw(Canvas(Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888)))
		
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
		if(uri.path != null) {
			val ps = pathSplit(uri.path!!)
			when(ps[0]) {
				"test" -> {
					startActivity(Intent(applicationContext, TestActivity::class.java).setData(intent.data))
				}
				"menu" -> {
					// TODO: open menu
				}
			}
		}
	}
	
	/**
	 * Split the uri path
	 */
	@SOSVersion(since = "0.0")
	private fun pathSplit(path: String) = path.split("/")
	
}