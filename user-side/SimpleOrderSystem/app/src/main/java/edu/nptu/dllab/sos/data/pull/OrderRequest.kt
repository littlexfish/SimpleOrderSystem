package edu.nptu.dllab.sos.data.pull

import edu.nptu.dllab.sos.data.Event
import edu.nptu.dllab.sos.data.EventPuller
import edu.nptu.dllab.sos.util.Util
import edu.nptu.dllab.sos.util.Util.asInt
import edu.nptu.dllab.sos.util.Util.toStringValue
import org.msgpack.value.Value

class OrderRequest : Event(EVENT_KEY), EventPuller {
	
	var orderId = -1 // TODO: check int or string
	
	override fun fromValue(value: Value) {
		val map = Util.checkMapValue(value).map()
		orderId = map[Util.OrderRequestKey.ORDER_ID.toStringValue()]!!.asInt()
	}
	
	companion object {
		const val EVENT_KEY = "order_request"
	}
	
}