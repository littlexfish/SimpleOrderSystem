package edu.nptu.dllab.sos.event

import org.msgpack.value.Value

abstract class Event(val event: String) {
	
	var msgPack: Value? = null
	
}