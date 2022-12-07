package edu.nptu.dllab.sos.data.pull

import edu.nptu.dllab.sos.data.Event
import edu.nptu.dllab.sos.data.EventPuller
import edu.nptu.dllab.sos.util.Util
import edu.nptu.dllab.sos.util.Util.asInt
import edu.nptu.dllab.sos.util.Util.toStringValue
import org.msgpack.value.Value

/**
 * The event to get order id
 *
 * @author Little Fish
 */
class OrderRequest : Event(EVENT_KEY), EventPuller {
	
	/**
	 * The order id
	 */
	var orderId = -1
	
	override fun fromValue(value: Value) {
		val map = Util.checkMapValue(value).map()
		orderId = map[Util.OrderRequestKey.ORDER_ID.toStringValue()]!!.asInt()
	}
	
	override fun toString(): String {
		return "order_request { orderId=$orderId }"
	}
	
	companion object {
		const val EVENT_KEY = "order_request"
	}
	
}