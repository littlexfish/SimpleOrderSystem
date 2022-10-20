package edu.nptu.dllab.sos.event

import org.msgpack.value.Value
import org.msgpack.value.ValueFactory

class OpenMenuEvent : Event("open_menu"), EventSender {
	
	var shopId = -1
	var menuVersion = -1
	
	override fun toMsgPack(): Value {
		val map = ValueFactory.newMapBuilder()
		map.put(ValueFactory.newString("event"), ValueFactory.newString(event))
		map.put(ValueFactory.newString("shopId"), ValueFactory.newInteger(shopId))
		map.put(ValueFactory.newString("menuVersion"), ValueFactory.newInteger(menuVersion))
		msgPack = map.build()
		return msgPack!!
	}
}