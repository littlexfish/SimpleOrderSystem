package edu.nptu.dllab.sos.event

import org.msgpack.value.MapValue
import org.msgpack.value.Value
import org.msgpack.value.ValueFactory

class UpdateEvent : Event("update"), EventReceiver {
	
	var shopId = -1
	var menuVersion = -1
	var menu: MapValue? = null
	var resources = ArrayList<Resource>()
	
	override fun fromMsgPack(value: Value) {
		msgPack = value
		val map = value.asMapValue().map()
		shopId = map[ValueFactory.newString("shopId")]!!.asIntegerValue().toInt()
		menuVersion = map[ValueFactory.newString("menuVersion")]!!.asIntegerValue().toInt()
		menu = map[ValueFactory.newString("menu")]!!.asMapValue()
		var ress = map[ValueFactory.newString("resource")]!!.asArrayValue()
		for(res in ress) {
			val rMap = res.asMapValue().map()
			val pa = rMap[ValueFactory.newString("path")]!!.asStringValue().toString()
			val id = rMap[ValueFactory.newString("id")]!!.asIntegerValue().toInt()
			val po = rMap[ValueFactory.newString("position")]!!.asStringValue().toString()
			val sha = rMap[ValueFactory.newString("sha256")]!!.asStringValue().toString()
			resources.add(Resource(pa, id, po, sha))
		}
	}
	
	inner class Resource(val path: String, val id: Int, val position: String, val sha256: String)
	
}