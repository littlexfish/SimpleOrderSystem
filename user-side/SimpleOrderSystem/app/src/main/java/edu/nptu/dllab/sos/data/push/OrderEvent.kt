package edu.nptu.dllab.sos.data.push

import edu.nptu.dllab.sos.data.Event
import edu.nptu.dllab.sos.data.EventPusher
import edu.nptu.dllab.sos.data.menu.OrderItem
import edu.nptu.dllab.sos.util.Util
import edu.nptu.dllab.sos.util.Util.toStringValue
import org.msgpack.value.Value
import org.msgpack.value.ValueFactory

/**
 * The event to make a order
 *
 * @author Little Fish
 */
class OrderEvent : Event(EVENT_KEY), EventPusher {
	
	private val items = ArrayList<OrderItem>()
	
	/**
	 * Add an item to items
	 */
	fun addItem(item: OrderItem) {
		items.add(item)
	}
	
	/**
	 * Add all items into items
	 */
	fun addAllItems(i: Iterable<OrderItem>) {
		items.addAll(i)
	}
	
	override fun toValue(): Value {
		val map = HashMap<Value, Value>()
		map[Util.NET_KEY_EVENT.toStringValue()] = EVENT_KEY.toStringValue()
		map[Util.OrderKey.ITEM.toStringValue()] = ValueFactory.newArray(items.map { it.toValue() })
		return ValueFactory.newMap(map)
	}
	
	override fun toString(): String {
		return "order { itemCount=${items.size} }"
	}
	
	companion object {
		const val EVENT_KEY = "order"
	}
	
}