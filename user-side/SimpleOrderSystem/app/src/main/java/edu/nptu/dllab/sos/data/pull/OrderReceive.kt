package edu.nptu.dllab.sos.data.pull

import edu.nptu.dllab.sos.data.Event
import edu.nptu.dllab.sos.data.EventPuller
import edu.nptu.dllab.sos.data.shop.Addition
import edu.nptu.dllab.sos.data.shop.Item
import edu.nptu.dllab.sos.data.shop.Order
import edu.nptu.dllab.sos.util.Util
import edu.nptu.dllab.sos.util.Util.OrderReceiveKey
import edu.nptu.dllab.sos.util.Util.asInt
import edu.nptu.dllab.sos.util.Util.asMap
import edu.nptu.dllab.sos.util.Util.asString
import edu.nptu.dllab.sos.util.Util.toStringValue
import org.msgpack.value.Value

/**
 * The event to receive order for shop side
 *
 * @author Little Fish
 */
class OrderReceive : Event(EVENT_KEY), EventPuller {
	
	/**
	 * The order for shop side
	 */
	var order: Order = Order(-1, emptyList())
	
	override fun fromValue(value: Value) {
		val map = Util.checkMapValue(value).map()
		val id = map[OrderReceiveKey.ORDER_ID.toStringValue()]!!.asInt()
		val itemListValue = map[OrderReceiveKey.ITEM.toStringValue()]!!.asArrayValue()
		
		val items = ArrayList<Item>()
		for(item in itemListValue) {
			val iMap = item.asMap()
			val itemId = iMap[KEY_ITEM_ID.toStringValue()]!!.asString()
			val addListValue = iMap[KEY_ADDITION.toStringValue()]!!.asArrayValue()
			val note = iMap[KEY_NOTE.toStringValue()]?.asString() ?: ""
			val addList = ArrayList<Addition>()
			for(add in addListValue) {
				addList.add(Addition.fromMapValue(add.asMapValue()))
			}
			val i = Item(itemId, addList, note)
			items.add(i)
		}
		order = Order(id, items)
	}
	
	override fun toString(): String {
		return "order { orderId=${order.id}, itemCount=${order.items.joinToString(", ", "[", "]") { it.itemId } }"
	}
	
	companion object {
		const val EVENT_KEY = "order"
		
		private const val KEY_ITEM_ID = "itemId"
		private const val KEY_ADDITION = "addition"
		private const val KEY_NOTE = "note"
	}
	
}