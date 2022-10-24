package edu.nptu.dllab.sos.data

import edu.nptu.dllab.sos.util.SOSVersion
import edu.nptu.dllab.sos.util.Util.ResourceKey.*
import edu.nptu.dllab.sos.util.Util.toIntegerValue
import edu.nptu.dllab.sos.util.Util.toStringValue
import org.msgpack.value.Value
import org.msgpack.value.ValueFactory

/**
 * The resource contains info from server
 *
 * @author Little Fish
 * @since 22/10/03
 */
@SOSVersion(since = "0.0")
data class Resource(val path: String, val id: Int, val position: String, val sha256: String) {
	
	/**
	 * Get msgpack of this resource
	 */
	@SOSVersion(since = "0.0")
	fun getValue(): Value {
		val map = ValueFactory.newMapBuilder()
		map.put(PATH.key.toStringValue(), path.toStringValue())
		map.put(ID.key.toStringValue(), id.toIntegerValue())
		map.put(POSITION.key.toStringValue(), position.toStringValue())
		map.put(SHA256.key.toStringValue(), sha256.toStringValue())
		return map.build()
	}
	
	/**
	 * Check the path is same
	 */
	@SOSVersion(since = "0.0")
	fun pathEquals(res: Resource): Boolean {
		return path == res.path
	}
	
}
