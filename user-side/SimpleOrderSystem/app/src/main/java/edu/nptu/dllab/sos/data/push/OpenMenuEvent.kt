package edu.nptu.dllab.sos.data.push

import edu.nptu.dllab.sos.data.Event
import edu.nptu.dllab.sos.data.EventPusher
import edu.nptu.dllab.sos.util.Util.NET_KEY_EVENT
import edu.nptu.dllab.sos.util.Util.OpenMenuKey
import edu.nptu.dllab.sos.util.Util.toIntegerValue
import edu.nptu.dllab.sos.util.Util.toStringValue
import org.msgpack.value.Value
import org.msgpack.value.ValueFactory

/**
 * The open menu event
 *
 * @author Little Fish
 */
class OpenMenuEvent : Event(EVENT_KEY), EventPusher {
	
	/**
	 * The shop id
	 */
	var shopId: Int = -1
	
	/**
	 * The menu version
	 */
	var menuVersion: Int = -1
	
	override fun toValue(): Value {
		val map = HashMap<Value, Value>()
		map[NET_KEY_EVENT.toStringValue()] = EVENT_KEY.toStringValue()
		map[OpenMenuKey.SHOP_ID.toStringValue()] = shopId.toIntegerValue()
		map[OpenMenuKey.MENU_VERSION.toStringValue()] = menuVersion.toIntegerValue()
		return ValueFactory.newMap(map)
	}
	
	override fun toString(): String {
		return "open_menu { shopId=$shopId, version=$menuVersion }"
	}
	
	companion object {
		const val EVENT_KEY = "open_menu"
	}
}