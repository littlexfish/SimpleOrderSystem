package edu.nptu.dllab.sos.data.push

import edu.nptu.dllab.sos.data.Event
import edu.nptu.dllab.sos.data.EventPusher
import edu.nptu.dllab.sos.util.Position
import edu.nptu.dllab.sos.util.Util.LinkKey
import edu.nptu.dllab.sos.util.Util.NET_KEY_EVENT
import edu.nptu.dllab.sos.util.Util.toStringValue
import org.msgpack.value.Value
import org.msgpack.value.ValueFactory

/**
 * The event link to server
 *
 * @author Little Fish
 */
class LinkEvent : Event(EVENT_KEY), EventPusher {
	
	/**
	 * The position
	 */
	var position = Position()
	
	override fun toValue(): Value {
		val map = HashMap<Value, Value>()
		map[NET_KEY_EVENT.toStringValue()] = EVENT_KEY.toStringValue()
		map[LinkKey.POSITION.toStringValue()] = position.toValue()
		return ValueFactory.newMap(map)
	}
	
	override fun toString(): String {
		return "link { position=(${position.x}, ${position.y}) }"
	}
	
	companion object {
		const val EVENT_KEY = "link"
	}
	
}