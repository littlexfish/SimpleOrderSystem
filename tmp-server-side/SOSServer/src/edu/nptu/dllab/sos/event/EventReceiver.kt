package edu.nptu.dllab.sos.event

import org.msgpack.value.Value

interface EventReceiver {

	fun fromMsgPack(value: Value)

}