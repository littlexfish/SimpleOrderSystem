package edu.nptu.dllab.sos.data.item

import androidx.annotation.CallSuper
import com.google.gson.JsonObject
import edu.nptu.dllab.sos.util.SOSVersion
import edu.nptu.dllab.sos.util.Util
import edu.nptu.dllab.sos.util.Util.toStringValue
import org.msgpack.value.MapValue
import org.msgpack.value.ValueFactory

/**
 * The condition of event item
 */
@SOSVersion(since = "0.0")
abstract class ItemCondition(val type: String) {
	
	/**
	 * Check the condition is pass
	 */
	@SOSVersion(since = "0.0")
	abstract fun checkCondition(value: Any?): Boolean
	
	/**
	 * Build condition from msgpack
	 */
	@SOSVersion(since = "0.0")
	abstract fun fromValue(data: MapValue)
	
	/**
	 * Build condition from json
	 */
	@Deprecated("use fromValue(org.msgpack.value.MapValue)")
	@SOSVersion(since = "0.0", until = "0.0")
	abstract fun fromJson(data: JsonObject)
	
	/**
	 * Build condition to msgpack
	 */
	@SOSVersion(since = "0.0")
	@CallSuper
	open fun toValue(): MapValue {
		val map = ValueFactory.newMapBuilder()
		map.put(Util.EventMenuKey.MENU_CONDITION_TYPE.key.toStringValue(), type.toStringValue())
		return map.build()
	}
	
	companion object {
		/**
		 * Get condition by condition type
		 * TODO: not complete
		 */
		@SOSVersion(since = "0.0")
		fun getCondition(type: String): ItemCondition {
			return when(type) {
				"time" -> TimeCondition()
				else -> TimeCondition()
			}
		}
	}
	
}