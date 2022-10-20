package edu.nptu.dllab.sos.event

import org.msgpack.value.Value

interface EventSender {
	
	fun toMsgPack(): Value
	
}