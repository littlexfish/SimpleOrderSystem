package edu.nptu.dllab.sos.data

import org.msgpack.value.Value

/**
 * The event push to server
 *
 * @author Little Fish
 */
interface EventPusher {
	
	/**
	 * Push event with msgpack
	 */
	fun toValue(): Value
	
}