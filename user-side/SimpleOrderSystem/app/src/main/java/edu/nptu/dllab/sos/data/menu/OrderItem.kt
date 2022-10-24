package edu.nptu.dllab.sos.data.menu

import androidx.annotation.CallSuper
import edu.nptu.dllab.sos.util.SOSVersion
import org.msgpack.value.MapValue
import org.msgpack.value.ValueFactory
import edu.nptu.dllab.sos.util.Util.OrderKey.*
import edu.nptu.dllab.sos.util.Util.toIntegerValue
import edu.nptu.dllab.sos.util.Util.toStringValue

/**
 * The base class of order item
 */
@SOSVersion(since = "0.0")
abstract class OrderItem(val shopId: Int, val itemId: String) {
	
	/**
	 * Get msgpack of item use to order
	 */
	@SOSVersion(since = "0.0")
	@CallSuper
	open fun toValue(): MapValue {
		val map = ValueFactory.newMapBuilder()
		map.put(ITEM_SHOP_ID.key.toStringValue(), shopId.toIntegerValue())
		map.put(ITEM_ITEM_ID.key.toStringValue(), itemId.toStringValue())
		return map.build()
	}
	
	/**
	 * Get full msgpack of this item
	 */
	@SOSVersion(since = "0.0")
	abstract fun toMapData(): MapValue
	
}