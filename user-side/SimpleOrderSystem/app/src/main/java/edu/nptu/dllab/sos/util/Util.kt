package edu.nptu.dllab.sos.util

import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import edu.nptu.dllab.sos.MainActivity
import edu.nptu.dllab.sos.io.Translator
import org.msgpack.value.MapValue
import org.msgpack.value.Value
import org.msgpack.value.ValueFactory
import java.nio.ByteBuffer
import kotlin.math.*

/**
 * The class store some of function
 *
 * @author Little Fish
 */
object Util {
	
	/**
	 * The transfer protocol of event key
	 */
	const val NET_KEY_EVENT = "event"
	
	/**
	 * This app uses scheme
	 */
	const val PREFIX_SOS_URL = "sos://"
	
	/**
	 * The activity request code that is ok
	 */
	const val REQUEST_OK = 0
	
	/**
	 * The activity request code that is back
	 */
	const val REQUEST_BACK = -1
	
	/**
	 * The activity request code that is error
	 */
	const val REQUEST_ERROR = 1
	
	/**
	 * The activity request code that is error on extra
	 */
	const val REQUEST_ERROR_EXTRA = 2
	
	/**
	 * The activity request code that is error on permission
	 */
	const val REQUEST_ERROR_PERMISSION = 3
	
	/**
	 * The global charset
	 */
	val CHARSET_GLOBAL = Charsets.UTF_8
	
	/**
	 * Get custom string from sos url
	 */
	fun getSOSTypeUrlString(url: String): String {
		if(url.matches(Regex(".+://.+"))) {
			val uri = Uri.parse(url)
			return when(uri.host) {
				MainActivity.HOST_TEST -> Translator.getString("qrcode.string.test")
				MainActivity.HOST_NONE -> Translator.getString("qrcode.string.open")
				MainActivity.HOST_MENU -> {
					val shopIdStr = uri.getQueryParameter("shopId") ?: return url
					if(shopIdStr == "test") return Translator.getString("qrcode.string.test")
					val num = shopIdStr.toIntOrNull()
					if(num != null) {
						Translator.getString("qrcode.string.menu").format(num)
					}
					else {
						url
					}
				}
				else -> url
			}
		}
		return url
	}
	
	/**
	 * Gen a qrcode
	 */
	fun genQrCode(content: String, width: Int, height: Int): Bitmap {
		if(content.isEmpty()) {
			return Bitmap.createBitmap(0, 0, Bitmap.Config.ARGB_8888)
		}
		val colorBlack = Color.BLACK
		val colorWhite = Color.WHITE
		val matrix = QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, width, height)
		val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
		for(y in 0 until height) {
			for(x in 0 until width) {
				bitmap.setPixel(x, y, if(matrix[x, y]) colorBlack else colorWhite)
			}
		}
		return bitmap
	}
	
	/**
	 * Check the value is map value
	 */
	fun checkMapValue(value: Value): MapValue {
		if(!value.isMapValue) throw Exceptions.DataFormatException("data format not map type")
		return value.asMapValue()
	}
	
	/**
	 * Define the radius of earth by meter
	 */
	private const val earthRadius = 6371009.0
	private const val earthLongRadius = 6378136.49
	private const val earthShortRadius = 6356755.00
	
	/**
	 * Calculate distance by two position use Great-Circle Distance function
	 *
	 * @return distance by meter
	 */
	fun getDistance(p1: Position, p2: Position): Double {
		val p1x = Math.toRadians(p1.x)
		val p1y = Math.toRadians(p1.y)
		val p2x = Math.toRadians(p2.x)
		val p2y = Math.toRadians(p2.y)
		val delX = abs(p1x - p2x)
		val delY = abs(p1y - p2y)
		return earthRadius * 2 * asin(sqrt(sin(delX / 2.0).pow(2) + cos(p1x) * cos(p2x) * sin(delY / 2.0).pow(2)))
//		return earthRadius * acos(sin(p1x) * sin(p2x) + cos(p1x) * cos(p2x) * delY)

//		val delX = abs(p1.x - p2.x)
//		val delY = abs(p1.y - p2.y)
//		return earthRadius * 2 * asin(sqrt(sin(delX / 2.0).pow(2) + cos(p1.x) * cos(p2.x) * sin(delY / 2.0).pow(2)))
//		return earthRadius * acos(sin(p1.x) * sin(p2.x) + cos(p1.x) * cos(p2.x) * delY)
//		return getAndoyerDistance(p1, p2)
	}
	
	private fun getAndoyerDistance(p1: Position, p2: Position): Double {
//		val p1x = Math.toRadians(p1.x)
//		val p1y = Math.toRadians(p1.y)
//		val p2x = Math.toRadians(p2.x)
//		val p2y = Math.toRadians(p2.y)
//		val f = (p1x + p2x) / 2
//		val g = (p1x - p2x) / 2
//		val lambda = (p1y - p2y) / 2
		val f = (p1.x + p2.x) / 2
		val g = (p1.x - p2.x) / 2
		val lambda = (p1.y - p2.y) / 2
		
		val s = sin(g).pow(2) * cos(lambda).pow(2) + cos(f).pow(2) * sin(lambda).pow(2)
		val c = cos(g).pow(2) * cos(lambda).pow(2) + sin(f).pow(2) * sin(lambda).pow(2)
		val omega = atan(s / c)
		val r = sqrt(s * c / omega)
		
		val fp = getF()
		val d = 2 * omega * earthLongRadius
		val h1 = (3 * r - 1) / (2 * c)
		val h2 = (3 * r + 1) / (2 * s)
		
		return d * (1 + fp * h1 * sin(f).pow(2) * cos(g).pow(2) - fp * h2 * cos(f).pow(2) * sin(g).pow(2))
	}
	
	private fun getF(): Double {
		val halfShort = earthShortRadius / 2
		val halfLong = earthLongRadius / 2
		val ae = acos(halfShort / halfLong)
		return 1 - cos(ae)
	}
	
	fun calDistUnit(meter: Double, unit: String): Double = when(unit) {
		"m" -> meter
		"ft" -> meter * 0.3048
		else -> meter
	}
	
	// extension methods
	/**
	 * String change to [org.msgpack.value.StringValue]
	 */
	fun String.toStringValue() = ValueFactory.newString(this)!!
	
	/**
	 * int change to [org.msgpack.value.IntegerValue]
	 */
	fun Int.toIntegerValue() = ValueFactory.newInteger(this)!!
	
	/**
	 * long change to [org.msgpack.value.IntegerValue]
	 */
	fun Long.toIntegerValue() = ValueFactory.newInteger(this)!!
	
	/**
	 * double change to [org.msgpack.value.FloatValue]
	 */
	fun Double.toFloatValue() = ValueFactory.newFloat(this)!!
	
	/**
	 * [org.msgpack.value.Value] change to int
	 */
	fun Value.asInt() = this.asIntegerValue().toInt()
	
	/**
	 * [org.msgpack.value.Value] change to long
	 */
	fun Value.asLong() = this.asIntegerValue().toLong()
	
	/**
	 * [org.msgpack.value.Value] change to [String]
	 */
	fun Value.asString() = this.asStringValue().asString()!!
	
	/**
	 * [org.msgpack.value.Value] change to double
	 */
	fun Value.asDouble() = this.asFloatValue().toDouble()
	
	/**
	 * [org.msgpack.value.Value] change to [MutableMap]
	 */
	fun Value.asMap(): MutableMap<Value, Value> = this.asMapValue().map()!!
	
	fun Int.toByteArray(): ByteArray {
		val buffer = ByteBuffer.allocate(4)
		buffer.putInt(this)
		return buffer.array()
	}
	
	// keys
	object LinkKey {
		const val POSITION = "position"
	}
	object NearShopKey {
		const val SHOP = "shop"
		const val SHOP_SHOP_ID = "shopId"
		const val SHOP_POSITION = "position"
		const val SHOP_STATE = "state"
		const val SHOP_TAGS = "tags"
		const val SHOP_NAME = "name"
	}
	object OpenMenuKey {
		const val SHOP_ID = "shopId"
		const val MENU_VERSION = "menuVersion"
	}
	object UpdateKey {
		const val SHOP_ID = "shopId"
		const val MENU_VERSION = "menuVersion"
		const val MENU = "menu"
		const val MENU_TYPE = "type"
		const val RESOURCE = "resource"
		const val RES_PATH = "path"
		const val RES_ID = "id"
		const val RES_POS = "position"
		const val RES_SHA256 = "sha256"
	}
	object EventMenuKey {
		const val MENU = "menu"
		const val MENU_CATE = "category"
		const val MENU_TYPE = "type"
		const val MENU_POPUP = "popup"
		const val MENU_ITEM = "itemId"
		const val MENU_ID = "id"
		const val MENU_CONDITION = "condition"
		const val MENU_CONDITION_TYPE = "type"
		const val RESOURCE = "resource"
		const val RES_PATH = "path"
		const val RES_ID = "id"
		const val RES_POS = "position"
		const val RES_SHA256 = "sha256"
	}
	object DownloadKey {
		const val PATH = "path"
	}
	object ResourceKey {
		const val FILE_INDEX = "file"
		const val FILE_TOTAL = "total"
		const val PATH = "path"
		const val SHA256 = "sha256"
		const val DATA = "data"
		const val POSITION = "position"
		const val ID = "id"
	}
	object OrderKey {
		const val ITEM = "item"
		const val ITEM_SHOP_ID = "shopId"
		const val ITEM_ITEM_ID = "itemId"
		const val ITEM_ADDITION_ID = "id"
		const val ITEM_ADDITION_TYPE = "type"
		const val ITEM_ADDITION_VALUE = "value"
	}
	object OrderRequestKey {
		const val ORDER_ID = "orderId"
	}
	object TraceKey {
		const val ORDER_ID = "orderId"
	}
	object OrderStatusKey {
		const val ORDER_ID = "orderId"
		const val STATUS = "status"
		const val REASON = "reason"
	}
	object ErrorKey {
		const val REASON = "reason"
		const val FORMAT = "format"
	}
	object OrderReceiveKey {
		const val ORDER_ID = "orderId"
		const val ITEM = "item"
	}
	object UpdateStatusKey {
		const val ORDER_ID = "orderId"
		const val STATUS = "status"
	}
	
}