package edu.nptu.dllab.sos.data

import com.google.gson.JsonElement
import edu.nptu.dllab.sos.util.SOSVersion
import org.msgpack.value.Value

/**
 * The event push to server
 *
 * @author Little Fish
 * @since 22/10/03
 */
@SOSVersion(since = "0.0")
interface EventPusher {
	
	/**
	 * Push event with json
	 */
	@SOSVersion(since = "0.0")
	fun toJson(): JsonElement
	
	/**
	 * Push event with msgpack
	 */
	@SOSVersion(since = "0.0")
	fun toValue(): Value
	
}