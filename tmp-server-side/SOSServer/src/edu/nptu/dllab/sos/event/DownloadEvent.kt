package edu.nptu.dllab.sos.event

import org.msgpack.value.Value
import org.msgpack.value.ValueFactory

class DownloadEvent : Event("download"), EventSender {
	
	var paths = ArrayList<String>()
	
	override fun toMsgPack(): Value {
		val map = ValueFactory.newMapBuilder()
		map.put(ValueFactory.newString("event"), ValueFactory.newString(event))
		map.put(ValueFactory.newString("path"), ValueFactory.newArray(paths.map { ValueFactory.newString(it) }))
		msgPack = map.build()
		return msgPack!!
	}
	
}