package edu.nptu.dllab.sos.util

import android.graphics.Bitmap
import android.graphics.Color
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import org.msgpack.value.MapValue
import org.msgpack.value.Value
import org.msgpack.value.ValueFactory
import java.nio.ByteBuffer

/**
 * The class store some of function
 *
 * @author Little Fish
 * @since 22/10/03
 */
@SOSVersion(since = "0.0")
object Util {
	
	/**
	 * The transfer protocol of event key
	 */
	@SOSVersion(since = "0.0")
	const val NET_KEY_EVENT = "event"
	
	/**
	 * This app uses scheme
	 */
	@SOSVersion(since = "0.0")
	const val PREFIX_SOS_URL = "sos://"
	
	/**
	 * The activity request code that is ok
	 */
	@SOSVersion(since = "0.0")
	const val REQUEST_OK = 0
	
	/**
	 * The activity request code that is back
	 */
	@SOSVersion(since = "0.0")
	const val REQUEST_BACK = -1
	
	/**
	 * The activity request code that is error
	 */
	@SOSVersion(since = "0.0")
	const val REQUEST_ERROR = 1
	
	/**
	 * The activity request code that is error on extra
	 */
	@SOSVersion(since = "0.0")
	const val REQUEST_ERROR_EXTRA = 2
	
	/**
	 * The activity request code that is error on permission
	 */
	@SOSVersion(since = "0.0")
	const val REQUEST_ERROR_PERMISSION = 3
	
	/**
	 * The global charset
	 */
	@SOSVersion(since = "0.0")
	val CHARSET_GLOBAL = Charsets.UTF_8
	
	/**
	 * Get custom string from sos url
	 */
	@SOSVersion(since = "0.0")
	fun getSOSTypeUrlString(url: String): String {
		return url
	}
	
	/**
	 * Gen a qrcode
	 */
	@SOSVersion(since = "0.0")
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
	@SOSVersion(since = "0.0")
	fun checkMapValue(value: Value): MapValue {
		if(!value.isMapValue) throw Exceptions.DataFormatException("data format not map type")
		return value.asMapValue()
	}
	
	// extension methods
	/**
	 * String change to [org.msgpack.value.StringValue]
	 */
	@SOSVersion(since = "0.0")
	fun String.toStringValue() = ValueFactory.newString(this)!!
	
	/**
	 * int change to [org.msgpack.value.IntegerValue]
	 */
	@SOSVersion(since = "0.0")
	fun Int.toIntegerValue() = ValueFactory.newInteger(this)!!
	
	/**
	 * long change to [org.msgpack.value.IntegerValue]
	 */
	@SOSVersion(since = "0.0")
	fun Long.toIntegerValue() = ValueFactory.newInteger(this)!!
	
	/**
	 * double change to [org.msgpack.value.FloatValue]
	 */
	@SOSVersion(since = "0.0")
	fun Double.toFloatValue() = ValueFactory.newFloat(this)!!
	
	/**
	 * [org.msgpack.value.Value] change to int
	 */
	@SOSVersion(since = "0.0")
	fun Value.asInt() = this.asIntegerValue().toInt()
	
	/**
	 * [org.msgpack.value.Value] change to long
	 */
	@SOSVersion(since = "0.0")
	fun Value.asLong() = this.asIntegerValue().toLong()
	
	/**
	 * [org.msgpack.value.Value] change to [String]
	 */
	@SOSVersion(since = "0.0")
	fun Value.asString() = this.asStringValue().asString()!!
	
	/**
	 * [org.msgpack.value.Value] change to double
	 */
	@SOSVersion(since = "0.0")
	fun Value.asDouble() = this.asFloatValue().toDouble()
	
	/**
	 * [org.msgpack.value.Value] change to [MutableMap]
	 */
	@SOSVersion(since = "0.0")
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
		const val SIZE = "size"
		const val DATA = "data"
		const val POSITION = "position"
		const val ID = "id"
	}
	object OrderKey {
		const val ITEM = "item"
		const val ITEM_SHOP_ID = "shopId"
		const val ITEM_ITEM_ID = "itemId"
		const val ITEM_ADDITION = "addition"
		const val ITEM_ADDITION_ID = "id"
		const val ITEM_ADDITION_TYPE = "type"
		const val ITEM_ADDITION_VALUE = "value"
		const val ITEM_NOTE = "note"
	}
	object OrderRequestKey {
		const val STATUS = "status"
		const val ORDER_ID = "orderId"
	}
	
}