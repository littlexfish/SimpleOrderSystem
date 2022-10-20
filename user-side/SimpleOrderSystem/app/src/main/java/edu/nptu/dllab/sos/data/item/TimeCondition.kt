package edu.nptu.dllab.sos.data.item

import com.google.gson.JsonObject
import edu.nptu.dllab.sos.util.Util.asLong
import edu.nptu.dllab.sos.util.Util.toIntegerValue
import edu.nptu.dllab.sos.util.Util.toStringValue
import org.msgpack.value.MapValue
import org.msgpack.value.ValueFactory

private const val KEY_START = "start"
private const val KEY_END = "end"

class TimeCondition : ItemCondition("time") {
	
	var start = -1L
	var end = -1L
	
	override fun checkCondition(value: Any?): Boolean {
		return value is Long && value >= start && value < end
	}
	
	override fun fromValue(data: MapValue) {
		val map = data.map()
		start = map[KEY_START.toStringValue()]!!.asLong()
		end = map[KEY_END.toStringValue()]!!.asLong()
	}
	
	override fun fromJson(data: JsonObject) {
		TODO("Not yet implemented")
	}
	
	override fun toValue(): MapValue {
		val map = ValueFactory.newMapBuilder()
		map.putAll(super.toValue().map())
		map.put(KEY_START.toStringValue(), start.toIntegerValue())
		map.put(KEY_END.toStringValue(), end.toIntegerValue())
		return map.build()
	}
	
}