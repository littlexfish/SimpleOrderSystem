package edu.nptu.dllab.sos.data.pull

import edu.nptu.dllab.sos.data.Event
import edu.nptu.dllab.sos.data.EventPuller
import edu.nptu.dllab.sos.util.Util
import edu.nptu.dllab.sos.util.Util.ErrorKey
import edu.nptu.dllab.sos.util.Util.asDouble
import edu.nptu.dllab.sos.util.Util.asInt
import edu.nptu.dllab.sos.util.Util.asString
import edu.nptu.dllab.sos.util.Util.toStringValue
import org.msgpack.value.Value
import org.msgpack.value.ValueFactory

/**
 * The event when got error
 *
 * @author Little Fish
 */
class Error : Event(EVENT_KEY), EventPuller {
	
	/**
	 * Error reason string
	 */
	var reason: String = ""
	
	/**
	 * The format parameter to format strings
	 */
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
		return "error { reason=$reason\"${if(format.isEmpty()) "" else ", " +
				"format=${format.map { it.toString() }.toTypedArray().contentToString()}"} } "
	}
	
	companion object {
		const val EVENT_KEY = "error"
		
		const val ERR_SHOP_NOT_FOUND = "shopNotFound"
		const val ERR_NO_RESOURCE = "noResource"
		
		/**
		 * return `true` when error reason is equals
		 */
		fun isErrorEquals(err: Error, reason: String): Boolean {
			return err.reason.uppercase() == reason.uppercase()
		}
	}
	
}