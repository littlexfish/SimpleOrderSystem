package edu.nptu.dllab.sos.data

import org.msgpack.value.Value

/**
 * The event get from server
 *
 * @author Little Fish
 */
interface EventPuller {
	
	/**
	 * Get event with msgpack
	 */
	fun fromValue(value: Value)
	
}