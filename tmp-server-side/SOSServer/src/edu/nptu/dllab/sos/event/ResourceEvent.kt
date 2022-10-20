package edu.nptu.dllab.sos.event

import org.msgpack.value.Value
import org.msgpack.value.ValueFactory

class ResourceEvent : Event("resource"), EventReceiver {
	
	var file = -1
	var total = -1
	var path = ""
	var sha256 = ""
	var data = ByteArray(0)
	
	override fun fromMsgPack(value: Value) {
		msgPack = value
		val map = value.asMapValue().map()
		file = map[ValueFactory.newString("file")]!!.asIntegerValue().toInt()
		total = map[ValueFactory.newString("total")]!!.asIntegerValue().toInt()
		path = map[ValueFactory.newString("file")]!!.asStringValue().toString()
		sha256 = map[ValueFactory.newString("file")]!!.asStringValue().toString()
		data = map[ValueFactory.newString("file")]!!.asBinaryValue().asByteArray()
	}
	
}