package edu.nptu.dllab.sos

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.SurfaceHolder
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import edu.nptu.dllab.sos.databinding.ActivityQrCodeScannerBinding
import edu.nptu.dllab.sos.io.Translator
import edu.nptu.dllab.sos.util.SOSVersion
import edu.nptu.dllab.sos.util.Util

/**
 * The activity use for scan qrcode
 */
@SOSVersion(since = "0.0")
class QrCodeScanner : AppCompatActivity() {
	
	private val boxNone = RectF(0f, 0f, 0f, 0f)
	private lateinit var binding: ActivityQrCodeScannerBinding
	
	/**
	 * The camera use for scan qrcode
	 */
	@SOSVersion(since = "0.0")
	private lateinit var camera: CameraSource
	
	/**
	 * The detector use for detect qrcode
	 */
	@SOSVersion(since = "0.0")
	private lateinit var detector: BarcodeDetector
	
	/**
	 * The value been scan
	 */
	@SOSVersion(since = "0.0")
	private var qrScanValue: String? = null
	
	/**
	 * The detect timer
	 */
	@SOSVersion(since = "0.0")
	private val timer = DetectTimer()
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		
		binding = ActivityQrCodeScannerBinding.inflate(layoutInflater)
		setContentView(binding.root)
		
		detector = BarcodeDetector.Builder(this)
			.setBarcodeFormats(Barcode.QR_CODE).build()
		camera = CameraSource.Builder(this, detector)
			.setAutoFocusEnabled(true).build()
		
		val width = resources.displayMetrics.widthPixels
		val height = width * 16 / 9
		
		binding.qrView.layoutParams.width = width
		binding.qrView.layoutParams.height = height
		binding.scanOverlay.layoutParams.width = width
		binding.scanOverlay.layoutParams.height = height
		binding.qrView.holder.setSizeFromLayout()
		
		var mu = false
		var vM = 1f
		var hM = 1f
		
		binding.qrView.holder.addCallback(object : SurfaceHolder.Callback {
			override fun surfaceCreated(holder: SurfaceHolder) {
				if(checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
					requestPermissions(arrayOf(android.Manifest.permission.CAMERA), 50)
				}
				else {
					camera.start(holder)
				}
			}
			override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int,
			                            height: Int) {
			}
			override fun surfaceDestroyed(holder: SurfaceHolder) {
				camera.stop()
			}
		})
		
		detector.setProcessor(object : Detector.Processor<Barcode> {
			override fun release() {
			}
			
			override fun receiveDetections(det: Detector.Detections<Barcode>) {
				val dets = det.detectedItems
				if(dets.size() > 0) {
					val qr = dets.valueAt(0)
					val value = qr.displayValue
					if(value.matches(Regex(".+://.+"))) { // check is url format
						if(!mu) {
							mu = true
							vM = binding.qrView.height.toFloat() / camera.previewSize.width
							hM = binding.qrView.width.toFloat() / camera.previewSize.height
						}
						val box = RectF(qr.boundingBox).also {
							it.top *= vM
							it.bottom *= vM
							it.left *= hM
							it.right *= hM
						}
						setQrScanValue(value)
						binding.scanOverlay.setPos(PointF(box.left, box.top))
						
						binding.scanOverlay.setBox(box)
						timer.start()
					}
				}
				runOnUiThread {
					binding.scanOverlay.invalidate()
				}
			}
		})
		
		binding.qrControlOverlay.setOnLongClickListener {
			if(qrScanValue != null) {
				val intent = Intent().setData(Uri.parse(qrScanValue))
				intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
				startActivity(intent)
			}
			true
		}
		
	}
	
	/**
	 * Process qrcode value
	 */
	@SOSVersion(since = "0.0")
	private fun setQrScanValue(value: String?) {
		if(value == qrScanValue) return
		runOnUiThread {
			qrScanValue = value
			if(value != null) {
				var display = value
				if(value.startsWith(Util.PREFIX_SOS_URL)) {
					display = Util.getSOSTypeUrlString(value)
				}
				binding.scanOverlay.setText(display/* + Translator.getString("qrcode.redirect")*/)
				binding.qrText.text = display
			}
			else {
				binding.scanOverlay.setText(null)
				binding.qrText.text = ""
			}
		}
	}
	
	override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
	                                        grantResults: IntArray) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults)
		if(checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
			if(shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA)) {
				requestPermissions(arrayOf(android.Manifest.permission.CAMERA), 50)
			}
			else {
				AlertDialog.Builder(this)
					.setMessage(Translator.getString("qrcode.permission"))
					.setPositiveButton(Translator.getString("qrcode.back")) { _, _ ->
						finishActivity(Util.REQUEST_ERROR_PERMISSION)
					}
					.create().show()
			}
		}
		else {
			camera.start(binding.qrView.holder)
		}
	}
	
	override fun onDestroy() {
		super.onDestroy()
		camera.stop()
		camera.release()
	}
	
	/**
	 * The timer use for prevent scan box blinking
	 */
	@SOSVersion(since = "0.0")
	inner class DetectTimer : CountDownTimer(250, 250) {
		override fun onTick(millisUntilFinished: Long) {}
		
		override fun onFinish() {
			setQrScanValue(null)
			binding.scanOverlay.setBox(boxNone)
		}
		
	}
	
}