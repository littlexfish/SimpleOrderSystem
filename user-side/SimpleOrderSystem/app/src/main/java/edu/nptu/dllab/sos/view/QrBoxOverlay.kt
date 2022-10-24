package edu.nptu.dllab.sos.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import edu.nptu.dllab.sos.util.SOSVersion

private val QR_OVERLAY_COLOR = Color.RED//Color.parseColor("#FF0000")
private val QR_STRING_OVERLAY_COLOR = Color.WHITE//Color.parseColor("#FF0000")
private val QR_STRING_S_OVERLAY_COLOR = Color.BLACK//Color.parseColor("#FF0000")
private const val STROKE_SIZE = 5f
private val QR_PAINT = Paint().also { it.strokeWidth = STROKE_SIZE;it.style = Paint.Style.STROKE;it.color = QR_OVERLAY_COLOR }
private val QR_STRING_PAINT = Paint().also { it.textSize = 50f;it.style = Paint.Style.FILL;it.color = QR_STRING_OVERLAY_COLOR }
private val QR_STRING_S_PAINT = Paint().also { it.textSize = 50f;it.style = Paint.Style.STROKE;it.strokeWidth = 5f;it.color = QR_STRING_S_OVERLAY_COLOR }

/**
 * The overlay use for show box on qrcode been scan
 */
@SOSVersion(since = "0.0")
class QrBoxOverlay : View {
	
	/**
	 * The box position
	 */
	@SOSVersion(since = "0.0")
	private var boxRect = RectF(0f, 0f, 0f, 0f)
	
	/**
	 * The text been scan
	 */
	@SOSVersion(since = "0.0")
	private var text: String? = null
	
	/**
	 * The position of text
	 */
	@SOSVersion(since = "0.0")
	private var position = PointF()
	
	constructor(context: Context?) : super(context)
	constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
	constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) :
			super(context, attrs, defStyleAttr, defStyleRes)
	
	override fun onDraw(canvas: Canvas?) {
		super.onDraw(canvas)
		canvas?.drawARGB(0, 0, 0, 0)
		canvas?.drawRect(boxRect, QR_PAINT)
		if(text != null) {
			canvas?.drawText(text!!, position.x, position.y, QR_STRING_S_PAINT)
			canvas?.drawText(text!!, position.x, position.y, QR_STRING_PAINT)
		}
	}
	
	fun setBox(rect: RectF) {
		boxRect = rect
	}
	
	fun setText(t: String?) {
		text = t
	}
	
	fun setPos(p: PointF = PointF()) {
		position = p
	}
	
}