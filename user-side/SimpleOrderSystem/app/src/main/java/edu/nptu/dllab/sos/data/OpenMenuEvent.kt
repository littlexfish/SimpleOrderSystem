package edu.nptu.dllab.sos.data

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import edu.nptu.dllab.sos.util.SOSVersion
import edu.nptu.dllab.sos.util.Util.OpenMenuKey.*
import edu.nptu.dllab.sos.util.Util.NET_KEY_EVENT
import edu.nptu.dllab.sos.util.Util.toIntegerValue
import edu.nptu.dllab.sos.util.Util.toStringValue
import org.msgpack.value.Value
import org.msgpack.value.ValueFactory

/**
 * The open menu event
 */
@SOSVersion(since = "0.0")
class OpenMenuEvent : Event(EVENT_KEY), EventPusher {
	
	/**
	 * The shop id
	 */
	@SOSVersion(since = "0.0")
	var shopId: Int = -1
	
	/**
	 * The menu version
	 */
	@SOSVersion(since = "0.0")
	var menuVersion: Int = -1
	
	@Deprecated("use toValue(): Value")
	override fun toJson(): JsonElement {
		val json = JsonObject()
		json.addProperty(NET_KEY_EVENT, EVENT_KEY)
		json.addProperty(SHOP_ID.key, shopId)
		json.addProperty(MENU_VERSION.key, menuVersion)
		return json
	}
	
	override fun toValue(): Value {
		val map = HashMap<Value, Value>()
		map[NET_KEY_EVENT.toStringValue()] = EVENT_KEY.toStringValue()
		map[SHOP_ID.key.toStringValue()] = shopId.toIntegerValue()
		map[MENU_VERSION.key.toStringValue()] = menuVersion.toIntegerValue()
		return ValueFactory.newMap(map)
	}
	
	override fun toString(): String {
		return toValue().toString()
	}
	
	companion object {
		const val EVENT_KEY = "open_menu"
	}
}