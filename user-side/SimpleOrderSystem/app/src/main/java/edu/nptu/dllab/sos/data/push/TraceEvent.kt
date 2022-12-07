package edu.nptu.dllab.sos.data.push

import edu.nptu.dllab.sos.data.Event
import edu.nptu.dllab.sos.data.EventPusher
import edu.nptu.dllab.sos.util.Util
import edu.nptu.dllab.sos.util.Util.toIntegerValue
import edu.nptu.dllab.sos.util.Util.toStringValue
import org.msgpack.value.Value
import org.msgpack.value.ValueFactory

/**
 * The event to get trace of order
 *
 * @author Little Fish
 */
class TraceEvent : Event(EVENT_KEY), EventPusher {
	
	/**
	 * The order id
	 */
	var orderId = -1
	
	override fun toValue(): Value {
		val map = HashMap<Value, Value>()
		map[Util.NET_KEY_EVENT.toStringValue()] = EVENT_KEY.toStringValue()
		map[Util.TraceKey.ORDER_ID.toStringValue()] = orderId.toIntegerValue()
		return ValueFactory.newMap(map)
	}
	
	override fun toString(): String {
		return "trace { orderId=$orderId } "
	}
	
	companion object {
		const val EVENT_KEY = "trace"
	}
	
}