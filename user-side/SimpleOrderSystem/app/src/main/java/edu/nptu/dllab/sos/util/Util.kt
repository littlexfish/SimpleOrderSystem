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
	 * Check the json element is json object
	 */
	@SOSVersion(since = "0.0")
	fun checkJsonObject(json: JsonElement): JsonObject {
		if(!json.isJsonObject) throw Exceptions.DataFormatException("data format not object type")
		return json.asJsonObject
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
	enum class LinkKey(val key: String) {
		POSITION("position")
	}
	enum class NearShopKey(val key: String) {
		SHOP("shop"), SHOP_SHOP_ID("shopId"),
		SHOP_POSITION("position"), SHOP_STATE("state"),
		SHOP_TAGS("tags")
	}
	enum class OpenMenuKey(val key: String) {
		SHOP_ID("shopId"), MENU_VERSION("menuVersion")
	}
	enum class UpdateKey(val key: String) {
		SHOP_ID("shopId"), MENU_VERSION("menuVersion"),
		MENU("menu"), MENU_TYPE("type"),
		RESOURCE("resource"), RES_PATH("path"),
		RES_ID("id"), RES_POS("position"),
		RES_SHA256("sha256")
	}
	enum class EventMenuKey(val key: String) {
		MENU("menu"), MENU_CATE("category"), MENU_TYPE("type"),
		MENU_POPUP("popup"), MENU_ITEM("itemId"), MENU_ID("id"),
		MENU_CONDITION("condition"), MENU_CONDITION_TYPE("type"), RESOURCE("resource"),
		RES_PATH("path"), RES_ID("id"), RES_POS("position"),
		RES_SHA256("sha256")
	}
	enum class DownloadKey(val key: String) {
		PATH("path")
	}
	enum class ResourceKey(val key: String) {
		FILE_INDEX("file"), FILE_TOTAL("total"),
		PATH("path"), SHA256("sha256"), SIZE("size"),
		DATA("data"), POSITION("position"), ID("id")
	}
	enum class OrderKey(val key: String) {
		ITEM("item"), ITEM_SHOP_ID("shopId"), ITEM_ITEM_ID("itemId"),
		ITEM_ADDITION("addition"), ITEM_ADDITION_ID("id"), ITEM_ADDITION_TYPE("type"),
		ITEM_ADDITION_VALUE("value"), ITEM_NOTE("note")
	}
	
}