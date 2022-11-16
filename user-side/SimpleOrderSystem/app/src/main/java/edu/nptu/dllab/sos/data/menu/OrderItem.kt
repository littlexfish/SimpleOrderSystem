package edu.nptu.dllab.sos.data.menu

import androidx.annotation.CallSuper
import edu.nptu.dllab.sos.util.SOSVersion
import edu.nptu.dllab.sos.util.Util.OrderKey
import edu.nptu.dllab.sos.util.Util.toIntegerValue
import edu.nptu.dllab.sos.util.Util.toStringValue
import org.msgpack.value.MapValue
import org.msgpack.value.ValueFactory

/**
 * The base class of order item
 */
@SOSVersion(since = "0.0")
abstract class OrderItem(val shopId: Int, val itemId: String, val display: String = "",
                         val currencyCode: String = "NTD", val price: Double = 0.0) {
	
	/**
	 * Get msgpack of item use to order
	 */
	@SOSVersion(since = "0.0")
	@CallSuper
	open fun toValue(): MapValue {
		val map = ValueFactory.newMapBuilder()
		map.put(OrderKey.ITEM_SHOP_ID.toStringValue(), shopId.toIntegerValue())
		map.put(OrderKey.ITEM_ITEM_ID.toStringValue(), itemId.toStringValue())
		return map.build()
	}
	
	/**
	 * Get full msgpack of this item
	 */
	@SOSVersion(since = "0.0")
	abstract fun toMapData(): MapValue
	
}