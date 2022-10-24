package edu.nptu.dllab.sos.data

import com.google.gson.JsonElement
import edu.nptu.dllab.sos.util.SOSVersion
import org.msgpack.value.Value

/**
 * The event get from server
 *
 * @author Little Fish
 * @since 22/10/03
 */
@SOSVersion(since = "0.0")
interface EventPuller {
	
	/**
	 * Get event with json
	 */
	@Deprecated("use fromValue(Value)")
	@SOSVersion(since = "0.0")
	fun fromJson(json: JsonElement)
	
	/**
	 * Get event with msgpack
	 */
	@SOSVersion(since = "0.0")
	fun fromValue(value: Value)
	
}