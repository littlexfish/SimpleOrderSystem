package edu.nptu.dllab.sos.data

import edu.nptu.dllab.sos.util.Util.ResourceKey
import edu.nptu.dllab.sos.util.Util.toIntegerValue
import edu.nptu.dllab.sos.util.Util.toStringValue
import org.msgpack.value.Value
import org.msgpack.value.ValueFactory

/**
 * The resource contains info from server
 *
 * @author Little Fish
 */
data class Resource(val path: String, val id: Int, val position: String, val sha256: String) {
	
	/**
	 * Get msgpack of this resource
	 */
	fun getValue(): Value {
		val map = ValueFactory.newMapBuilder()
		map.put(ResourceKey.PATH.toStringValue(), path.toStringValue())
		map.put(ResourceKey.ID.toStringValue(), id.toIntegerValue())
		map.put(ResourceKey.POSITION.toStringValue(), position.toStringValue())
		map.put(ResourceKey.SHA256.toStringValue(), sha256.toStringValue())
		return map.build()
	}
	
	/**
	 * Check the path is same
	 */
	fun pathEquals(res: Resource): Boolean {
		return path == res.path
	}
	
}
