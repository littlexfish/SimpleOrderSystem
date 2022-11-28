package edu.nptu.dllab.sos.data.pull

import edu.nptu.dllab.sos.data.Event
import edu.nptu.dllab.sos.data.EventPuller
import edu.nptu.dllab.sos.util.Util
import edu.nptu.dllab.sos.util.Util.ErrorKey
import edu.nptu.dllab.sos.util.Util.asDouble
import edu.nptu.dllab.sos.util.Util.asInt
import edu.nptu.dllab.sos.util.Util.asString
import edu.nptu.dllab.sos.util.Util.toFloatValue
import edu.nptu.dllab.sos.util.Util.toIntegerValue
import edu.nptu.dllab.sos.util.Util.toStringValue
import org.msgpack.value.Value
import org.msgpack.value.ValueFactory

class Error : Event(EVENT_KEY), EventPuller {
	
	var reason: String = ""
	val format = ArrayList<Any?>()
	
	override fun fromValue(value: Value) {
		val map = Util.checkMapValue(value).map()
		reason = map[ErrorKey.REASON.toStringValue()]?.asString() ?: ""
		format.clear()
		for(i in map[ErrorKey.FORMAT.toStringValue()]?.asArrayValue()
			?: ValueFactory.emptyArray()) {
			format.add(when {
	           i.isStringValue -> i.asString()
	           i.isFloatValue -> i.asDouble()
	           i.isIntegerValue -> i.asInt()
	           i.isBooleanValue -> i.asBooleanValue().boolean
	           else -> null
           })
		}
	}
	
	override fun toString(): String {
		val map = ValueFactory.newMapBuilder()
		map.put(Util.NET_KEY_EVENT.toStringValue(), event.toStringValue())
		map.put(ErrorKey.REASON.toStringValue(), reason.toStringValue())
		map.put(ErrorKey.FORMAT.toStringValue(), ValueFactory.newArray(format.map {
			when(it) {
				is String -> it.toStringValue()
				is Double -> it.toFloatValue()
				is Int -> it.toIntegerValue()
				else -> ValueFactory.newNil()
			}
		}))
		return map.build().toJson()
	}
	
	companion object {
		const val EVENT_KEY = "error"
		
		const val ERR_SHOP_NOT_FOUND = "shopNotFound"
		const val ERR_NO_RESOURCE = "noResource"
		
		fun isErrorEquals(err: Error, reason: String): Boolean {
			return err.reason.uppercase() == reason.uppercase()
		}
	}
	
}