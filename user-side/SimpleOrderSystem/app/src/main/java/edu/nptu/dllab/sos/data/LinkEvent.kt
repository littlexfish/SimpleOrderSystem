package edu.nptu.dllab.sos.data

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import edu.nptu.dllab.sos.util.Position
import edu.nptu.dllab.sos.util.SOSVersion
import edu.nptu.dllab.sos.util.Util.NET_KEY_EVENT
import edu.nptu.dllab.sos.util.Util.LinkKey.*
import edu.nptu.dllab.sos.util.Util.toStringValue
import org.msgpack.value.Value
import org.msgpack.value.ValueFactory

/**
 * The event link to server
 *
 * @author Little Fish
 * @since 22/10/03
 */
@SOSVersion(since = "0.0")
class LinkEvent : Event(EVENT_KEY), EventPusher {
	
	/**
	 * The position
	 */
	@SOSVersion(since = "0.0")
	var position = Position()
	
	@Deprecated("use toValue(): Value")
	override fun toJson(): JsonElement {
		val obj = JsonObject()
		obj.addProperty(NET_KEY_EVENT, EVENT_KEY)
		obj.add(POSITION.key, position.toJson())
		return obj
	}
	
	override fun toValue(): Value {
		val map = HashMap<Value, Value>()
		map[NET_KEY_EVENT.toStringValue()] = EVENT_KEY.toStringValue()
		map[POSITION.key.toStringValue()] = position.toValue()
		return ValueFactory.newMap(map)
	}
	
	override fun toString(): String {
		return toValue().toString()
	}
	
	companion object {
		const val EVENT_KEY = "link"
	}
	
}