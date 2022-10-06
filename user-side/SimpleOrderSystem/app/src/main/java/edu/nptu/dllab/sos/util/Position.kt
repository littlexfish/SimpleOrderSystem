package edu.nptu.dllab.sos.util

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import edu.nptu.dllab.sos.data.EventPusher
import edu.nptu.dllab.sos.util.Util.toFloatValue
import org.msgpack.value.Value
import org.msgpack.value.ValueFactory

/**
 * The data class that contains position we define
 *
 * @author Little Fish
 * @since 22/10/03
 */
@SOSVersion(since = "0.0")
data class Position(var x: Double = -1.0, var y: Double = -1.0): EventPusher {
	
	override fun toJson(): JsonElement {
		val json = JsonArray()
		json.add(x)
		json.add(y)
		return json
	}
	
	override fun toValue(): Value {
		return ValueFactory.newArray(x.toFloatValue(), y.toFloatValue())
	}
	
	companion object {
		val INVALID_POSITION = Position()
	}
}
