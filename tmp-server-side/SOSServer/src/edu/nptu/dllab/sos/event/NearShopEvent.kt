package edu.nptu.dllab.sos.event

import org.msgpack.value.Value
import org.msgpack.value.ValueFactory

class NearShopEvent : Event("near_shop"), EventReceiver {
	
	var shops = ArrayList<Shop>()
	
	override fun fromMsgPack(value: Value) {
		msgPack = value
		val map = value.asMapValue().map()
		val shops = map[ValueFactory.newString("shop")]!!.asArrayValue()
		for(shop in shops) {
			val sMap = shop.asMapValue().map()
			val id = sMap[ValueFactory.newString("shopId")]!!.asIntegerValue().toInt()
			val pA = sMap[ValueFactory.newString("position")]!!.asArrayValue()
			val pX = pA[0].asFloatValue().toDouble()
			val pY = pA[1].asFloatValue().toDouble()
			val st = sMap[ValueFactory.newString("state")]!!.asStringValue().toString()
			this.shops.add(Shop(id, pX, pY, st))
		}
	}
	
	inner class Shop(val shopId: Int, val pX: Double, val pY: Double, val state: String)
	
}