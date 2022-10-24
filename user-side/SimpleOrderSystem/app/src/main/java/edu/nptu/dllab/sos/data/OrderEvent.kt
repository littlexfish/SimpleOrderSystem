package edu.nptu.dllab.sos.data

import com.google.gson.JsonElement
import com.google.gson.JsonNull
import edu.nptu.dllab.sos.util.SOSVersion
import org.msgpack.value.Value

/**
 * TODO: implement it
 */
@SOSVersion(since = "0.0")
class OrderEvent : EventPusher {
	
	@Deprecated("use toValue(): Value",
	            ReplaceWith("JsonNull.INSTANCE", "com.google.gson.JsonNull"))
	override fun toJson(): JsonElement {
		// now do nothing
		return JsonNull.INSTANCE
	}
	
	override fun toValue(): Value {
		TODO("Not yet implemented")
	}
	
	
}