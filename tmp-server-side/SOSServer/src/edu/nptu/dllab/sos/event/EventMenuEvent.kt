package edu.nptu.dllab.sos.event

import org.msgpack.value.ArrayValue
import org.msgpack.value.Value
import org.msgpack.value.ValueFactory

class EventMenuEvent : Event("event_menu"), EventReceiver {
	
	var menu: ArrayValue? = null
	var resources = ArrayList<Resource>()
	
	override fun fromMsgPack(value: Value) {
		msgPack = value
		val map = value.asMapValue().map()
		menu = map[ValueFactory.newString("menu")]!!.asArrayValue()
		
		val resources = map[ValueFactory.newString("resource")]!!.asArrayValue()
		for(resource in resources) {
			val r = resource.asMapValue().map()
			val path = r[ValueFactory.newString("path")]!!.asStringValue().toString()
			val id = r[ValueFactory.newString("id")]!!.asIntegerValue().toInt()
			val position = r[ValueFactory.newString("position")]!!.asStringValue().toString()
			val sha256 = r[ValueFactory.newString("sha256")]!!.asStringValue().toString()
			this.resources.add(Resource(path, id, position, sha256))
		}
	}
	inner class Resource(val path: String, val id: Int, val position: String, val sha256: String)
}