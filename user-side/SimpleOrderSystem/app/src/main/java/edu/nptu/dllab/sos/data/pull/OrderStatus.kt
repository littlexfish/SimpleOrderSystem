package edu.nptu.dllab.sos.data.pull

import edu.nptu.dllab.sos.data.Event
import edu.nptu.dllab.sos.data.EventPuller
import edu.nptu.dllab.sos.util.Util
import edu.nptu.dllab.sos.util.Util.OrderStatusKey
import edu.nptu.dllab.sos.util.Util.asInt
import edu.nptu.dllab.sos.util.Util.asString
import edu.nptu.dllab.sos.util.Util.toStringValue
import org.msgpack.value.Value

class OrderStatus : Event(EVENT_KEY), EventPuller {
	
	var orderId: Int = -1
	var status: String = ""
	var reason: String = ""
	
	override fun fromValue(value: Value) {
		val map = Util.checkMapValue(value).map()
		orderId = map[OrderStatusKey.ORDER_ID.toStringValue()]!!.asInt()
		status = map[OrderStatusKey.STATUS.toStringValue()]!!.asString()
		reason = map[OrderStatusKey.REASON.toStringValue()]?.asString() ?: ""
	}
	
	companion object {
		const val EVENT_KEY = "trace"
	}
	
}