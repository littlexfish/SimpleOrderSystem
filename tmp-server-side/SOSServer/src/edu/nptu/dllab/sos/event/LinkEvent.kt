package edu.nptu.dllab.sos.event

import org.msgpack.value.Value
import org.msgpack.value.ValueFactory

class LinkEvent : Event("link"), EventSender {
	
	var pX = 0.0
	var pY = 0.0
	
	override fun toMsgPack(): Value {
		val pos = ValueFactory.newArray(ValueFactory.newFloat(pX), ValueFactory.newFloat(pY))
		val o = ValueFactory.newMapBuilder()
		o.put(ValueFactory.newString("event"), ValueFactory.newString(event))
		o.put(ValueFactory.newString("position"), pos)
		msgPack = o.build()
		return msgPack!!
	}
	
}