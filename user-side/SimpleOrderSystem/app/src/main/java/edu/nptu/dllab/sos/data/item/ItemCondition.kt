package edu.nptu.dllab.sos.data.item

import androidx.annotation.CallSuper
import com.google.gson.JsonObject
import edu.nptu.dllab.sos.util.Util
import edu.nptu.dllab.sos.util.Util.toStringValue
import org.msgpack.value.MapValue
import org.msgpack.value.ValueFactory

abstract class ItemCondition(val type: String) {
	
	abstract fun checkCondition(value: Any?): Boolean
	
	abstract fun fromValue(data: MapValue)
	
	abstract fun fromJson(data: JsonObject)
	
	@CallSuper
	open fun toValue(): MapValue {
		val map = ValueFactory.newMapBuilder()
		map.put(Util.EventMenuKey.MENU_CONDITION_TYPE.key.toStringValue(), type.toStringValue())
		return map.build()
	}
	
	companion object {
		fun getCondition(type: String): ItemCondition {
			return when(type) {
				"time" -> TimeCondition()
				else -> TimeCondition()
			}
		}
	}
	
}