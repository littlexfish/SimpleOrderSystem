package edu.nptu.dllab.sos.data.push

import com.google.gson.JsonElement
import com.google.gson.JsonNull
import edu.nptu.dllab.sos.data.Event
import edu.nptu.dllab.sos.data.EventPusher
import edu.nptu.dllab.sos.util.SOSVersion
import edu.nptu.dllab.sos.util.Util.NET_KEY_EVENT
import edu.nptu.dllab.sos.util.Util.DownloadKey
import edu.nptu.dllab.sos.util.Util.toStringValue
import org.msgpack.value.Value
import org.msgpack.value.ValueFactory

/**
 * The event to request download
 *
 * @author Little Fish
 * @since 22/10/04
 */
@SOSVersion(since = "0.0")
class DownloadRequestEvent : Event(EVENT_KEY), EventPusher {
	
	private val downloadRes = ArrayList<String>()
	
	/**
	 * Add path to download
	 */
	@SOSVersion(since = "0.0")
	fun addPath(path: String) {
		downloadRes.add(path)
	}
	
	/**
	 * Add multi-path
	 */
	@SOSVersion(since = "0.0")
	fun addAllPath(paths: Iterable<String>) {
		downloadRes.addAll(paths)
	}
	
	override fun toValue(): Value {
		val map = HashMap<Value, Value>()
		map[NET_KEY_EVENT.toStringValue()] = EVENT_KEY.toStringValue()
		val paths = ArrayList<Value>(downloadRes.size)
		for(p in downloadRes) paths.add(p.toStringValue())
		map[DownloadKey.PATH.toStringValue()] = ValueFactory.newArray(paths)
		return ValueFactory.newMap(map)
	}
	
	override fun toString(): String {
		return toValue().toString()
	}
	
	companion object {
		const val EVENT_KEY = "download"
	}
	
}