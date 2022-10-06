package edu.nptu.dllab.sos.util

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import org.msgpack.value.MapValue
import org.msgpack.value.Value
import org.msgpack.value.ValueFactory

/**
 * The class store some of function
 *
 * @author Little Fish
 * @since 22/10/03
 */
@SOSVersion(since = "0.0")
object Util {
	
	const val NET_KEY_EVENT = "event"
	
	const val REQUEST_OK = 0
	const val REQUEST_ERROR = 1
	const val REQUEST_ERROR_EXTRA = 2
	
	
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
	 * double change to [org.msgpack.value.FloatValue]
	 */
	@SOSVersion(since = "0.0")
	fun Double.toFloatValue() = ValueFactory.newFloat(this)!!
	
	/**
	 * [org.msgpack.value.Value] change to int
	 */
	@SOSVersion(since = "0.0")
	fun Value.asInt() = this.asIntegerValue().asInt()
	
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
	
	
	// keys
	enum class LinkKey(val key: String) {
		POSITION("position")
	}
	enum class NearShopKey(val key: String) {
		SHOP("shop"), SHOP_SHOP_ID("shopId"),
		SHOP_POSITION("position"), STATE("state")
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
		MENU_FILTER("filter"), RESOURCE("resource"),
		RES_PATH("path"), RES_ID("id"), RES_POS("position"),
		RES_SHA256("sha256")
	}
	enum class DownloadKey(val key: String) {
		PATH("path")
	}
	enum class ResourceKey(val key: String) {
		FILE_INDEX("file"), FILE_TOTAL("total"),
		PATH("path"), SHA256("sha256"), SIZE("size")
	}
	
}