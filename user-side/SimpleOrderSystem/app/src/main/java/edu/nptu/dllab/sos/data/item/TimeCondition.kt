package edu.nptu.dllab.sos.data.item

import com.google.gson.JsonObject
import edu.nptu.dllab.sos.util.Util.asLong
import edu.nptu.dllab.sos.util.Util.toIntegerValue
import edu.nptu.dllab.sos.util.Util.toStringValue
import org.msgpack.value.MapValue
import org.msgpack.value.ValueFactory

private const val KEY_START = "start"
private const val KEY_END = "end"

/**
 * The time type condition
 */
class TimeCondition : ItemCondition("time") {
	
	/**
	 * Time start
	 */
	var start = -1L
	
	/**
	 * Time end
	 */
	var end = -1L
	
	override fun checkCondition(value: Any?): Boolean {
		return value is Long && value >= start && value < end
	}
	
	override fun fromValue(data: MapValue) {
		val map = data.map()
		start = map[KEY_START.toStringValue()]!!.asLong()
		end = map[KEY_END.toStringValue()]!!.asLong()
	}
	
	@Deprecated("use fromValue(org.msgpack.value.MapValue)")
	override fun fromJson(data: JsonObject) {
		// now do nothing
	}
	
	override fun toValue(): MapValue {
		val map = ValueFactory.newMapBuilder()
		map.putAll(super.toValue().map())
		map.put(KEY_START.toStringValue(), start.toIntegerValue())
		map.put(KEY_END.toStringValue(), end.toIntegerValue())
		return map.build()
	}
	
}