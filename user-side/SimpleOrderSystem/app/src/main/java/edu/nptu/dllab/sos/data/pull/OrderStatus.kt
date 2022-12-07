package edu.nptu.dllab.sos.data.pull

import edu.nptu.dllab.sos.data.Event
import edu.nptu.dllab.sos.data.EventPuller
import edu.nptu.dllab.sos.util.Util
import edu.nptu.dllab.sos.util.Util.OrderStatusKey
import edu.nptu.dllab.sos.util.Util.asInt
import edu.nptu.dllab.sos.util.Util.asString
import edu.nptu.dllab.sos.util.Util.toStringValue
import org.msgpack.value.Value

/**
 * The event to get order status
 *
 * @author Little Fish
 */
class OrderStatus : Event(EVENT_KEY), EventPuller {
	
	/**
	 * The order id
	 */
	var orderId: Int = -1
	
	/**
	 * The order status
	 */
	var status: String = ""
	
	/**
	 * The status reason
	 */
	var reason: String = ""
	
	override fun fromValue(value: Value) {
		val map = Util.checkMapValue(value).map()
		orderId = map[OrderStatusKey.ORDER_ID.toStringValue()]!!.asInt()
		status = map[OrderStatusKey.STATUS.toStringValue()]!!.asString()
		reason = map[OrderStatusKey.REASON.toStringValue()]?.asString() ?: ""
	}
	
	override fun toString(): String {
		return "order_status { orderId=$orderId, status=$status${if(reason.isBlank()) "" else ", reason=$reason"} }"
	}
	
	companion object {
		const val EVENT_KEY = "order_status"
	}
	
}