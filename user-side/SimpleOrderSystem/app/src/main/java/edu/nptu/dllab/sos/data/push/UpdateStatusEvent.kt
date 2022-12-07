package edu.nptu.dllab.sos.data.push

import edu.nptu.dllab.sos.data.Event
import edu.nptu.dllab.sos.data.EventPusher
import edu.nptu.dllab.sos.util.Util
import edu.nptu.dllab.sos.util.Util.toIntegerValue
import edu.nptu.dllab.sos.util.Util.toStringValue
import org.msgpack.value.Value
import org.msgpack.value.ValueFactory

/**
 * The event to update the order status
 *
 * @author Little Fish
 */
class UpdateStatusEvent : Event(TraceEvent.EVENT_KEY), EventPusher {
	
	/**
	 * The order id
	 */
	var orderId = -1
	
	/**
	 * The status
	 */
	var status = ""
	
	override fun toValue(): Value {
		val map = ValueFactory.newMapBuilder()
		map.put(Util.NET_KEY_EVENT.toStringValue(), EVENT_KEY.toStringValue())
		map.put(Util.UpdateStatusKey.ORDER_ID.toStringValue(), orderId.toIntegerValue())
		map.put(Util.UpdateStatusKey.STATUS.toStringValue(), status.toStringValue())
		return map.build()
	}
	
	override fun toString(): String {
		return "update_status { orderId=$orderId, status=$status }"
	}

	companion object {
		const val EVENT_KEY = "update_status"
	}
	
}