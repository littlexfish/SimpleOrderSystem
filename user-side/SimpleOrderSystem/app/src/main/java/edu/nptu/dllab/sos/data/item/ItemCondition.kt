package edu.nptu.dllab.sos.data.item

import androidx.annotation.CallSuper
import com.google.gson.JsonObject
import edu.nptu.dllab.sos.util.Util
import edu.nptu.dllab.sos.util.Util.toStringValue
import org.msgpack.value.MapValue
import org.msgpack.value.ValueFactory

/**
 * The condition of event item
 */
abstract class ItemCondition(val type: String) {
	
	/**
	 * Check the condition is pass
	 */
	abstract fun checkCondition(value: Any?): Boolean
	
	/**
	 * Build condition from msgpack
	 */
	abstract fun fromValue(data: MapValue)
	
	/**
	 * Build condition from json
	 */
	@Deprecated("use fromValue(org.msgpack.value.MapValue)")
	abstract fun fromJson(data: JsonObject)
	
	/**
	 * Build condition to msgpack
	 */
	@CallSuper
	open fun toValue(): MapValue {
		val map = ValueFactory.newMapBuilder()
		map.put(Util.EventMenuKey.MENU_CONDITION_TYPE.toStringValue(), type.toStringValue())
		return map.build()
	}
	
	companion object {
		/**
		 * Get condition by condition type
		 */
		fun getCondition(type: String): ItemCondition {
			// TODO: not complete
			return when(type) {
				"time" -> TimeCondition()
				else -> TimeCondition()
			}
		}
	}
	
}